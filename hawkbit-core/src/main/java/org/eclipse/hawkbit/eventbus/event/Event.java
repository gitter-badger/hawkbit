/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.eventbus.event;

/**
 * An event declaration which holds an revision for each event so consumers have
 * the chance to know if they might already retrieved an newer event.
 *
 *
 *
 *
 */
public interface Event {

    /**
     * @return the revision of this event which should be increment or each new
     *         event in case the event have a causalität. Might be {@code -1} in
     *         case the events does not provide any revision.
     */
    long getRevision();

    /**
     * @return the tenant of the entity.
     */
    String getTenant();
}
