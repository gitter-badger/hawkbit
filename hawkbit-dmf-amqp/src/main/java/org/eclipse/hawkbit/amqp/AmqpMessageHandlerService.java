/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.amqp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.hawkbit.api.HostnameResolver;
import org.eclipse.hawkbit.artifact.repository.model.DbArtifact;
import org.eclipse.hawkbit.artifact.repository.model.DbArtifactHash;
import org.eclipse.hawkbit.cache.CacheConstants;
import org.eclipse.hawkbit.cache.DownloadArtifactCache;
import org.eclipse.hawkbit.cache.DownloadType;
import org.eclipse.hawkbit.dmf.amqp.api.EventTopic;
import org.eclipse.hawkbit.dmf.amqp.api.MessageHeaderKey;
import org.eclipse.hawkbit.dmf.amqp.api.MessageType;
import org.eclipse.hawkbit.dmf.json.model.ActionUpdateStatus;
import org.eclipse.hawkbit.dmf.json.model.Artifact;
import org.eclipse.hawkbit.dmf.json.model.ArtifactHash;
import org.eclipse.hawkbit.dmf.json.model.DownloadResponse;
import org.eclipse.hawkbit.dmf.json.model.TenantSecruityToken;
import org.eclipse.hawkbit.eventbus.event.TargetAssignDistributionSetEvent;
import org.eclipse.hawkbit.im.authentication.SpPermission.SpringEvalExpressions;
import org.eclipse.hawkbit.im.authentication.TenantAwareAuthenticationDetails;
import org.eclipse.hawkbit.repository.ArtifactManagement;
import org.eclipse.hawkbit.repository.ControllerManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.Action.Status;
import org.eclipse.hawkbit.repository.model.ActionStatus;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.LocalArtifact;
import org.eclipse.hawkbit.repository.model.SoftwareModule;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.eventbus.EventBus;

/**
 *
 * {@link AmqpMessageHandlerService} handles all incoming AMQP messages.
 *
 *
 *
 *
 */
