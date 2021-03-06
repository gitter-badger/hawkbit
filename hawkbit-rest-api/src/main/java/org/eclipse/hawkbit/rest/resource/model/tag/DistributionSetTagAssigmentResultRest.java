/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource.model.tag;

import org.eclipse.hawkbit.rest.resource.model.distributionset.DistributionSetsRest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * * A json annotated rest model for DSAssigmentResult to RESTful API
 * representation.
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionSetTagAssigmentResultRest {

    @JsonProperty
    private DistributionSetsRest assignedDistributionSets;

    @JsonProperty
    private DistributionSetsRest unassignedDistributionSets;

    public DistributionSetsRest getAssignedDistributionSets() {
        return assignedDistributionSets;
    }

    public DistributionSetsRest getUnassignedDistributionSets() {
        return unassignedDistributionSets;
    }

    public void setAssignedDistributionSets(final DistributionSetsRest assignedDistributionSets) {
        this.assignedDistributionSets = assignedDistributionSets;
    }

    public void setUnassignedDistributionSets(final DistributionSetsRest unassignedDistributionSets) {
        this.unassignedDistributionSets = unassignedDistributionSets;
    }

}
