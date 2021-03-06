/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.List;

import org.eclipse.hawkbit.repository.model.SoftwareModuleMetadata;
import org.eclipse.hawkbit.repository.model.SwMetadataCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link SoftwareModuleMetadata} repository.
 *
 *
 *
 */
@Transactional(readOnly = true)
public interface SoftwareModuleMetadataRepository
        extends PagingAndSortingRepository<SoftwareModuleMetadata, SwMetadataCompositeKey>,
        JpaSpecificationExecutor<SoftwareModuleMetadata> {

    /**
     * Saves all given entities.
     *
     * @param entities
     * @return the saved entities
     * @throws IllegalArgumentException
     *             in case the given entity is (@literal null}.
     */
    @Override
    <S extends SoftwareModuleMetadata> List<S> save(Iterable<S> entities);

    /**
     * finds all software module meta data of the given software module id.
     * 
     * @param swId
     *            the ID of the software module to retrieve the meta data
     * @param pageable
     *            the page request to page the result set
     * @return the paged result of all meta data of an given software module id
     */
    Page<SoftwareModuleMetadata> findBySoftwareModuleId(final Long swId, Pageable pageable);

}
