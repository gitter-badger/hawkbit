/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.io.Serializable;

import org.eclipse.hawkbit.repository.model.BaseEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command repository operations for all {@link BaseEntity}s.
 *
 *
 *
 *
 * @param <T>
 *            type if the entity type
 * @param <I>
 *            of the entity type
 */
@NoRepositoryBean
@Transactional(readOnly = true)
public interface BaseEntityRepository<T extends BaseEntity, I extends Serializable>
        extends PagingAndSortingRepository<T, I> {

    /**
     * Deletes all {@link BaseEntity} of a given tenant.
     *
     * @param tenant
     *            to delete data from
     */
    @Modifying
    @Transactional
    void deleteByTenantIgnoreCase(String tenant);

}
