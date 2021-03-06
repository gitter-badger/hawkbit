/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthTokenSourceTrustAuthenticationProviderTest {

    private static final String REQUEST_SOURCE_IP = "127.0.0.1";

    private final PreAuthTokenSourceTrustAuthenticationProvider underTestWithoutSourceIpCheck = new PreAuthTokenSourceTrustAuthenticationProvider();
    private final PreAuthTokenSourceTrustAuthenticationProvider underTestWithSourceIpCheck = new PreAuthTokenSourceTrustAuthenticationProvider(
            REQUEST_SOURCE_IP);

    @Mock
    private TenantAwareWebAuthenticationDetails webAuthenticationDetailsMock;

    /**
     * Testing in case the containing controllerId in the URI request path does
     * not accord with the controllerId in the request header.
     */
    @Test(expected = BadCredentialsException.class)
    public void principalAndCredentialsNotTheSameThrowsAuthenticationException() {
        final String principal = "controllerIdURL";
        final String credentials = "controllerIdHeader";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        // test, should throw authentication exception
        underTestWithoutSourceIpCheck.authenticate(token);
    }

    /**
     * Testing that the controllerId within the URI request path is the same
     * with the controllerId within the request header and no source IP check is
     * in place.
     */
    @Test
    public void principalAndCredentialsAreTheSameWithNoSourceIpCheckIsSuccessful() {
        final String principal = "controllerId";
        final String credentials = "controllerId";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        final Authentication authenticate = underTestWithoutSourceIpCheck.authenticate(token);
        assertThat(authenticate.isAuthenticated()).isTrue();
    }

    /**
     * Testing that the controllerId in the URI request match with the
     * controllerId in the request header but the request are not coming from a
     * trustful source.
     */
    @Test(expected = InsufficientAuthenticationException.class)
    public void priniciapAndCredentialsAreTheSameButSourceIpRequestNotMatching() {
        final String remoteAddress = "192.168.1.1";
        final String principal = "controllerId";
        final String credentials = "controllerId";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        when(webAuthenticationDetailsMock.getRemoteAddress()).thenReturn(remoteAddress);

        // test, should throw authentication exception
        final Authentication authenticate = underTestWithSourceIpCheck.authenticate(token);
        assertThat(authenticate.isAuthenticated()).isTrue();
    }

    /**
     * Testing that the controllerId in the URI request match with the
     * controllerId in the request header and the source Ip is matching the
     * allowed remote IP address.
     */
    @Test()
    public void priniciapAndCredentialsAreTheSameAndSourceIpIsTrusted() {
        final String principal = "controllerId";
        final String credentials = "controllerId";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        when(webAuthenticationDetailsMock.getRemoteAddress()).thenReturn(REQUEST_SOURCE_IP);

        // test, should throw authentication exception
        final Authentication authenticate = underTestWithSourceIpCheck.authenticate(token);
        assertThat(authenticate.isAuthenticated()).isTrue();
    }

    @Test()
    public void priniciapAndCredentialsAreTheSameAndSourceIpIsWithinList() {
        final String[] trustedIPAddresses = new String[] { "192.168.1.1", "192.168.1.2", REQUEST_SOURCE_IP,
                "192.168.1.3" };
        final String principal = "controllerId";
        final String credentials = "controllerId";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        when(webAuthenticationDetailsMock.getRemoteAddress()).thenReturn(REQUEST_SOURCE_IP);

        final PreAuthTokenSourceTrustAuthenticationProvider underTestWithList = new PreAuthTokenSourceTrustAuthenticationProvider(
                trustedIPAddresses);

        // test, should throw authentication exception
        final Authentication authenticate = underTestWithList.authenticate(token);
        assertThat(authenticate.isAuthenticated()).isTrue();
    }

    @Test(expected = InsufficientAuthenticationException.class)
    public void principalAndCredentialsAreTheSameSourceIpListNotMatches() {
        final String[] trustedIPAddresses = new String[] { "192.168.1.1", "192.168.1.2", "192.168.1.3" };
        final String principal = "controllerId";
        final String credentials = "controllerId";
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(principal,
                credentials);
        token.setDetails(webAuthenticationDetailsMock);

        when(webAuthenticationDetailsMock.getRemoteAddress()).thenReturn(REQUEST_SOURCE_IP);

        final PreAuthTokenSourceTrustAuthenticationProvider underTestWithList = new PreAuthTokenSourceTrustAuthenticationProvider(
                trustedIPAddresses);

        // test, should throw authentication exception
        final Authentication authenticate = underTestWithList.authenticate(token);
        assertThat(authenticate.isAuthenticated()).isTrue();
    }
}
