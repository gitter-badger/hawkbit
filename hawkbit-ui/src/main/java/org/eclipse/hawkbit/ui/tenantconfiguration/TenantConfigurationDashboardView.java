/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.tenantconfiguration;

import org.eclipse.hawkbit.ui.HawkbitUI;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.decorators.SPUIButtonStyleSmallNoBorder;
import org.eclipse.hawkbit.ui.documentation.DocumentationPageLink;
import org.eclipse.hawkbit.ui.tenantconfiguration.ConfigurationGroup.ConfigurationGroupChangeListener;
import org.eclipse.hawkbit.ui.utils.I18N;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI for the system configuration view.
 * 
 *
 *
 */
@SpringView(name = TenantConfigurationDashboardView.VIEW_NAME, ui = HawkbitUI.class)
@ViewScope
public class TenantConfigurationDashboardView extends CustomComponent
        implements View, ConfigurationGroupChangeListener {

    public static final String VIEW_NAME = "spSystemConfig";
    private static final long serialVersionUID = 1L;

    @Autowired
    private DefaultDistributionSetTypeLayout defaultDistributionSetTypeLayout;

    @Autowired
    private AuthenticationConfigurationView authenticationConfigurationView;

    @Autowired
    private I18N i18n;

    @Autowired
    private transient UINotification uINotification;

    private Button saveConfigurationBtn;
    private Button undoConfigurationBtn;

    @Override
    public void enter(final ViewChangeEvent event) {

        final Panel rootPanel = new Panel();
        rootPanel.setStyleName("tenantconfig-root");

        final VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        rootLayout.setMargin(true);
        rootLayout.setSpacing(true);
        rootLayout.addComponent(defaultDistributionSetTypeLayout);
        rootLayout.addComponent(authenticationConfigurationView);
        final HorizontalLayout buttonContent = saveConfigurationButtonsLayout();
        rootLayout.addComponent(buttonContent);
        rootLayout.setComponentAlignment(buttonContent, Alignment.BOTTOM_LEFT);
        rootPanel.setContent(rootLayout);
        setCompositionRoot(rootPanel);

        authenticationConfigurationView.addChangeListener(this);
        defaultDistributionSetTypeLayout.addChangeListener(this);

    }

    private HorizontalLayout saveConfigurationButtonsLayout() {

        final HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setSpacing(true);
        saveConfigurationBtn = SPUIComponentProvider.getButton(SPUIComponetIdProvider.SYSTEM_CONFIGURATION_SAVE, "", "",
                "", true, FontAwesome.SAVE, SPUIButtonStyleSmallNoBorder.class);
        saveConfigurationBtn.setEnabled(false);
        saveConfigurationBtn.setDescription(i18n.get("configuration.savebutton.tooltip"));
        saveConfigurationBtn.addClickListener(event -> saveConfiguration());
        hlayout.addComponent(saveConfigurationBtn);

        undoConfigurationBtn = SPUIComponentProvider.getButton(SPUIComponetIdProvider.SYSTEM_CONFIGURATION_CANCEL, "",
                "", "", true, FontAwesome.UNDO, SPUIButtonStyleSmallNoBorder.class);
        undoConfigurationBtn.setEnabled(false);
        undoConfigurationBtn.setDescription(i18n.get("configuration.cancellbutton.tooltip"));
        undoConfigurationBtn.addClickListener(event -> undoConfiguration());
        hlayout.addComponent(undoConfigurationBtn);

        final Link linkToSystemConfigHelp = DocumentationPageLink.SYSTEM_CONFIGURATION_VIEW.getLink();
        hlayout.addComponent(linkToSystemConfigHelp);

        return hlayout;
    }

    private void saveConfiguration() {
        defaultDistributionSetTypeLayout.save();
        authenticationConfigurationView.save();

        // More methods
        saveConfigurationBtn.setEnabled(false);
        undoConfigurationBtn.setEnabled(false);
        uINotification.displaySuccess(i18n.get("notification.configuration.save"));
    }

    private void undoConfiguration() {
        defaultDistributionSetTypeLayout.undo();
        authenticationConfigurationView.undo();

        // More methods
        saveConfigurationBtn.setEnabled(false);
        undoConfigurationBtn.setEnabled(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.hawkbit.server.ui.tenantconfiguration.ConfigurationGroup.
     * ConfigurationGroupChangeListener #configurationChanged()
     */
    @Override
    public void configurationChanged() {
        saveConfigurationBtn.setEnabled(true);
        undoConfigurationBtn.setEnabled(true);
    }

}
