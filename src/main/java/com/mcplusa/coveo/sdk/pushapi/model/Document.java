package com.mcplusa.coveo.sdk.pushapi.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Document {

    private final static DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final String documentId;
    private String title;
    private String data;
    private String fileExtension;
    private String parentId;
    private Long orderingId;
    private LocalDateTime date;
    private LocalDateTime createddate;
    private CompressionType compressionType;
    private Map<String, Object> metadata = new HashMap<>();
    private List<PermissionsSetsModel> permissions;

    public Document(String documentId) {
        this.documentId = documentId;
    }

    public Document(String documentId, String data) {
        this.documentId = documentId;
        this.data = data;
    }

    public Document(String documentId, String data, String title) {
        this.documentId = documentId;
        this.data = data;
        this.title = title;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getOrderingId() {
        return orderingId;
    }

    public void setOrderingId(Long orderingId) {
        this.orderingId = orderingId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getCreateddate() {
        return createddate;
    }

    public void setCreateddate(LocalDateTime createddate) {
        this.createddate = createddate;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public <T> void addMetadata(String key, T value, Class<T> valueType) {
        this.metadata.put(key, value);
    }

    public <T> T getMetadata(String key, Class<T> valueType) {
        return (T) this.metadata.get(key);
    }

    public List<PermissionsSetsModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionsSetsModel> permissions) {
        this.permissions = permissions;
    }

    /**
     * Parse the object to JsonObject
     *
     * @return JsonObject
     */
    public String toJson() {
        Gson gson = new Gson();
        JsonObject docJson = new JsonObject();
        docJson.addProperty("documentId", documentId);
        docJson.addProperty("title", title);
        docJson.addProperty("data", data);
        docJson.addProperty("fileExtension", fileExtension);
        docJson.addProperty("parentId", parentId);
        docJson.add("permissions", gson.toJsonTree(permissions));

        if (date != null) {
            docJson.addProperty("date", DATE_FMT.format(date));
        }

        if (date != null) {
            docJson.addProperty("createddate", DATE_FMT.format(createddate));
        }

        if (compressionType != null) {
            docJson.addProperty("compressionType", compressionType.toString());
        }

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Number) {
                docJson.addProperty(key, (Number) value);
            } else if (value instanceof LocalDateTime) {
                docJson.addProperty(key, DATE_FMT.format((LocalDateTime) value));
            } else if (value instanceof List<?>) {
                JsonArray list = (JsonArray) gson.toJsonTree(value, new TypeToken<List>() {
                }.getType());
                docJson.add(key, list);
            } else {
                docJson.addProperty(key, value.toString());
            }
        }

        return gson.toJson(docJson);
    }

    @Override
    public String toString() {
        return "Document{" + "documentId=" + documentId + ", title=" + title + ", data=" + data + ", fileExtension=" + fileExtension + ", metadata=" + metadata + ", permissions=" + permissions + '}';
    }
}
