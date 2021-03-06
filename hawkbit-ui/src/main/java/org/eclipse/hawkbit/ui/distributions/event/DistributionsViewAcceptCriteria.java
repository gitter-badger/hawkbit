/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.distributions.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.hawkbit.ui.common.AbstractAcceptCriteria;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.eclipse.hawkbit.ui.utils.SPUIDefinitions;
import org.eclipse.hawkbit.ui.utils.SPUILabelDefinitions;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;

/**
 * Distributions View for Accept criteria.
 * 
 *
 * 
 */
@SpringComponent
@ViewScope
public class DistributionsViewAcceptCriteria extends AbstractAcceptCriteria {

    private static final long serialVersionUID = -7686564967583118935L;

    private static final Map<String, Object> DROP_HINTS_CONFIGS = createDropHintConfigurations();

    private static final Map<String, List<String>> DROP_CONFIGS = createDropConfigurations();

    @Autowired
    private transient UINotification uiNotification;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    /*
     * (non-Javadoc)
     * 
     * @see hawkbit.server.ui.common.AbstractAcceptCriteria#analyseDragComponent
     * (com.vaadin.event .dd.DragAndDropEvent, com.vaadin.ui.Component)
     */
    @Override
    protected void analyseDragComponent(final Component compsource) {
        final String sourceID = getComponentId(compsource);
        final Object event = DROP_HINTS_CONFIGS.get(sourceID);
        eventBus.publish(this, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.AbstractAcceptCriteria#hideDropHints
     * ()
     */
    @Override
    protected void hideDropHints() {
        eventBus.publish(this, DragEvent.HIDE_DROP_HINT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.common.AbstractAcceptCriteria#invalidDrop()
     */
    @Override
    protected void invalidDrop() {
        uiNotification.displayValidationError(SPUILabelDefinitions.ACTION_NOT_ALLOWED);
    }

    @Override
    protected String getComponentId(final Component component) {
        String id = component.getId();
        if (isDistributionTypeButtonId(component.getId())) {
            id = SPUIDefinitions.DISTRIBUTION_TYPE_ID_PREFIXS;
        } else if (isSoftwareTypeButtonId(component.getId())) {
            id = SPUIDefinitions.SOFTWARE_MODULE_TAG_ID_PREFIXS;
        }
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.common.AbstractAcceptCriteria#
     * getDropHintConfigurations()
     */
    @Override
    protected Map<String, Object> getDropHintConfigurations() {
        return DROP_HINTS_CONFIGS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.common.AbstractAcceptCriteria#
     * publishDragStartEvent(java.lang.Object)
     */
    @Override
    protected void publishDragStartEvent(final Object event) {
        eventBus.publish(this, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.common.AbstractAcceptCriteria#
     * getDropConfigurations()
     */
    @Override
    protected Map<String, List<String>> getDropConfigurations() {
        return DROP_CONFIGS;
    }

    private boolean isDistributionTypeButtonId(final String id) {
        return id != null && (id.startsWith(SPUIDefinitions.DISTRIBUTION_TYPE_ID_PREFIXS)
                || id.startsWith(SPUIDefinitions.DISTRIBUTION_SET_TYPE_ID_PREFIXS));
    }

    private boolean isSoftwareTypeButtonId(final String id) {
        return id != null && id.startsWith(SPUIDefinitions.SOFTWARE_MODULE_TAG_ID_PREFIXS);
    }

    private static Map<String, List<String>> createDropConfigurations() {
        final Map<String, List<String>> config = new HashMap<String, List<String>>();

        // Delete drop area droppable components
        config.put(SPUIComponetIdProvider.DELETE_BUTTON_WRAPPER_ID,
                Arrays.asList(SPUIDefinitions.DISTRIBUTION_TYPE_ID_PREFIXS, SPUIComponetIdProvider.DIST_TABLE_ID,
                        SPUIComponetIdProvider.UPLOAD_SOFTWARE_MODULE_TABLE,
                        SPUIDefinitions.SOFTWARE_MODULE_TAG_ID_PREFIXS));

        // Distribution table drop components
        config.put(SPUIComponetIdProvider.DIST_TABLE_ID,
                Arrays.asList(SPUIComponetIdProvider.UPLOAD_SOFTWARE_MODULE_TABLE));

        return config;
    }

    private static Map<String, Object> createDropHintConfigurations() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put(SPUIDefinitions.DISTRIBUTION_TYPE_ID_PREFIXS, DragEvent.DISTRIBUTION_TYPE_DRAG);
        config.put(SPUIComponetIdProvider.DIST_TABLE_ID, DragEvent.DISTRIBUTION_DRAG);
        config.put(SPUIComponetIdProvider.UPLOAD_SOFTWARE_MODULE_TABLE, DragEvent.SOFTWAREMODULE_DRAG);
        config.put(SPUIDefinitions.SOFTWARE_MODULE_TAG_ID_PREFIXS, DragEvent.SOFTWAREMODULE_TYPE_DRAG);
        return config;
    }

}
