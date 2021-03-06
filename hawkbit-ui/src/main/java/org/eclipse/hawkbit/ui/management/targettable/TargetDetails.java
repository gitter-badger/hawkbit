/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.management.targettable;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.hawkbit.repository.SpPermissionChecker;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.SoftwareModule;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.ui.common.detailslayout.AbstractTableDetailsLayout;
import org.eclipse.hawkbit.ui.common.tagdetails.TargetTagToken;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.management.event.TargetTableEvent;
import org.eclipse.hawkbit.ui.management.event.TargetTableEvent.TargetComponentEvent;
import org.eclipse.hawkbit.ui.management.state.ManagementUIState;
import org.eclipse.hawkbit.ui.utils.HawkbitCommonUtil;
import org.eclipse.hawkbit.ui.utils.I18N;
import org.eclipse.hawkbit.ui.utils.SPDateTimeUtil;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.eclipse.hawkbit.ui.utils.SPUIDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Target details layout.
 * 
 *
 *
 */
@SpringComponent
@ViewScope
public class TargetDetails extends AbstractTableDetailsLayout {

    private static final long serialVersionUID = 4571732743399605843L;

    @Autowired
    private I18N i18n;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    @Autowired
    private SpPermissionChecker permChecker;

    @Autowired
    private ManagementUIState managementUIState;

    @Autowired
    private TargetAddUpdateWindowLayout targetAddUpdateWindowLayout;

    @Autowired
    private TargetTagToken targetTagToken;

    private Target selectedTarget = null;

    private VerticalLayout assignedDistLayout;
    private VerticalLayout installedDistLayout;

