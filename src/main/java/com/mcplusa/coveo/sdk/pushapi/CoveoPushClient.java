package com.mcplusa.coveo.sdk.pushapi;

import com.google.gson.Gson;
import com.mcplusa.coveo.sdk.CoveoClient;
import com.mcplusa.coveo.sdk.CoveoEnvironment;
import com.mcplusa.coveo.sdk.CoveoResponse;
import com.mcplusa.coveo.sdk.CoveoResponseException;
import com.mcplusa.coveo.sdk.pushapi.model.Document;
import com.mcplusa.coveo.sdk.pushapi.model.FileContainerResponse;
import com.mcplusa.coveo.sdk.pushapi.model.PushAPIStatus;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoveoPushClient extends CoveoClient {

    static final Logger log = LoggerFactory.getLogger(CoveoPushClient.class);

    private final String organizationId;
    private final String sourceId;
    private final String host;
    private final String baseUrl;
    private final CloseableHttpClient client;

    public CoveoPushClient(CloseableHttpClient client, String accessToken, String organizationId, String sourceId, CoveoEnvironment environment) {
        this.client = client;
        this.accessToken = accessToken;
        this.environment = environment;
        this.organizationId = organizationId;
        this.sourceId = sourceId;
        this.host = getHost();
        this.baseUrl = host + "/push/v1/organizations/" + organizationId + "/sources/" + sourceId;
    }
    
    public CoveoPushClient(CloseableHttpClient client, String accessToken, String organizationId, String sourceId, String host) {
        this.client = client;
        this.accessToken = accessToken;
        this.organizationId = organizationId;
        this.sourceId = sourceId;
        this.host = host;
        this.baseUrl = host + "/push/v1/organizations/" + organizationId + "/sources/" + sourceId;
    }

    @Override
    public CoveoResponse ping() throws IOException {
        return this.updateSourceStatus(PushAPIStatus.IDLE);
    }
    
    /**
     * Get the host based on the environment
     * @return coveo host
     */
    @Override
    public String getHost() {
        switch (this.environment) {
            case PRODUCTION:
                return "https://api.cloud.coveo.com";
            case HIPAA:
                return "https://apihipaa.cloud.coveo.com";
            case DEVELOPMENT:
                return "https://apidev.cloud.coveo.com";
            case STAGING:
                return "https://apiqa.cloud.coveo.com";
            default:
                return "https://api.cloud.coveo.com";
        }
    }

    /**
     * Modifies the current status of a Push source. This operation allows you
     * to update the activity logs of a Push source (and consequently the
     * activity indicators in the Coveo Cloud V2 administration console).
     * Pushing an active source status (i.e., REBUILD, REFRESH, or INCREMENTAL)
     * creates an activity. Pushing the IDLE status terminates the ongoing
     * activity and marks it as completed.
     *
     * @param status
     * @return
     * @throws CoveoResponseException
     */
    public CoveoResponse updateSourceStatus(PushAPIStatus status) throws IOException {
        String uri = this.baseUrl + "/status?statusType=" + status.toString();
        HttpPost request = new HttpPost(uri);
        setDefaultHeaders(request);

        log.debug("Setting Source Status: {}", status);

        HttpResponse response = null;
        try {
            response = client.execute(request);

            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                return respOrEx.response;
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Error updating source status", ex);
            throw ex;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    /**
     * Adds or updates an individual item in a Push source.
     *
     * @param document The document that will be added or updated
     * @return
     * @throws CoveoResponseException
     */
    public CoveoResponse pushSingleDocument(Document document) throws IOException {
        String uri = null;
        try {
            uri = this.baseUrl + "/documents?documentId=" + URLEncoder.encode(document.getDocumentId(), "UTF-8");

            if (document.getOrderingId() != null) {
                uri += "&orderingId=" + document.getOrderingId();
            }

            if (document.getCompressionType() != null) {
                uri += "&compressionType=" + document.getCompressionType().toString();
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("Couldn't encode document id");
        }

        log.debug("Pushing document: {}", document.getDocumentId());

        HttpPut request = new HttpPut(uri);
        setDefaultHeaders(request);
        request.setEntity(new StringEntity(document.toJson(), ContentType.APPLICATION_JSON));
        HttpResponse response = null;
        try {
            response = client.execute(request);

            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                return respOrEx.response;
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Error pushing single document", ex);
            throw ex;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    /**
     * Delete the specified documentId on Coveo
     *
     * @param documentId the Coveo documentId
     * @return CoveoResponse instance.
     * @throws java.io.IOException
     */
    public CoveoResponse deleteDocument(String documentId) throws IOException {
        HttpDelete request = null;
        HttpResponse response = null;
        try {
            String uri = this.baseUrl + "/documents?documentId=" + URLEncoder.encode(documentId, "UTF-8");
            request = new HttpDelete(uri);
            setDefaultHeaders(request);

            log.debug("Deleting document: {}", documentId);

            response = client.execute(request);
            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                return respOrEx.response;
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Error deleting document", ex);
            throw ex;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    /**
     * Get Coveo Files API for a pre-signed AWS S3 URL, and a fileID for referencing
     * it in the JSON Document.
     *
     * @return FileContainerResponse instance.
     * @throws IOException
     */
    public FileContainerResponse getFileContainer() throws IOException {
        String uri = this.host + "/push/v1/organizations/" + this.organizationId + "/files";
        HttpPost request = new HttpPost(uri);
        setDefaultHeaders(request);

        log.debug("Getting File Container...");

        HttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = client.execute(request);

            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                entity = respOrEx.response.getEntity();
                String fileResponse = EntityUtils.toString(entity);
                return new Gson().fromJson(fileResponse, FileContainerResponse.class);
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Unable to get a File Container", ex);
            throw ex;
        } finally {
            request.releaseConnection();

            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
        }
    }

    /**
     * PUT file on S3.
     *
     * @param in        the file InputStream
     * @param uploadUri a pre-signed AWS S3 upload url
     * @return CoveoResponse instance.
     * @throws IOException
     */
    public CoveoResponse pushFileOnS3(InputStream in, String uploadUri) throws IOException {
        HttpPut request = new HttpPut(uploadUri);

        // Set special headers for S3
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        request.setHeader("x-amz-server-side-encryption", "AES256");

        InputStreamEntity entity = new InputStreamEntity(in, in.available(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        HttpResponse response = null;
        try {
            response = client.execute(request);

            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                return respOrEx.response;
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Unable to upload file to S3", ex);
            throw ex;
        } finally {
            request.releaseConnection();
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * BATCH: Adds, updates, and/or deletes a large number of encrypted items in a
     * Push source with a single request.
     *
     * @param fileId The unique identifier of the file, this file is from File
     *               Container.
     * @return CoveoResponse instance.
     * @throws IOException
     */
    public CoveoResponse pushDocumentsBatch(String fileId) throws IOException {
        String uri = this.baseUrl + "/documents/batch?fileId=" + fileId;
        HttpPut request = new HttpPut(uri);
        setDefaultHeaders(request);

        log.debug("Pushing batch file {}: {}", fileId, uri);

        HttpResponse response = null;
        try {
            response = client.execute(request);

            ResponseOrResponseException respOrEx = convertResponse(request.getRequestLine(), response);
            if (respOrEx.responseException == null) {
                return respOrEx.response;
            } else {
                throw respOrEx.responseException;
            }
        } catch (IOException ex) {
            log.error("Error pushing the batch of documents", ex);
            throw ex;
        } finally {
            request.releaseConnection();
        }
    }

    private void setDefaultHeaders(HttpRequestBase http) {
        http.setHeader(HttpHeaders.ACCEPT, "application/json");
        http.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        http.setHeader(this.getAuthorizationHeader());
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getSourceId() {
        return sourceId;
    }
}
