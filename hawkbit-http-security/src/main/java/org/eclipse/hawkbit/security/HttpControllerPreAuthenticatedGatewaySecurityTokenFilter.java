/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;

import org.eclipse.hawkbit.repository.SystemManagement;
import org.eclipse.hawkbit.tenancy.TenantAware;

/**
 * Extract the {@code Authorization} header is a HTTP standard and reverse proxy
 * or other proxies will keep the Authorization headers untouched instead of
 * maybe custom headers which have then weird side-effects. Furthermore
 * frameworks are aware of the sensitivity of the Authorization header and do
 * not log it and store it somewhere.
 *
 *
 *
 */
public class HttpControllerPreAuthenticatedGatewaySecurityTokenFilter
        extends AbstractHttpControllerAuthenticationFilter {

    /**
     * Constructor.
     * 
     * @param systemManagement
     *            the system management service to retrieve configuration
     *            properties
     * @param tenantAware
     *            the tenant aware service to get configuration for the specific
     *            tenant
     */
    public HttpControllerPreAuthenticatedGatewaySecurityTokenFilter(final SystemManagement systemManagement,
            final TenantAware tenantAware) {
        super(systemManagement, tenantAware);
    }

    @Override
    protected PreAuthenficationFilter createControllerAuthenticationFilter() {
        return new ControllerPreAuthenticatedGatewaySecurityTokenFilter(systemManagement, tenantAware);
    }

}
