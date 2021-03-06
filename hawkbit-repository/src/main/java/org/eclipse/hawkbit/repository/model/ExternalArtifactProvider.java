/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * External repositories for artifact storage. The SP server provides URLs for
 * the targets to download rom these external ressources but does not access
 * thenm itself.
 *
 *
 *
 *
 */
@Table(name = "sp_external_provider", indexes = {
        @Index(name = "sp_idx_external_provider_prim", columnList = "tenant,id") })
@Entity
public class ExternalArtifactProvider extends NamedEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "base_url", length = 512, nullable = false)
    private String basePath;

    @Column(name = "default_url_suffix", length = 512, nullable = true)
    private String defaultSuffix;

    /**
     * Constructs {@link ExternalArtifactProvider} based on given properties.
     *
     * @param name
     *            of the provided
     * @param description
     *            which is optional
     * @param baseURL
     *            of all {@link ExternalArtifact}s of the provider
     * @param defaultUrlSuffix
     *            that is used if {@link ExternalArtifact#getUrlSuffix()} is
     *            empty.
     */
    public ExternalArtifactProvider(final String name, final String description, final String baseURL,
            final String defaultUrlSuffix) {
        super(name, description);
        basePath = baseURL;
        defaultSuffix = defaultUrlSuffix;
    }

    ExternalArtifactProvider() {
        super();
        defaultSuffix = "";
        basePath = "";
    }

    /**
     * @return the basePath
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * @return the defaultSuffix
     */
    public String getDefaultSuffix() {
        return defaultSuffix;
    }

    /**
     * @param basePath
     *            the basePath to set
     */
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    /**
     * @param defaultSuffix
     *            the defaultSuffix to set
     */
    public void setDefaultSuffix(final String defaultSuffix) {
        this.defaultSuffix = defaultSuffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.getClass().getName().hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ExternalArtifactProvider)) {
            return false;
        }

        return true;
    }

}
