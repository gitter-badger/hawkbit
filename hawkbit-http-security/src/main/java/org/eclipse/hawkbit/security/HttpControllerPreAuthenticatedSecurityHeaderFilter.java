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
 * An pre-authenticated processing filter which extracts the principal from a
 * request URI and the credential from a request header.
 * 
 *
 *
 */
public class HttpControllerPreAuthenticatedSecurityHeaderFilter extends AbstractHttpControllerAuthenticationFilter {

    private final String caCommonNameHeader;
    private final String caAuthorityNameHeader;

    /**
     * Creates a new {@link ControllerPreAuthenticatedSecurityHeaderFilter}, in
     * case the HTTP request matches the given pattern the principal is parsed
     * from the HTTP request with the given URI pattern, in case the URI pattern
     * does not match the current request then only the existence of the
     * configured header field is checked.
     * 
     * @param caCommonNameHeader
     *            the http-header which holds the common-name of the certificate
     * @param caAuthorityNameHeader
     *            the http-header which holds the ca-authority name of the
     *            certificate
     * @param systemManagement
     *            the system management service to retrieve configuration
     *            properties to check if the header authentication is enabled
     *            for this tenant
     * @param tenantAware
     *            the tenant aware service to get configuration for the specific
     *            tenant
     */
    public HttpControllerPreAuthenticatedSecurityHeaderFilter(final String caCommonNameHeader,
            final String caAuthorityNameHeader, final SystemManagement systemManagement,
            final TenantAware tenantAware) {
        super(systemManagement, tenantAware);
        this.caCommonNameHeader = caCommonNameHeader;
        this.caAuthorityNameHeader = caAuthorityNameHeader;
    }

    @Override
    protected PreAuthenficationFilter createControllerAuthenticationFilter() {
        return new ControllerPreAuthenticatedSecurityHeaderFilter(caCommonNameHeader, caAuthorityNameHeader,
                systemManagement, tenantAware);
    }

}
