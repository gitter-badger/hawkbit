/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource.helper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.hawkbit.artifact.repository.model.DbArtifact;
import org.eclipse.hawkbit.cache.CacheWriteNotify;
import org.eclipse.hawkbit.controller.FileSteamingFailedException;
import org.eclipse.hawkbit.repository.model.Action.ActionType;
import org.eclipse.hawkbit.repository.model.LocalArtifact;
import org.eclipse.hawkbit.rest.resource.model.distributionset.ActionTypeRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.common.math.DoubleMath;
import com.google.common.net.HttpHeaders;

/**
 * Utility class for the Rest Source API.
 *
 *
 *
 *
 */
public final class RestResourceConversionHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RestResourceConversionHelper.class);

    private static final int BUFFER_SIZE = 4096;

    // utility class, private constructor.
    private RestResourceConversionHelper() {

    }

    /**
     * Write response without target relation.
     *
     * @param artifact
     *            the artifact
     * @param servletResponse
     *            to be sent back to the requesting client
     * @param request
     *            from the client
     * @param file
     *            to be write to the client response
     *
     * @return http code
     */
    public static ResponseEntity<Void> writeFileResponse(final LocalArtifact artifact,
            final HttpServletResponse servletResponse, final HttpServletRequest request, final DbArtifact file) {
        return writeFileResponse(artifact, servletResponse, request, file, null, null);
    }

    /**
     * <p>
     * Write response with target relation and publishes events concerning the
     * download progress based on given {@link UpdateActionStatus}.
     * </p>
     *
     * <p>
     * The request supports RFC7233 range requests.
     * </p>
     *
     * @param artifact
     *            the artifact
     * @param response
     *            to be sent back to the requesting client
     * @param request
     *            from the client
     * @param file
     *            to be write to the client response
     * @param cacheWriteNotify
     *            to write progress updates to
     * @param statusId
     *            of the UpdateActionStatus
     *
     * @throws IOException
     *             in case of exceptions
     *
     * @return http code
     *
     * @see https://tools.ietf.org/html/rfc7233
     */
    public static ResponseEntity<Void> writeFileResponse(final LocalArtifact artifact,
            final HttpServletResponse response, final HttpServletRequest request, final DbArtifact file,
            final CacheWriteNotify cacheWriteNotify, final Long statusId) {

        ResponseEntity<Void> result = null;

        final String etag = artifact.getSha1Hash();
        final Long lastModified = artifact.getLastModifiedAt() != null ? artifact.getLastModifiedAt()
                : artifact.getCreatedAt();
        final long length = file.getSize();

        response.reset();
        response.setBufferSize(BUFFER_SIZE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + artifact.getFilename());
        response.setHeader(HttpHeaders.ETAG, etag);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        final ByteRange full = new ByteRange(0, length - 1, length);
        final List<ByteRange> ranges = new ArrayList<>();

        // Validate and process Range and If-Range headers.
        final String range = request.getHeader("Range");
        if (range != null) {
            LOG.debug("range header for filename ({}) is: {}", artifact.getFilename(), range);

            // Range header matches"bytes=n-n,n-n,n-n..."
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + length);
                LOG.debug("range header for filename ({}) is not satisfiable: ", artifact.getFilename());
                return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }

            // RFC: if the representation is unchanged, send me the part(s) that
            // I am requesting in
            // Range; otherwise, send me the entire representation.
            checkForShortcut(request, etag, lastModified, full, ranges);

            // it seems there are valid ranges
            result = extractRange(response, length, ranges, range);
            // return if range extraction turned out to be invalid
            if (result != null) {
                return result;
            }
        }

        // full request - no range
        if (ranges.isEmpty() || ranges.get(0).equals(full)) {
            LOG.debug("filename ({}) results into a full request: ", artifact.getFilename());
            fullfileRequest(artifact, response, file, cacheWriteNotify, statusId, full);
            result = new ResponseEntity<>(HttpStatus.OK);
        }
        // standard range request
        else if (ranges.size() == 1) {
            LOG.debug("filename ({}) results into a standard range request: ", artifact.getFilename());
            standardRangeRequest(artifact, response, file, cacheWriteNotify, statusId, ranges);
            result = new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        }
        // multipart range request
        else {
            LOG.debug("filename ({}) results into a multipart range request: ", artifact.getFilename());
            multipartRangeRequest(artifact, response, file, cacheWriteNotify, statusId, ranges);
            result = new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        }

        return result;

    }

    private static void fullfileRequest(final LocalArtifact artifact, final HttpServletResponse response,
            final DbArtifact file, final CacheWriteNotify cacheWriteNotify, final Long statusId, final ByteRange full) {
        final ByteRange r = full;
        response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + r.getStart() + "-" + r.getEnd() + "/" + r.getTotal());
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(r.getLength()));

        try {
            copyStreams(file.getFileInputStream(), response.getOutputStream(), cacheWriteNotify, statusId, r.getStart(),
                    r.getLength());
        } catch (final IOException e) {
            LOG.error("fullfileRequest of file ({}) failed!", artifact.getFilename(), e);
            throw new FileSteamingFailedException(artifact.getFilename());
        }
    }

    private static ResponseEntity<Void> extractRange(final HttpServletResponse response, final long length,
            final List<ByteRange> ranges, final String range) {
        ResponseEntity<Void> result = null;
        if (ranges.isEmpty()) {
            for (final String part : range.substring(6).split(",")) {
                long start = sublong(part, 0, part.indexOf('-'));
                long end = sublong(part, part.indexOf('-') + 1, part.length());

                if (start == -1) {
                    start = length - end;
                    end = length - 1;
                } else if (end == -1 || end > length - 1) {
                    end = length - 1;
                }

                // Check if Range is syntactically valid. If not, then return
                // 416.
                if (start > end) {
                    response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + length);
                    result = new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
                    return result;
                }

                // Add range.
                ranges.add(new ByteRange(start, end, length));
            }
        }

        return null;
    }

    private static long sublong(final String value, final int beginIndex, final int endIndex) {
        final String substring = value.substring(beginIndex, endIndex);
        return substring.length() > 0 ? Long.parseLong(substring) : -1;
    }

    private static void checkForShortcut(final HttpServletRequest request, final String etag, final long lastModified,
            final ByteRange full, final List<ByteRange> ranges) {
        final String ifRange = request.getHeader(HttpHeaders.IF_RANGE);
        if (ifRange != null && !ifRange.equals(etag)) {
            try {
                final long ifRangeTime = request.getDateHeader(HttpHeaders.IF_RANGE);
                if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                    ranges.add(full);
                }
            } catch (final IllegalArgumentException ignore) {
                LOG.info("Invalid if-range header field", ignore);
                ranges.add(full);
            }
        }
    }

    private static void multipartRangeRequest(final LocalArtifact artifact, final HttpServletResponse response,
            final DbArtifact file, final CacheWriteNotify cacheWriteNotify, final Long statusId,
            final List<ByteRange> ranges) {
        response.setContentType("multipart/byteranges; boundary=" + ByteRange.MULTIPART_BOUNDARY);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        try {
            for (final ByteRange r : ranges) {
                // Add multipart boundary and header fields for every range.
                response.getOutputStream().println();
                response.getOutputStream().println("--" + ByteRange.MULTIPART_BOUNDARY);
                response.getOutputStream()
                        .println("Content-Range: bytes " + r.getStart() + "-" + r.getEnd() + "/" + r.getTotal());

                // Copy single part range of multi part range.
                copyStreams(file.getFileInputStream(), response.getOutputStream(), cacheWriteNotify, statusId,
                        r.getStart(), r.getLength());
            }

            // End with final multipart boundary.
            response.getOutputStream().println();
            response.getOutputStream().print("--" + ByteRange.MULTIPART_BOUNDARY + "--");
        } catch (final IOException e) {
            LOG.error("multipartRangeRequest of file ({}) failed!", artifact.getFilename(), e);
            throw new FileSteamingFailedException(artifact.getFilename());
        }
    }

    private static void standardRangeRequest(final LocalArtifact artifact, final HttpServletResponse response,
            final DbArtifact file, final CacheWriteNotify cacheWriteNotify, final Long statusId,
            final List<ByteRange> ranges) {
        final ByteRange r = ranges.get(0);
        response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + r.getStart() + "-" + r.getEnd() + "/" + r.getTotal());
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(r.getLength()));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        try {
            copyStreams(file.getFileInputStream(), response.getOutputStream(), cacheWriteNotify, statusId, r.getStart(),
                    r.getLength());
        } catch (final IOException e) {
            LOG.error("standardRangeRequest of file ({}) failed!", artifact.getFilename(), e);
            throw new FileSteamingFailedException(artifact.getFilename());
        }
    }

    private static long copyStreams(final InputStream from, final OutputStream to,
            final CacheWriteNotify cacheWriteNotify, final Long statusId, final long start, final long length)
                    throws IOException {
        checkNotNull(from);
        checkNotNull(to);
        final byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        int progressPercent = 1;

        // skipp until start is reached
        long skipped = 0;
        do {
            skipped += from.skip(start);
        } while (skipped < start);

        long toRead = length;
        boolean toContinue = true;

        while (toContinue) {
            final int r = from.read(buf);
            if (r == -1) {
                break;
            }

            toRead -= r;
            if (toRead > 0) {
                to.write(buf, 0, r);
                total += r;
            } else {
                to.write(buf, 0, (int) toRead + r);
                total += toRead + r;
                toContinue = false;
            }

            if (cacheWriteNotify != null) {
                final int newPercent = DoubleMath.roundToInt(total * 100.0 / length, RoundingMode.DOWN);

                // every 10 percent an event
                if (newPercent == 100 || newPercent > progressPercent + 10) {
                    progressPercent = newPercent;
                    cacheWriteNotify.downloadProgressPercent(statusId, progressPercent);
                }
            }
        }
        return total;
    }

    /**
     * Checks given CSV string for defined match value or * wildcard.
     *
     * @param matchHeader
     *            to search through
     * @param toMatch
     *            to search for
     *
     * @return <code>true</code> if string matches.
     */
    public static boolean matchesHttpHeader(final String matchHeader, final String toMatch) {
        final String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
    }

    /**
     * Convert a action rest type to a action repository type.
     * 
     * @param actionTypeRest
     *            the rest type
     * @return <null> or the action repository type
     */
    public static ActionType convertActionType(final ActionTypeRest actionTypeRest) {
        if (actionTypeRest == null) {
            return null;
        }

        switch (actionTypeRest) {
        case SOFT:
            return ActionType.SOFT;
        case FORCED:
            return ActionType.FORCED;
        case TIMEFORCED:
            return ActionType.TIMEFORCED;
        default:
            throw new IllegalStateException("Action Type is not supported");
        }

    }
}
