/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.app;

import org.eclipse.hawkbit.RepositoryApplicationConfiguration;
import org.eclipse.hawkbit.autoconfigure.security.EnableHawkbitManagedSecurityConfiguration;
import org.eclipse.hawkbit.controller.EnableDirectDeviceApi;
import org.eclipse.hawkbit.rest.resource.EnableRestResources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * A {@link SpringBootApplication} annotated class with a main method to start.
 * The minimal configuration for the stand alone hawkBit server.
 *
 */
@SpringBootApplication
@Import({ RepositoryApplicationConfiguration.class })
@EnableHawkbitManagedSecurityConfiguration
@EnableRestResources
@EnableDirectDeviceApi
public class Start {

    /**
     * Main method to start the spring-boot application.
     *
     * @param args
     *            the VM arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Start.class, args);
    }
}
