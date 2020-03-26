package com.mcplusa.coveo.sdk.pushapi.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * BatchRequest
 */
public class BatchRequest {

    private List<JsonObject> addOrUpdate;
    private List<DeleteDocument> delete;

    public BatchRequest() {
        this.addOrUpdate = new ArrayList<>();
        this.delete = new ArrayList<>();
    }

    /**
     * Add the given document to the addOrUpdate array.
     * 
     * @param document to being added or updated.
     */
    public void pushDocument(Document document) {
        this.addOrUpdate.add(new Gson().fromJson(document.toJson(), JsonObject.class));
    }

    /**
     * Add the given document to the delete array.
     * 
     * @param document to being deleted.
     */
    public void deleteDocument(DeleteDocument document) {
        this.delete.add(document);
    }

    /**
     * Add the given document to the delete array.
     * 
     * @param documentId     of the document to being deleted.
     * @param deleteChildren to delete all the children of the document.
     */
    public void deleteDocument(String documentId, boolean deleteChildren) {
        this.delete.add(new DeleteDocument(documentId, deleteChildren));
    }

    public List<JsonObject> getAddOrUpdate() {
        return addOrUpdate;
    }

    public void setAddOrUpdate(List<JsonObject> addOrUpdate) {
        this.addOrUpdate = addOrUpdate;
    }

    public List<DeleteDocument> getDelete() {
        return delete;
    }

    public void setDelete(List<DeleteDocument> delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "BatchRequest [addOrUpdate=" + addOrUpdate + ", delete=" + delete + "]";
    }
}