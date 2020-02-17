package com.mcplusa.coveo.sdk.pushapi.model;

/**
 * The type of compression, if the content is being pushed as compressed binary data
 */
public enum CompressionType {
    UNCOMPRESSED,
    DEFLATE,
    GZIP,
    LZMA,
    ZLIB
}
