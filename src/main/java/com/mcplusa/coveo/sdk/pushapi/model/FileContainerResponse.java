package com.mcplusa.coveo.sdk.pushapi.model;

/**
 * FileContainerResponse
 */
public class FileContainerResponse {

    private String fileId;
    private String uploadUri;

    public FileContainerResponse(String fileId, String uploadUri) {
        this.fileId = fileId;
        this.uploadUri = uploadUri;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUploadUri() {
        return uploadUri;
    }

    public void setUploadUri(String uploadUri) {
        this.uploadUri = uploadUri;
    }

    @Override
    public String toString() {
        return "FileContainerResponse [fileId=" + fileId + ", uploadUri=" + uploadUri + "]";
    }
}