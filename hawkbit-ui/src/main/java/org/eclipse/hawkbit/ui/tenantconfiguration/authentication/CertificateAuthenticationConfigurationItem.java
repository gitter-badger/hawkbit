/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.tenantconfiguration.authentication;

import javax.annotation.PostConstruct;

import org.eclipse.hawkbit.repository.SystemManagement;
import org.eclipse.hawkbit.repository.model.TenantConfiguration;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationKey;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.utils.I18N;
import org.eclipse.hawkbit.ui.utils.SPUILabelDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 *
 */
@SpringComponent
@ViewScope
public class CertificateAuthenticationConfigurationItem extends AbstractAuthenticationTenantConfigurationItem {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Autowired
    private I18N i18n;

    private boolean configurationEnabled = false;
    private boolean configurationEnabledChange = false;
    private boolean configurationCaRootAuthorityChanged = false;

    private VerticalLayout detailLayout;
    private TextField caRootAuthorityTextField;

    /**
     * @param systemManagement
     *            the system management to retrie the configuration
     */
    @Autowired
    public CertificateAuthenticationConfigurationItem(final SystemManagement systemManagement) {
        super(TenantConfigurationKey.AUTHENTICATION_MODE_HEADER_ENABLED, systemManagement);
    }

    /**
     * init mehotd called by spring.
     */
    @PostConstruct
    public void init() {
        super.init(i18n.get("label.configuration.auth.header"));
        configurationEnabled = isConfigEnabled();

        detailLayout = new VerticalLayout();
        detailLayout.setImmediate(true);

        final HorizontalLayout caRootAuthorityLayout = new HorizontalLayout();
        caRootAuthorityLayout.setSpacing(true);

        final Label caRootAuthorityLabel = SPUIComponentProvider.getLabel("SSL Issuer Hash:",
                SPUILabelDefinitions.SP_LABEL_SIMPLE);
        caRootAuthorityLabel.setDescription(
                "The SSL Issuer iRules.X509 hash, to validate against the controller request certifcate.");

        caRootAuthorityTextField = SPUIComponentProvider.getTextField("", ValoTheme.TEXTFIELD_TINY, false, null, "",
                true, 128);
        caRootAuthorityTextField.setWidth("500px");
        caRootAuthorityTextField.setImmediate(true);
        caRootAuthorityTextField.addTextChangeListener(event -> caRootAuthorityChanged());

        caRootAuthorityLayout.addComponent(caRootAuthorityLabel);
        caRootAuthorityLayout.addComponent(caRootAuthorityTextField);

        detailLayout.addComponent(caRootAuthorityLayout);

        if (isConfigEnabled()) {
            caRootAuthorityTextField.setValue(getCaRootAuthorityValue());
            setDetailVisible(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.tenantconfiguration.
     * TenantConfigurationItem# configEnable()
     */
    @Override
    public void configEnable() {
        if (!configurationEnabled) {
            configurationEnabledChange = true;
        }
        configurationEnabled = true;
        configurationCaRootAuthorityChanged = true;
        setDetailVisible(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.hawkbit.server.ui.tenantconfiguration.
     * TenantConfigurationItem# configDisable()
     */
    @Override
    public void configDisable() {
        if (configurationEnabled) {
            configurationEnabledChange = true;
        }
        configurationEnabled = false;
        setDetailVisible(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hawkbit.server.ui.tenantconfiguration.TenantConfigurationItem#save()
     */
    @Override
    public void save() {
        if (configurationEnabledChange) {
            getSystemManagement().addOrUpdateConfiguration(
                    new TenantConfiguration(getConfigurationKey().getKeyName(), String.valueOf(configurationEnabled)));
        }
        if (configurationCaRootAuthorityChanged) {
            final String value = caRootAuthorityTextField.getValue() != null ? caRootAuthorityTextField.getValue() : "";
            getSystemManagement().addOrUpdateConfiguration(new TenantConfiguration(
                    TenantConfigurationKey.AUTHENTICATION_MODE_HEADER_AUTHORITY_NAME.getKeyName(), value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hawkbit.server.ui.tenantconfiguration.TenantConfigurationItem#undo()
     */
    @Override
    public void undo() {
        configurationEnabledChange = false;
        configurationCaRootAuthorityChanged = false;

        configurationEnabled = getSystemManagement().getConfigurationValue(getConfigurationKey(), Boolean.class);
        caRootAuthorityTextField.setValue(getCaRootAuthorityValue());
    }

    private String getCaRootAuthorityValue() {
        return getSystemManagement()
                .getConfigurationValue(TenantConfigurationKey.AUTHENTICATION_MODE_HEADER_AUTHORITY_NAME, String.class);
    }

    private void setDetailVisible(final boolean visible) {
        if (visible) {
            addComponent(detailLayout);
        } else {
            removeComponent(detailLayout);
        }

    }

    private void caRootAuthorityChanged() {
        configurationCaRootAuthorityChanged = true;
        notifyConfigurationChanged();
    }
}