    /**
     * Initialize the Target details.
     */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        targetAddUpdateWindowLayout.init();
        eventBus.subscribe(this);
    }

    @Override
    protected String getDefaultCaption() {
        return i18n.get("target.details.header");
    }

    @Override
    protected void addTabs(final TabSheet detailsTab) {
        detailsTab.addTab(createDetailsLayout(), i18n.get("caption.tab.details"), null);
        detailsTab.addTab(createDescriptionLayout(), i18n.get("caption.tab.description"), null);
        detailsTab.addTab(createAttributesLayout(), i18n.get("caption.attributes.tab"), null);
        detailsTab.addTab(createAssignedDistLayout(), i18n.get("header.target.assigned"), null);
        detailsTab.addTab(createInstalledDistLayout(), i18n.get("header.target.installed"), null);
        detailsTab.addTab(createTagsLayout(), i18n.get("caption.tags.tab"), null);
        detailsTab.addTab(createLogLayout(), i18n.get("caption.logs.tab"), null);

    }

    private Component createInstalledDistLayout() {
        installedDistLayout = getTabLayout();
        return installedDistLayout;
    }

    private Component createAssignedDistLayout() {
        assignedDistLayout = getTabLayout();
        return assignedDistLayout;
    }

    private VerticalLayout createTagsLayout() {
        final VerticalLayout tagsLayout = getTabLayout();
        tagsLayout.addComponent(targetTagToken.getTokenField());
        return tagsLayout;
    }

    @Override
    protected void onEdit(final ClickEvent event) {
        if (selectedTarget != null) {
            final Window newDistWindow = targetAddUpdateWindowLayout.getWindow();
            targetAddUpdateWindowLayout.populateValuesOfTarget(selectedTarget.getControllerId());
            newDistWindow.setCaption(i18n.get("caption.update.dist"));
            UI.getCurrent().addWindow(newDistWindow);
            newDistWindow.setVisible(Boolean.TRUE);
        }
    }

    @Override
    protected String getEditButtonId() {
        return SPUIComponetIdProvider.TARGET_EDIT_ICON;
    }

    @Override
    protected Boolean onLoadIsTableRowSelected() {
        return managementUIState.getLastSelectedTargetIdName() != null;
    }

    @Override
    protected Boolean onLoadIsTableMaximized() {
        return managementUIState.isTargetTableMaximized();
    }

    @Override
    protected void populateDetailsWidget() {
        if (selectedTarget != null) {
            setName(getDefaultCaption(), selectedTarget.getName());
        }
        populateDetailsWidget(selectedTarget);
    }

    private void populateDetailsWidget(final Target target) {
        if (target != null) {
            setName(getDefaultCaption(), target.getName());
            updateDetailsLayout(target.getControllerId(), target.getTargetInfo().getAddress(),
                    target.getSecurityToken(),
                    SPDateTimeUtil.getFormattedDate(target.getTargetInfo().getLastTargetQuery()));
            populateDescription(target);
            populateDistributionDtls(installedDistLayout, target.getTargetInfo().getInstalledDistributionSet());
            populateDistributionDtls(assignedDistLayout, target.getAssignedDistributionSet());
            updateLogLayout(getLogLayout(), target.getLastModifiedAt(), target.getLastModifiedBy(),
                    target.getCreatedAt(), target.getCreatedBy(), i18n);
            populateAttributes(target);
        } else {
            setName(getDefaultCaption(), HawkbitCommonUtil.SP_STRING_EMPTY);
            updateDetailsLayout(null, null, null, null);
            populateDescription(null);
            populateDistributionDtls(installedDistLayout, null);
            populateDistributionDtls(assignedDistLayout, null);
            updateLogLayout(getLogLayout(), null, HawkbitCommonUtil.SP_STRING_EMPTY, null, null, i18n);
            populateAttributes(null);
        }
    }

    private void populateAttributes(final Target target) {
        if (target != null) {

            updateAttributesLayout(target);
        } else {
            updateAttributesLayout(null);
        }
    }

    private void populateDescription(final Target target) {
        if (target != null) {
            updateDescriptionLayout(i18n.get("label.description"), target.getDescription());
        } else {
            updateDescriptionLayout(i18n.get("label.description"), null);
        }
    }

    private void updateDetailsLayout(final String controllerId, final URI address, final String securityToken,
            final String lastQueryDate) {
        final VerticalLayout detailsTabLayout = getDetailsLayout();
        detailsTabLayout.removeAllComponents();

        final Label controllerLabel = SPUIComponentProvider.createNameValueLabel(i18n.get("label.target.id"),
                HawkbitCommonUtil.trimAndNullIfEmpty(controllerId) == null ? "" : controllerId);
        controllerLabel.setId(SPUIComponetIdProvider.TARGET_CONTROLLER_ID);
        detailsTabLayout.addComponent(controllerLabel);

        final Label lastPollDtLabel = SPUIComponentProvider.createNameValueLabel(i18n.get("label.target.lastpolldate"),
                HawkbitCommonUtil.trimAndNullIfEmpty(lastQueryDate) == null ? "" : lastQueryDate);
        lastPollDtLabel.setId(SPUIComponetIdProvider.TARGET_LAST_QUERY_DT);
        detailsTabLayout.addComponent(lastPollDtLabel);

        final Label typeLabel = SPUIComponentProvider.createNameValueLabel(i18n.get("label.ip"),
                address == null ? StringUtils.EMPTY : address.toString());
        typeLabel.setId(SPUIComponetIdProvider.TARGET_IP_ADDRESS);
        detailsTabLayout.addComponent(typeLabel);

        if (securityToken != null) {
            final HorizontalLayout securityTokenLayout = getSecurityTokenLayout(securityToken);
            controllerLabel.setId(SPUIComponetIdProvider.TARGET_SECURITY_TOKEN);
            detailsTabLayout.addComponent(securityTokenLayout);
        }
    }

    private HorizontalLayout getSecurityTokenLayout(final String securityToken) {
        final HorizontalLayout securityTokenLayout = new HorizontalLayout();

        final Label securityTableLbl = new Label(
                SPUIComponentProvider.getBoldHTMLText(i18n.get("label.target.security.token")), ContentMode.HTML);
        securityTableLbl.addStyleName(SPUIDefinitions.TEXT_STYLE);
        securityTableLbl.addStyleName("label-style");

        final TextField securityTokentxt = new TextField();
        securityTokentxt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        securityTokentxt.addStyleName(ValoTheme.TEXTFIELD_TINY);
        securityTokentxt.addStyleName("targetDtls-securityToken");
        securityTokentxt.addStyleName(SPUIDefinitions.TEXT_STYLE);
        securityTokentxt.setCaption(null);
        securityTokentxt.setValue(securityToken);
        securityTokentxt.setReadOnly(true);

        securityTokenLayout.addComponent(securityTableLbl);
        securityTokenLayout.addComponent(securityTokentxt);
        return securityTokenLayout;
    }

    private void populateDistributionDtls(final VerticalLayout layout, final DistributionSet distributionSet) {
        layout.removeAllComponents();
        if (distributionSet != null) {
            // Display distribution set name
            layout.addComponent(SPUIComponentProvider.createNameValueLabel(i18n.get("label.dist.details.name"),
                    distributionSet.getName()));

            layout.addComponent(SPUIComponentProvider.createNameValueLabel(i18n.get("label.dist.details.version"),
                    distributionSet.getVersion()));

            /* Module info */
            distributionSet.getModules()
                    .forEach(module -> layout.addComponent(getSWModlabel(module.getType().getName(), module)));
        }
    }

    /**
     * Create Label for SW Module.
     * 
     * @param labelName
     *            as Name
     * @param swModule
     *            as Module (JVM|OS|AH)
     * @return Label as UI
     */
    private Label getSWModlabel(final String labelName, final SoftwareModule swModule) {
        return SPUIComponentProvider.createNameValueLabel(labelName + " : ", swModule.getName(), swModule.getVersion());
    }

    @Override
    protected void clearDetails() {
        populateDetailsWidget(null);
    }

    @Override
    protected Boolean hasEditPermission() {
        return permChecker.hasUpdateTargetPermission();
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final TargetTableEvent targetTableEvent) {
        // Get the event type
        final TargetComponentEvent event = targetTableEvent.getTargetComponentEvent();

        if (event == TargetComponentEvent.SELECTED_TARGET || event == TargetComponentEvent.EDIT_TARGET) {
            UI.getCurrent().access(() -> {
                // If selected or edited, populate the fresh details.
                if (targetTableEvent.getTarget() != null) {
                    selectedTarget = targetTableEvent.getTarget();
                } else {
                    selectedTarget = null;
                }
                populateData(selectedTarget != null);

            });
        } else if (event == TargetComponentEvent.MINIMIZED) {
            UI.getCurrent().access(() -> showLayout());
        } else if (event == TargetComponentEvent.MAXIMIZED) {
            UI.getCurrent().access(() -> hideLayout());
        }
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }

    @Override
    protected String getTabSheetId() {
        return SPUIComponetIdProvider.TARGET_DETAILS_TABSHEET;
    }

    @Override
    protected String getDetailsHeaderCaptionId() {
        return SPUIComponetIdProvider.TARGET_DETAILS_HEADER_LABEL_ID;
    }

}
