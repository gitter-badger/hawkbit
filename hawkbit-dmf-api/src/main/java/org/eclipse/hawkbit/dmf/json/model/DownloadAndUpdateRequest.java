/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.dmf.json.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of download and update request.
 *
 *
 *
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadAndUpdateRequest {
    @JsonProperty
    private Long actionId;
    @JsonProperty
    private final List<SoftwareModule> softwareModules = new LinkedList<>();

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(final Long correlator) {
        this.actionId = correlator;
    }

    public List<SoftwareModule> getSoftwareModules() {
        return softwareModules;
    }

    /**
     * Add a Software module.
     *
     * @param createSoftwareModule
     *            the module
     */
    public void addSoftwareModule(final SoftwareModule createSoftwareModule) {
        softwareModules.add(createSoftwareModule);

    }
}