public class AmqpMessageHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpMessageHandlerService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ControllerManagement controllerManagement;

    @Autowired
    private AmqpControllerAuthentfication authenticationManager;

    @Autowired
    private ArtifactManagement artifactManagement;

    @Autowired
    private EventBus eventBus;

    @Autowired
    @Qualifier(CacheConstants.DOWNLOAD_ID_CACHE)
    private Cache cache;

    @Autowired
    private HostnameResolver hostnameResolver;

    /**
     * /** Method to handle all incoming amqp messages.
     *
     * @param message
     *            incoming message
     * @param type
     *            the message type
     * @param contentType
     *            the contentType of the message
     * @param tenant
     *            the contentType of the message
     * @return a message if <null> no message is send back to sender
     */
    @RabbitListener(queues = "${hawkbit.dmf.rabbitmq.receiverQueue}", containerFactory = "listenerContainerFactory")
    public Message onMessage(final Message message, @Header(MessageHeaderKey.TYPE) final String type,
            @Header(MessageHeaderKey.TENANT) final String tenant) {
        checkContentTypeJson(message);
        final SecurityContext oldContext = SecurityContextHolder.getContext();
        try {
            final MessageType messageType = MessageType.valueOf(type);
            switch (messageType) {
            case THING_CREATED:
                setTenantSecurityContext(tenant);
                registerTarget(message);
                break;
            case EVENT:
                setTenantSecurityContext(tenant);
                final String topicValue = getStringHeaderKey(message, MessageHeaderKey.TOPIC, "EventTopic is null");
                final EventTopic eventTopic = EventTopic.valueOf(topicValue);
                handleIncomingEvent(message, eventTopic);
                break;
            case AUTHENTIFICATION:
                return handleAuthentifiactionMessage(message);
            default:
                logAndThrowMessageError(message, "No handle method was found for the given message type.");
            }
        } finally {
            SecurityContextHolder.setContext(oldContext);
        }
        return null;
    }

    private Message handleAuthentifiactionMessage(final Message message) {
        final DownloadResponse authentificationResponse = new DownloadResponse();
        final MessageProperties messageProperties = message.getMessageProperties();
        final TenantSecruityToken secruityToken = convertMessage(message, TenantSecruityToken.class);
        final String sha1 = secruityToken.getSha1();
        try {
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.doAuthenticate(secruityToken));
            final LocalArtifact localArtifact = artifactManagement
                    .findFirstLocalArtifactsBySHA1(secruityToken.getSha1());
            if (localArtifact == null) {
                throw new EntityNotFoundException();
            }

            // check action for this download purposes, the method will throw an
            // EntityNotFoundException in case the controller is not allowed to
            // download this file
            // because it's not assigned to an action and not assigned to this
            // controller.
            final Action action = controllerManagement.getActionForDownloadByTargetAndSoftwareModule(
                    secruityToken.getControllerId(), localArtifact.getSoftwareModule());
            LOG.info("Found action for download authentication request action: {}, sha1: {}", action,
                    secruityToken.getSha1());

            final Artifact artifact = convertDbArtifact(artifactManagement.loadLocalArtifactBinary(localArtifact));
            if (artifact == null) {
                throw new EntityNotFoundException();
            }
            authentificationResponse.setArtifact(artifact);
            final String downloadId = UUID.randomUUID().toString();
            final DownloadArtifactCache downloadCache = new DownloadArtifactCache(DownloadType.BY_SHA1, sha1);
            cache.put(downloadId, downloadCache);
            authentificationResponse
                    .setDownloadUrl(UriComponentsBuilder.fromUri(hostnameResolver.resolveHostname().toURI())
                            .path("/api/v1/downloadserver/downloadId/").path(downloadId).build().toUriString());
            authentificationResponse.setResponseCode(HttpStatus.OK.value());
        } catch (final BadCredentialsException | AuthenticationServiceException | CredentialsExpiredException e) {
            LOG.error("Login failed", e);
            authentificationResponse.setResponseCode(HttpStatus.FORBIDDEN.value());
            authentificationResponse.setMessage("Login failed");
        } catch (final URISyntaxException e) {
            LOG.error("URI build exception", e);
            authentificationResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            authentificationResponse.setMessage("Building download URI failed");
        } catch (final EntityNotFoundException e) {
            final String errorMessage = "Artifact with sha1 " + sha1 + "not found ";
            LOG.warn(errorMessage, e);
            authentificationResponse.setResponseCode(HttpStatus.NOT_FOUND.value());
            authentificationResponse.setMessage(errorMessage);
        }

        return rabbitTemplate.getMessageConverter().toMessage(authentificationResponse, messageProperties);
    }

    private static Artifact convertDbArtifact(final DbArtifact dbArtifact) {
        final Artifact artifact = new Artifact();
        artifact.setSize(dbArtifact.getSize());
        final DbArtifactHash dbArtifactHash = dbArtifact.getHashes();
        artifact.setHashes(new ArtifactHash(dbArtifactHash.getSha1(), dbArtifactHash.getMd5()));
        return artifact;
    }

    protected void logAndThrowMessageError(final Message message, final String error) {
        LOG.error("Error \"{}\" reported by message {}", error, message.getMessageProperties().getMessageId());
        throw new IllegalArgumentException(error);
    }

    private static void setSecurityContext(final Authentication authentication) {
        final SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        securityContextImpl.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContextImpl);
    }

    private static void setTenantSecurityContext(final String tenantId) {
        final AnonymousAuthenticationToken authenticationToken = new AnonymousAuthenticationToken(
                UUID.randomUUID().toString(), "AMQP-Controller",
                Collections.singletonList(new SimpleGrantedAuthority(SpringEvalExpressions.CONTROLLER_ROLE_ANONYMOUS)));
        authenticationToken.setDetails(new TenantAwareAuthenticationDetails(tenantId, true));
        setSecurityContext(authenticationToken);
    }

    private String getStringHeaderKey(final Message message, final String key, final String errorMessageIfNull) {
        final Map<String, Object> header = message.getMessageProperties().getHeaders();
        final Object value = header.get(key);
        if (value == null) {
            logAndThrowMessageError(message, errorMessageIfNull);
        }
        return value.toString();
    }

    /**
     * Method to create a new target or to find the target if it already exists.
     *
     * @param targetID
     *            the ID of the target/thing
     * @param ip
     *            the ip of the target/thing
     */
    private void registerTarget(final Message message) {
        final String thingId = getStringHeaderKey(message, MessageHeaderKey.THING_ID, "ThingId is null");
        final String replyTo = message.getMessageProperties().getReplyTo();

        if (StringUtils.isEmpty(replyTo)) {
            logAndThrowMessageError(message, "No ReplyTo was set for the createThing Event.");
        }
        final URI amqpUri = IpUtil.createAmqpUri(replyTo);
        final Target target = controllerManagement.findOrRegisterTargetIfItDoesNotexist(thingId, amqpUri);
        LOG.debug("Target {} reported online state.", thingId);

        lookIfUpdateAvailable(target);
    }

    private void lookIfUpdateAvailable(final Target target) {
        final List<Action> actions = controllerManagement.findActionByTargetAndActive(target);
        if (actions.isEmpty()) {
            return;
        }
        // action are ordered by ASC
        final Action action = actions.get(0);
        final DistributionSet distributionSet = action.getDistributionSet();
        final List<SoftwareModule> softwareModuleList = controllerManagement
                .findSoftwareModulesByDistributionSet(distributionSet);
        eventBus.post(new TargetAssignDistributionSetEvent(target.getControllerId(), action.getId(), softwareModuleList,
                target.getTargetInfo().getAddress()));

    }

    /**
     * Method to handle the different topics to an event.
     *
     * @param message
     *            the incoming event message.
     * @param topic
     *            the topic of the event.
     */
    private void handleIncomingEvent(final Message message, final EventTopic topic) {
        switch (topic) {
        case UPDATE_ACTION_STATUS:
            updateActionStatus(message);
            return;
        default:
            logAndThrowMessageError(message, "Got event without appropriate topic.");
        }
    }

    /**
     * Method to update the action status of an action through the event.
     *
     * @param actionUpdateStatus
     *            the object form the ampq message
     */
    private void updateActionStatus(final Message message) {
        final ActionUpdateStatus actionUpdateStatus = convertMessage(message, ActionUpdateStatus.class);
        final Long actionId = actionUpdateStatus.getActionId();
        LOG.debug("Target notifies intermediate about action {} with status {}.", actionId,
                actionUpdateStatus.getActionStatus().name());

        if (actionId == null) {
            logAndThrowMessageError(message, "Invalid message no action id");
        }

        final Action action = controllerManagement.findActionWithDetails(actionId);

        if (action == null) {
            logAndThrowMessageError(message,
                    "Got intermediate notification about action " + actionId + " but action does not exist");
        }

        final ActionStatus actionStatus = new ActionStatus();
        final List<String> messageText = actionUpdateStatus.getMessage();
        final String messageString = String.join(", ", messageText);
        actionStatus.addMessage(messageString);
        actionStatus.setAction(action);
        actionStatus.setOccurredAt(System.currentTimeMillis());

        switch (actionUpdateStatus.getActionStatus()) {
        case DOWNLOAD:
            actionStatus.setStatus(Status.DOWNLOAD);
            break;
        case RETRIEVED:
            actionStatus.setStatus(Status.RETRIEVED);
            break;
        case RUNNING:
            actionStatus.setStatus(Status.RUNNING);
            break;
        case CANCELED:
            actionStatus.setStatus(Status.CANCELED);
            break;
        case FINISHED:
            actionStatus.setStatus(Status.FINISHED);
            break;
        case ERROR:
            actionStatus.setStatus(Status.ERROR);
            break;
        case WARNING:
            actionStatus.setStatus(Status.WARNING);
            break;
        case CANCEL_REJECTED:
            handleCancelRejected(message, action, actionStatus);
            break;
        default:
            logAndThrowMessageError(message, "Status for action does not exisit.");
        }

        Action addUpdateActionStatus;

        if (!actionStatus.getStatus().equals(Status.CANCELED)) {
            addUpdateActionStatus = controllerManagement.addUpdateActionStatus(actionStatus, action);
        } else {
            addUpdateActionStatus = controllerManagement.addCancelActionStatus(actionStatus, action);
        }

        if (!addUpdateActionStatus.isActive()) {
            lookIfUpdateAvailable(action.getTarget());
        }
    }

    private void handleCancelRejected(final Message message, final Action action, final ActionStatus actionStatus) {
        if (action.isCancelingOrCanceled()) {

            actionStatus.setStatus(Status.WARNING);

            // cancel action rejected, write warning status message and fall
            // back to running action status

        } else {
            logAndThrowMessageError(message,
                    "Cancel Recjected message is not allowed, if action is on state: " + action.getStatus());
        }
    }

    /**
     * Is needed to convert a incoming message to is originally object type.
     *
     * @param message
     *            the message to convert.
     * @param clazz
     *            the class of the originally object.
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T convertMessage(final Message message, final Class<T> clazz) {
        message.getMessageProperties().getHeaders().put(AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME,
                clazz.getTypeName());
        return (T) rabbitTemplate.getMessageConverter().fromMessage(message);
    }

    /**
     * Is needed to verify if an incoming message has the content type json.
     *
     * @param message
     *            the to verify
     * @param contentType
     *            the content type
     * @return true if the content type has json, false it not.
     */

    private static void checkContentTypeJson(final Message message) {
        final MessageProperties messageProperties = message.getMessageProperties();
        if (messageProperties.getContentType() != null && messageProperties.getContentType().contains("json")) {
            return;
        }
        throw new IllegalArgumentException("Content-Type is not JSON compatible");
    }

    void setControllerManagement(final ControllerManagement controllerManagement) {
        this.controllerManagement = controllerManagement;
    }

    void setHostnameResolver(final HostnameResolver hostnameResolver) {
        this.hostnameResolver = hostnameResolver;
    }

    void setRabbitTemplate(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    MessageConverter getMessageConverter() {
        return rabbitTemplate.getMessageConverter();
    }

    void setAuthenticationManager(final AmqpControllerAuthentfication authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    void setArtifactManagement(final ArtifactManagement artifactManagement) {
        this.artifactManagement = artifactManagement;
    }

    void setCache(final Cache cache) {
        this.cache = cache;
    }

    void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

}
