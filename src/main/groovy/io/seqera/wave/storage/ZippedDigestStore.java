/*
 *  Copyright (c) 2023, Seqera Labs.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *  This Source Code Form is "Incompatible With Secondary Licenses", as
 *  defined by the Mozilla Public License, v. 2.0.
 */

package io.seqera.wave.storage;

import java.util.Base64;

import io.seqera.wave.util.ZipUtils;

/**
 * Implements a digest store compress/decompression on-demand
 * the byte array content to retain as less as possible memory
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class ZippedDigestStore implements DigestStore{

    final private byte[] bytes;
    final private String mediaType;
    final private String digest;
    final Integer size;

    public ZippedDigestStore(byte[] bytes, String mediaType, String digest, int size) {
        this.bytes = ZipUtils.compress(bytes);
        this.mediaType = mediaType;
        this.digest = digest;
        this.size = size;
    }

    public byte[] getBytes() {
        return ZipUtils.decompressAsBytes(bytes);
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getDigest() {
        return digest;
    }

    public Integer getSize() {
        return size;
    }

    public String toString() {
        return String.format("ZippedDigestStore(mediaType=%s; digest=%s; size=%d; bytesBase64=%s)", mediaType, digest, size, new String(Base64.getEncoder().encode(bytes)));
    }

    @Override
    public String toLogString() {
        return String.format("ZippedDigestStore(digest=%s; size=%d; mediaType=%s; bytes=<omitted>)", digest, size, mediaType);
    }
}