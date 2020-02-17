package com.mcplusa.coveo.sdk;

import java.util.Objects;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;

/**
 * Holds an Coveo response. It wraps the {@link HttpResponse} returned and
 * associates it with its corresponding {@link RequestLine} and
 * {@link HttpHost}.
 */
public class CoveoResponse {

    private final RequestLine requestLine;
    private final HttpResponse response;

    public CoveoResponse(RequestLine requestLine, HttpResponse response) {
        Objects.requireNonNull(requestLine, "requestLine cannot be null");
        Objects.requireNonNull(response, "response cannot be null");
        this.requestLine = requestLine;
        this.response = response;
    }

    /**
     * Returns the request line that generated this response
     */
    public RequestLine getRequestLine() {
        return requestLine;
    }

    /**
     * Returns the status line of the current response
     */
    public StatusLine getStatusLine() {
        return response.getStatusLine();
    }

    /**
     * Returns all the response headers
     */
    public Header[] getHeaders() {
        return response.getAllHeaders();
    }

    /**
     * Returns the value of the first header with a specified name of this
     * message. If there is more than one matching header in the message the
     * first element is returned. If there is no matching header in the message
     * <code>null</code> is returned.
     */
    public String getHeader(String name) {
        Header header = response.getFirstHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    /**
     * Returns the response body available, null otherwise
     *
     * @see HttpEntity
     */
    public HttpEntity getEntity() {
        return response.getEntity();
    }

    HttpResponse getHttpResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Response{"
                + "requestLine=" + requestLine
                + ", response=" + response.getStatusLine()
                + '}';
    }
}
