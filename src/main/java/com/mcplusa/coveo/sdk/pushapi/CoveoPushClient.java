package com.mcplusa.coveo.sdk.pushapi;

import com.mcplusa.coveo.sdk.CoveoClient;
import com.mcplusa.coveo.sdk.CoveoEnvironment;
import com.mcplusa.coveo.sdk.CoveoResponse;
import com.mcplusa.coveo.sdk.CoveoResponseException;
import com.mcplusa.coveo.sdk.pushapi.model.Document;
import com.mcplusa.coveo.sdk.pushapi.model.PushAPIStatus;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoveoPushClient extends CoveoClient {

    final static Logger log = LoggerFactory.getLogger(CoveoPushClient.class);

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
