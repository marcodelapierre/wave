package io.seqera.wave.storage;

import java.io.IOException;
import java.nio.file.Path;

import io.seqera.wave.storage.reader.ContentReader;
import io.seqera.wave.storage.reader.PathContentReader;

/**
 * Implements a digest store that laods the binary content on-demand
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class LazyDigestStore implements DigestStore{

    final private String mediaType;
    final private String digest;
    final private ContentReader contentReader;

    LazyDigestStore(ContentReader content, String mediaType, String digest) {
        this.contentReader = content;
        this.mediaType = mediaType;
        this.digest = digest;
    }

    LazyDigestStore(Path content, String mediaType, String digest) {
        this.contentReader = new PathContentReader(content);
        this.mediaType = mediaType;
        this.digest = digest;
    }

    @Override
    public byte[] getBytes() {
        try {
            return contentReader !=null ? contentReader.readAllBytes() : null;
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to load digest content at path: "+ contentReader, e);
        }
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public String getDigest() {
        return digest;
    }

}