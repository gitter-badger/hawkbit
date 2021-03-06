/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Collection;
import java.util.List;

import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Tag;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetTag;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link Target} repository.
 *
 *
 *
 */
@Transactional(readOnly = true)
public interface TargetRepository extends BaseEntityRepository<Target, Long>, JpaSpecificationExecutor<Target> {

    /**
     * Loads {@link Target} including details {@link EntityGraph} by given ID.
     *
     * @param controllerID
     *            to search for
     * @return found {@link Target} or <code>null</code> if not found.
     */
    @EntityGraph(value = "Target.detail", type = EntityGraphType.LOAD)
    Target findByControllerId(String controllerID);

    /**
     * Finds targets by given list of {@link Target#getControllerId()}s.
     *
     * @param controllerIDs
     *            to serach for
     * @return list of found {@link Target}s
     */
    List<Target> findByControllerIdIn(String... controllerIDs);

    /**
     * Deletes the {@link Target}s with the given target IDs.
     *
     * @param targetIDs
     *            to be deleted
     */
    @Modifying
    @Transactional
    // Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=349477
    @Query("DELETE FROM Target t WHERE t.id IN ?1")
    void deleteByIdIn(final Collection<Long> targetIDs);

    /**
     * Finds {@link Target}s by assigned {@link Tag}.
     *
     * @param tag
     *            to be found
     * @return list of found targets
     */
    @Query(value = "SELECT DISTINCT t FROM Target t JOIN t.tags tt WHERE tt = :tag")
    List<Target> findByTag(@Param("tag") final TargetTag tag);

    /**
     * Finds all {@link Target}s based on given {@link Target#getControllerId()}
     * list and assigned {@link Tag#getName()}.
     *
     * @param tag
     *            to search for
     * @param controllerIds
     *            to search for
     * @return {@link List} of found {@link Target}s.
     */
    @Query(value = "SELECT DISTINCT t from Target t JOIN t.tags tt WHERE tt.name = :tagname AND t.controllerId IN :targets")
    List<Target> findByTagNameAndControllerIdIn(@Param("tagname") final String tag,
            @Param("targets") final Collection<String> controllerIds);

    /**
     * Used by UI to filter based on selected status.
     * 
     * @param pageable
     *            for page configuration
     * @param status
     *            to filter for
     *
     * @return found targets
     */
    Page<Target> findByTargetInfoUpdateStatus(final Pageable pageable, final TargetUpdateStatus status);

    /**
     * Finds all targets that have defined {@link DistributionSet} installed.
     * 
     * @param pageable
     *            for page configuration
     * @param set
     *            is the {@link DistributionSet} to filter for.
     *
     * @return found targets
     */
    Page<Target> findByTargetInfoInstalledDistributionSet(final Pageable pageable, final DistributionSet set);

    /**
     * retrieves the {@link Target}s which has the {@link DistributionSet}
     * installed with the given ID.
     * 
     * @param pageable
     *            parameter
     * @param setID
     *            the ID of the {@link DistributionSet}
     * @return the found {@link Target}s
     */
    Page<Target> findByTargetInfoInstalledDistributionSetId(final Pageable pageable, final Long setID);

    /**
     * Finds all targets that have defined {@link DistributionSet} assigned.
     * 
     * @param pageable
     *            for page configuration
     * @param set
     *            is the {@link DistributionSet} to filter for.
     *
     * @return found targets
     */
    Page<Target> findByAssignedDistributionSet(final Pageable pageable, final DistributionSet set);

    /**
     * Saves all given {@link Target}s.
     *
     * @param entities
     * @return the saved entities
     * @throws IllegalArgumentException
     *             in case the given entity is (@literal null}.
     *
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = { "targetStatus", "distributionUsageInstalled", "targetsLastPoll" }, allEntries = true)
    <S extends Target> List<S> save(Iterable<S> entities);

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the entity instance completely.
     *
     * @param entity
     *            the target to save
     * @return the saved entity
     */
    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = { "targetStatus", "distributionUsageInstalled", "targetsLastPoll" }, allEntries = true)
    <S extends Target> S save(S entity);

    /**
     * Finds all targets that have defined {@link DistributionSet} assigned.
     * 
     * @param pageable
     *            for page configuration
     * @param setID
     *            is the ID of the {@link DistributionSet} to filter for.
     *
     * @return page of found targets
     */
    Page<Target> findByAssignedDistributionSetId(final Pageable pageable, final Long setID);

    /**
     * Counts number of targets with given
     * {@link Target#getAssignedDistributionSet()}.
     *
     * @param distId
     *            to search for
     *
     * @return number of found {@link Target}s.
     */
    Long countByAssignedDistributionSetId(final Long distId);

    /**
     * @param ids
     *            of count in DB
     * @return number of found {@link Target}s with given
     *         {@link Target#getControllerId()}s
     */
    @Query("SELECT COUNT(t) FROM Target t WHERE t.controllerId IN ?1")
    Long countByControllerIdIn(final Collection<String> ids);

    /**
     * Counts number of targets with given
     * {@link TargetStatus#getInstalledDistributionSet()}.
     *
     * @param distId
     *            to search for
     * @return number of found {@link Target}s.
     */
    Long countByTargetInfoInstalledDistributionSetId(final Long distId);

    /**
     * Finds all targets that have defined {@link DistributionSet} assigned or
     * installed.
     * 
     * @param pageable
     *            for page configuration
     * @param assigned
     *            {@link DistributionSet} filter for; please note: must not be
     *            null
     * @param installed
     *            {@link DistributionSet} filter for; please note: must not be
     *            null
     *
     * @return found targets
     */
    Page<Target> findByAssignedDistributionSetOrTargetInfoInstalledDistributionSet(final Pageable pageable,
            final DistributionSet assigned, final DistributionSet installed);

    /**
     * Finds all targets that have defined {@link DistributionSet} assigned or
     * installed.
     * 
     * @param pageable
     *            for page configuration
     * @param assigned
     *            {@link DistributionSet} filter for; please note: must not be
     *            null
     * @param installed
     *            {@link DistributionSet} filter for; please note: must not be
     *            null
     * @return found targets
     */
    Page<Target> findByAssignedDistributionSetIdOrTargetInfoInstalledDistributionSetId(final Pageable pageable,
            final Long assigned, final Long installed);

    /**
     * Finds all {@link Target}s in the repository.
     *
     * @return {@link List} of {@link Target}s
     *
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    @Override
    List<Target> findAll();

    @Override
    // Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=349477
    @Query("SELECT t FROM Target t WHERE t.id IN ?1")
    List<Target> findAll(Iterable<Long> ids);

    /**
     * Sets {@link Target#getAssignedDistributionSet()}.
     *
     * @param set
     *            to use
     * @param modifiedAt
     *            current time
     * @param modifiedBy
     *            current auditor
     * @param targets
     *            to update
     */
    @Modifying
    @Transactional
    @Query("UPDATE Target t  SET t.assignedDistributionSet = :set, t.lastModifiedAt = :lastModifiedAt, t.lastModifiedBy = :lastModifiedBy WHERE t.id IN :targets")
    void setAssignedDistributionSet(@Param("set") DistributionSet set, @Param("lastModifiedAt") Long modifiedAt,
            @Param("lastModifiedBy") String modifiedBy, @Param("targets") Collection<Long> targets);
}
