/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.distributions.smtype;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.eclipse.hawkbit.ui.artifacts.event.SoftwareModuleTypeEvent;
import org.eclipse.hawkbit.ui.common.SoftwareModuleTypeBeanQuery;
import org.eclipse.hawkbit.ui.common.filterlayout.AbstractFilterButtonClickBehaviour;
import org.eclipse.hawkbit.ui.common.filterlayout.AbstractFilterButtons;
import org.eclipse.hawkbit.ui.distributions.event.DistributionsViewAcceptCriteria;
import org.eclipse.hawkbit.ui.distributions.event.SaveActionWindowEvent;
import org.eclipse.hawkbit.ui.distributions.state.ManageDistUIState;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.eclipse.hawkbit.ui.utils.SPUIDefinitions;
import org.eclipse.hawkbit.ui.utils.SPUILabelDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

/**
 * Software Module Type filter buttons.
 * 
 *
 * 
 */
@SpringComponent
@ViewScope
public class DistSMTypeFilterButtons extends AbstractFilterButtons {

    private static final long serialVersionUID = 6804534533362387433L;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    @Autowired
    private ManageDistUIState manageDistUIState;

    @Autowired
    private DistributionsViewAcceptCriteria distributionsViewAcceptCriteria;

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * getButtonsTableId()
     */
    @Override
    protected String getButtonsTableId() {

        return SPUIComponetIdProvider.SW_MODULE_TYPE_TABLE_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * createButtonsLazyQueryContainer ()
     */
    @Override
    protected LazyQueryContainer createButtonsLazyQueryContainer() {
        final Map<String, Object> queryConfig = new HashMap<String, Object>();
        final BeanQueryFactory<SoftwareModuleTypeBeanQuery> typeQF = new BeanQueryFactory<SoftwareModuleTypeBeanQuery>(
                SoftwareModuleTypeBeanQuery.class);
        typeQF.setQueryConfiguration(queryConfig);
        final LazyQueryContainer lazyQueryContainer = new LazyQueryContainer(
                new LazyQueryDefinition(true, 20, SPUILabelDefinitions.VAR_NAME), typeQF);
        return lazyQueryContainer;
    }

    @Override
    protected String getButtonWrapperData() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * isClickedByDefault(java.lang .Long)
     */
    @Override
    protected boolean isClickedByDefault(final Long buttonId) {

        return manageDistUIState.getSoftwareModuleFilters().getSoftwareModuleType().isPresent()
                && manageDistUIState.getSoftwareModuleFilters().getSoftwareModuleType().get().getId().equals(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * createButtonId(java.lang. String)
     */
    @Override
    protected String createButtonId(final String name) {
        return SPUIComponetIdProvider.SM_TYPE_FILTER_BTN_ID + name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.filterlayout.AbstractFilterButtons#
     * getFilterButtonDropHandler()
     */
    @Override
    protected DropHandler getFilterButtonDropHandler() {

        return new DropHandler() {
            private static final long serialVersionUID = 1L;

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return distributionsViewAcceptCriteria;
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

        return SPUIDefinitions.SOFTWARE_MODULE_TAG_ID_PREFIXS;
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final SoftwareModuleTypeEvent event) {
        if (event.getSoftwareModuleTypeEnum() == SoftwareModuleTypeEvent.SoftwareModuleTypeEnum.ADD_SOFTWARE_MODULE_TYPE
                || event.getSoftwareModuleTypeEnum() == SoftwareModuleTypeEvent.SoftwareModuleTypeEnum.UPDATE_SOFTWARE_MODULE_TYPE
                        && event.getSoftwareModuleType() != null) {
            refreshTypeTable();
        }
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final SaveActionWindowEvent event) {
        if (event == SaveActionWindowEvent.SAVED_DELETE_SW_MODULE_TYPES) {
            refreshTypeTable();
        }
    }

    private void refreshTypeTable() {
        setContainerDataSource(createButtonsLazyQueryContainer());
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }

}
