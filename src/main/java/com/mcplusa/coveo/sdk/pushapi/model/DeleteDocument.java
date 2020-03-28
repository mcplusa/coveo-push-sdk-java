package com.mcplusa.coveo.sdk.pushapi.model;

/**
 * DeleteDocument
 */
public class DeleteDocument {

    private String documentId;
    private boolean deleteChildren;

    public DeleteDocument(String documentId) {
        this.documentId = documentId;
        this.deleteChildren = false;
    }

    public DeleteDocument(String documentId, boolean deleteChildren) {
        this.documentId = documentId;
        this.deleteChildren = deleteChildren;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isDeleteChildren() {
        return deleteChildren;
    }

    public void setDeleteChildren(boolean deleteChildren) {
        this.deleteChildren = deleteChildren;
    }

    @Override
    public String toString() {
        return "DeleteDocument [deleteChildren=" + deleteChildren + ", documentId=" + documentId + "]";
    }
}