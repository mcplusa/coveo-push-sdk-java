package com.mcplusa.coveo.sdk;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

public class CoveoResponseException extends IOException {

    private static final long serialVersionUID = -852792425280766394L;
    private final CoveoResponse response;

    public CoveoResponseException(CoveoResponse response) throws IOException {
        super(buildMessage(response));
        this.response = response;
    }

    static String buildMessage(CoveoResponse response) throws IOException {
        String message = String.format(Locale.ROOT,
                "method [%s], URI [%s], status line [%s]",
                response.getRequestLine().getMethod(),
                response.getRequestLine().getUri(),
                response.getStatusLine().toString()
        );

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            if (entity.isRepeatable() == false) {
                entity = new BufferedHttpEntity(entity);
                response.getHttpResponse().setEntity(entity);
            }
            message += "\n" + EntityUtils.toString(entity);
        }
        return message;
    }

    /**
     * Returns the {@link CoveoResponse} that caused this exception to be thrown.
     * @return CoveoResponse
     */
    public CoveoResponse getResponse() {
        return response;
    }
}
