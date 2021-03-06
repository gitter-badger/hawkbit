/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.artifacts.smtype;

import javax.annotation.PreDestroy;

import org.eclipse.hawkbit.ui.artifacts.event.SoftwareModuleTypeEvent;
import org.eclipse.hawkbit.ui.artifacts.event.UploadArtifactUIEvent;
import org.eclipse.hawkbit.ui.artifacts.event.UploadViewAcceptCriteria;
import org.eclipse.hawkbit.ui.artifacts.state.ArtifactUploadState;
import org.eclipse.hawkbit.ui.common.SoftwareModuleTypeBeanQuery;
import org.eclipse.hawkbit.ui.common.filterlayout.AbstractFilterButtonClickBehaviour;
import org.eclipse.hawkbit.ui.common.filterlayout.AbstractFilterButtons;
import org.eclipse.hawkbit.ui.utils.HawkbitCommonUtil;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

/**
 * Software module type filter buttons.
 * 
 *
 * 
 */
@SpringComponent
@ViewScope
public class SMTypeFilterButtons extends AbstractFilterButtons {

    private static final long serialVersionUID = 169198312654380358L;

    @Autowired
    private ArtifactUploadState artifactUploadState;

    @Autowired
    private UploadViewAcceptCriteria uploadViewAcceptCriteria;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    /**
     * Initialize component.
     * 
     * @param filterButtonClickBehaviour
     *            the clickable behaviour.
     */
    public void init(final AbstractFilterButtonClickBehaviour filterButtonClickBehaviour) {
        super.init(filterButtonClickBehaviour);
        eventBus.subscribe(this);
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final SoftwareModuleTypeEvent event) {
        if (event.getSoftwareModuleTypeEnum() == SoftwareModuleTypeEvent.SoftwareModuleTypeEnum.ADD_SOFTWARE_MODULE_TYPE
                || event.getSoftwareModuleTypeEnum() == SoftwareModuleTypeEvent.SoftwareModuleTypeEnum.UPDATE_SOFTWARE_MODULE_TYPE
                || event.getSoftwareModuleTypeEnum() == SoftwareModuleTypeEvent.SoftwareModuleTypeEnum.DELETE_SOFTWARE_MODULE_TYPE
                        && event.getSoftwareModuleType() != null) {
            refreshTable();
        }
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final UploadArtifactUIEvent event) {
        if (event == UploadArtifactUIEvent.DELETED_ALL_SOFWARE_TYPE) {
            refreshTable();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.layouts.SPFilterButtons#getButtonsTableId()
     */
    @Override
    protected String getButtonsTableId() {
        return SPUIComponetIdProvider.SW_MODULE_TYPE_TABLE_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.layouts.SPFilterButtons#
     * createButtonsLazyQueryContainer()
     */
    @Override
    protected LazyQueryContainer createButtonsLazyQueryContainer() {
        return HawkbitCommonUtil.createLazyQueryContainer(
                new BeanQueryFactory<SoftwareModuleTypeBeanQuery>(SoftwareModuleTypeBeanQuery.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see hawkbit.server.ui.layouts.SPFilterButtons#isClickedByDefault(java.
     * lang.Long)
     */
    @Override
    protected boolean isClickedByDefault(final Long buttonId) {
        return artifactUploadState.getSoftwareModuleFilters().getSoftwareModuleType().isPresent() && artifactUploadState
                .getSoftwareModuleFilters().getSoftwareModuleType().get().getId().equals(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hawkbit.server.ui.layouts.SPFilterButtons#createButtonId(java.lang.
     * String)
     */
    @Override
    protected String createButtonId(final String name) {
        return SPUIComponetIdProvider.SM_TYPE_FILTER_BTN_ID + name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.common.filterlayout.SPFilterButtons#
     * getFilterButtonDropHandler()
     */
    @Override
    protected DropHandler getFilterButtonDropHandler() {
        return new DropHandler() {
            private static final long serialVersionUID = 1L;

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return uploadViewAcceptCriteria;
            }

            @Override
            public void drop(final DragAndDropEvent event) {
                /* Not required */
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * getButttonWrapperId()
     */
    @Override
    protected String getButttonWrapperIdPrefix() {
        return SPUIComponetIdProvider.UPLOAD_TYPE_BUTTON_PREFIX;
    }

    @Override
    protected String getButtonWrapperData() {
        return null;
    }

}
