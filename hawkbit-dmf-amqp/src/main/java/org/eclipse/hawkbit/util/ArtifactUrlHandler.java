/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.util;

import org.eclipse.hawkbit.dmf.json.model.Artifact;
import org.eclipse.hawkbit.repository.model.LocalArtifact;

/**
 * Interface declaration of the {@link ArtifactUrlHandler} which generates the
 * URLs to specific artifacts.
 *
 */
@FunctionalInterface
public interface ArtifactUrlHandler {

    /**
     * Returns a generated URL for a given artifact for a specific protocol.
     *
     * @param controllerId
     *            the authentifacted controller id
     * @param localArtifact
     *            the artifact to retrieve a URL to
     * @param protocol
     *            the protocol the URL should be generated
     * @return an URL for the given artifact in a given protocol
     */
    String getUrl(String controllerId, LocalArtifact localArtifact, final Artifact.UrlProtocol protocol);

}
