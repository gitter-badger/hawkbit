/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.filtermanagement;

import javax.annotation.PreDestroy;

import org.eclipse.hawkbit.ui.HawkbitUI;
import org.eclipse.hawkbit.ui.filtermanagement.event.CustomFilterUIEvent;
import org.eclipse.hawkbit.ui.filtermanagement.state.FilterManagementUIState;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 *
 *
 */

@SpringView(name = FilterManagementView.VIEW_NAME, ui = HawkbitUI.class)
@ViewScope
public class FilterManagementView extends VerticalLayout implements View {

    private static final long serialVersionUID = 8751545414237389386L;

    public static final String VIEW_NAME = "targetFilters";

    @Autowired
    private TargetFilterHeader targetFilterHeader;

    @Autowired
    private TargetFilterTable targetFilterTable;

    @Autowired
    private CreateOrUpdateFilterHeader createNewFilterHeader;

    @Autowired
    private CreateOrUpdateFilterTable createNewFilterTable;

    @Autowired
    private FilterManagementUIState filterManagementUIState;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    @Override
    public void enter(final ViewChangeEvent event) {
        setSizeFull();
        setImmediate(true);
        buildLayout();
        eventBus.subscribe(this);
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }

    private void buildLayout() {
        setSizeFull();
        setSpacing(false);
        setMargin(false);
        addStyleName("table-layout");
        if (filterManagementUIState.isCreateFilterViewDisplayed()) {
            viewCreateTargetFilterLayout();
        } else if (filterManagementUIState.isEditViewDisplayed()) {
            viewTargetFilterDetailLayout();
        } else {
            viewListView();
        }
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final CustomFilterUIEvent custFilterUIEvent) {
        if (custFilterUIEvent == CustomFilterUIEvent.TARGET_FILTER_DETAIL_VIEW) {
            viewTargetFilterDetailLayout();
        } else if (custFilterUIEvent == CustomFilterUIEvent.CREATE_NEW_FILTER_CLICK) {
            UI.getCurrent().access(() -> viewCreateTargetFilterLayout());
        } else if (custFilterUIEvent == CustomFilterUIEvent.EXIT_CREATE_OR_UPDATE_FILTRER_VIEW) {
            UI.getCurrent().access(() -> viewListView());
        }
    }

    private void viewCreateTargetFilterLayout() {
        buildFilterDetailOrCreateView();

    }

    private void viewTargetFilterDetailLayout() {
        buildFilterDetailOrCreateView();
    }

    private void buildFilterDetailOrCreateView() {
        removeAllComponents();
        addComponents(createNewFilterHeader, createNewFilterTable);
        setComponentAlignment(createNewFilterHeader, Alignment.TOP_LEFT);
        setComponentAlignment(createNewFilterTable, Alignment.TOP_LEFT);
        setExpandRatio(createNewFilterTable, 1.0f);
    }

    private void viewListView() {
        removeAllComponents();
        addComponents(targetFilterHeader, targetFilterTable);
        setComponentAlignment(targetFilterHeader, Alignment.TOP_LEFT);
        setComponentAlignment(targetFilterTable, Alignment.TOP_LEFT);
        setExpandRatio(targetFilterTable, 1.0f);
    }

}
