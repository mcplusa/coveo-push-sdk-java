/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcplusa.coveo.sdk;

import java.io.IOException;
import java.util.Objects;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoveoClient implements ICoveoClient {

    final static Logger log = LoggerFactory.getLogger(CoveoClient.class);
    
    /**
     * Access token of the Source
     */
    protected String accessToken;
    
    protected CoveoEnvironment environment;

    @Override
    public abstract CoveoResponse ping() throws IOException;
    
    public abstract String getHost();
    
    protected Header getAuthorizationHeader() {
        return new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
    }

    /**
     * Closes response
     *
     * @param response current HttpResponse
     */
    protected void closeResponse(CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
        } catch (IOException ex) {
            log.error("Couldn't close the response", ex);
        }
    }

    /**
     * Convert response to check if it is successful.
     *
     * @param requestLine
     * @param httpResponse
     * @return ResponseOrResponseException that will contain the response if its
     * successful, otherwise will return an CoveoResponseException.
     * @throws IOException
     */
    protected ResponseOrResponseException convertResponse(RequestLine requestLine, HttpResponse httpResponse) throws IOException {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        CoveoResponse response = new CoveoResponse(requestLine, httpResponse);

        if (isSuccessfulResponse(statusCode)) {
            return new ResponseOrResponseException(response);
        }

        CoveoResponseException responseException = new CoveoResponseException(response);
        return new ResponseOrResponseException(responseException);
    }

    /**
     * Check if the status code is successful
     *
     * @param statusCode
     * @return true if the status code is successful (2xx)
     */
    protected static boolean isSuccessfulResponse(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    protected static class ResponseOrResponseException {

        public final CoveoResponse response;
        public final CoveoResponseException responseException;

        ResponseOrResponseException(CoveoResponse response) {
            this.response = Objects.requireNonNull(response);
            this.responseException = null;
        }

        ResponseOrResponseException(CoveoResponseException responseException) {
            this.responseException = Objects.requireNonNull(responseException);
            this.response = null;
        }
    }

}
