/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import org.eclipse.hawkbit.repository.model.DistributionSetType;
import org.eclipse.hawkbit.repository.model.SoftwareModuleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link PagingAndSortingRepository} for {@link DistributionSetType}.
 *
 *
 *
 *
 */
@Transactional(readOnly = true)
public interface DistributionSetTypeRepository
        extends BaseEntityRepository<DistributionSetType, Long>, JpaSpecificationExecutor<DistributionSetType> {

    /**
     *
     * @param pageable
     *            page parameters
     * @param isDeleted
     *            to <code>true</code> if only soft deleted entries of
     *            <code>false</code> if undeleted ones
     * @return list of found {@link DistributionSetType}s
     */
    Page<DistributionSetType> findByDeleted(Pageable pageable, boolean isDeleted);

    /**
     * @param isDeleted
     *            to <code>true</code> if only marked as deleted have to be
     *            count or all undeleted.
     * @return number of {@link DistributionSetType}s in the repository.
     */
    Long countByDeleted(boolean isDeleted);

    /**
     * Counts all distribution set type where a specific software module type is
     * assigned to.
     * 
     * @param softwareModuleType
     *            the software module type to count the distribution set type
     *            which has this software module type assigned
     * @return the number of {@link DistributionSetType}s in the repository
     *         assigned to the given software module type
     */
    Long countByElementsSmType(SoftwareModuleType softwareModuleType);
}
