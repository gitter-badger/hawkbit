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

import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.AssignmentResult;
import org.eclipse.hawkbit.repository.model.Target;

/**
 * A bean which holds a complex result of an service operation to combine the
 * information of an assignment and how much of the assignment has been done and
 * how much of the assignments had already been existed.
 *
 *
 *
 *
 */
public class DistributionSetAssignmentResult extends AssignmentResult {

    private final List<String> assignedTargets;
    private final List<Action> actions;

    private final TargetManagement targetManagement;

    /**
     *
     * Constructor.
     *
     * @param assignedTargets
     *            the target objects which have been assigned to the
     *            distribution set
     * @param assigned
     *            count of the assigned targets
     * @param alreadyAssigned
     *            the count of the already assigned targets
     * @param targetManagement
     *            to retrieve the assigned targets
     * @param actions
     *            of the assignment
     *
     */
    public DistributionSetAssignmentResult(final List<String> assignedTargets, final int assigned,
            final int alreadyAssigned, final List<Action> actions, final TargetManagement targetManagement) {
        super(assigned, alreadyAssigned);
        this.assignedTargets = assignedTargets;
        this.actions = actions;
        this.targetManagement = targetManagement;
    }

    /**
     * @return the assignedTargets
     */
    public List<Target> getAssignedTargets() {
        return targetManagement.findTargetsByControllerID(assignedTargets);
    }

    /**
     * @return the actionId
     */
    public List<Action> getActions() {
        return actions;
    }

}
