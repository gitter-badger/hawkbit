/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.eventbus.event;

import org.eclipse.hawkbit.repository.model.TargetTag;

/**
 * Defines the {@link AbstractBaseEntityEvent} for update a {@link TargetTag}.
 *
 */
public class TargetTagUpdateEvent extends AbstractBaseEntityEvent<TargetTag> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param tag
     *            the tag which is updated
     */
    public TargetTagUpdateEvent(final TargetTag tag) {
        super(tag);
    }
}
