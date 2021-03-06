/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.artifact.repository;

import java.io.InputStream;
import java.util.List;

import org.eclipse.hawkbit.artifact.repository.model.DbArtifact;
import org.eclipse.hawkbit.artifact.repository.model.DbArtifactHash;

/**
 * ArtifactRepository service interface.
 * 
 *
 *
 */
public interface ArtifactRepository {

    /**
     * Stores an artifact into the repository.
     * 
     * @param tenant
     *            the tenant to store the artifact
     * @param content
     *            the content to store
     * @param filename
     *            the filename of the artifact
     * @param contentType
     *            the content type of the artifact
     * @return the stored artifact
     * @throws ArtifactStoreException
     *             in case storing of the artifact was not successful
     */
    DbArtifact store(final InputStream content, final String filename, final String contentType);

    /**
     * Stores an artifact into the repository.
     * 
     * @param content
     *            the content to store
     * @param filename
     *            the filename of the artifact
     * @param contentType
     *            the content type of the artifact
     * @param hash
     *            the hashes of the artifact to do hash-checks after storing the
     *            artifact, might be {@code null}
     * @return the stored artifact
     * @throws ArtifactStoreException
     *             in case storing of the artifact was not successful
     * @throws HashNotMatchException
     *             in case {@code hash} is provided and not matching to the
     *             calculated hashes during storing
     */
    DbArtifact store(final InputStream content, final String filename, final String contentType, DbArtifactHash hash);

    /**
     * Deletes an artifact by its ID.
     * 
     * @param artifactId
     *            the ID of the artifact to delete
     */
    void deleteById(final String artifactId);

    /**
     * Deletes an artifact by its SHA1 hash.
     * 
     * @param sha1Hash
     *            the sha1-hash of the artifact to delete
     */
    void deleteBySha1(final String sha1Hash);

    /**
     * Retrieves a {@link DbArtifact} from the store by it's SHA1 hash.
     * 
     * @param sha1Hash
     *            the sha1-hash of the file to lookup.
     * @return The artifact file object or {@code null} if no file exists.
     */
    DbArtifact getArtifactBySha1(String sha1);

    /**
     * Retrieves a {@link DbArtifact} from the store by it's ID.
     * 
     * @param id
     *            the ID of the artifact to retrieve
     * @return The artifact file object or {@code null} if no file exists.
     */
    DbArtifact getArtifactById(final String id);

    /**
     * Retrieves a list of {@link GridFSDBFile} from the store by all SHA1
     * hashes.
     * 
     * @param tenant
     *            the tenant to retrieve the artifacts from, ignore case.
     * @param sha1Hashes
     *            the sha1-hashes of the files to lookup.
     * @return list of artfiacts
     */
    List<DbArtifact> getArtifactsBySha1(final List<String> sha1Hashes);

}
