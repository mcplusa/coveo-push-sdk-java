package com.mcplusa.coveo.sdk;

import com.mcplusa.coveo.sdk.pushapi.CoveoPushClient;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating Coveo client objects.
 */
public class CoveoFactory implements Closeable {

    static final Logger log = LoggerFactory.getLogger(CoveoFactory.class);

    /**
     * The target environment
     */
    private final CoveoEnvironment environment;

    /**
     * HTTP Client with default headers
     */
    private final CloseableHttpClient httpClient;

    /**
     * Instantiates a new {@link CoveoFactory}, default environment will be PRODUCTION.
     *
     */
    public CoveoFactory() {
        this.environment = CoveoEnvironment.PRODUCTION;
        this.httpClient = createHttpClient();
    }

    /**
     * Instantiates a new {@link CoveoFactory}.
     *
     * @param environment The target environment. automatically targets the
     * associated host.
     */
    public CoveoFactory(CoveoEnvironment environment) {
        this.environment = environment;
        this.httpClient = createHttpClient();
    }

    /**
     * Instantiates a new {@link CoveoPushClient}
     *
     * @param accessToken access token of the Source
     * @param orgId The unique identifier of the target organization.
     * @param sourceId The unique identifier of the target source.
     * @return instance of {@link CoveoPushClient}
     */
    public CoveoPushClient newPushClient(String accessToken, String orgId, String sourceId) {
        return new CoveoPushClient(httpClient, accessToken, orgId, sourceId, environment);
    }

    /**
     * Setup default {@link CloseableHttpClient}
     */
    private CloseableHttpClient createHttpClient() {
        SSLContext sslcontext = SSLContexts.createSystemDefault();
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setSSLContext(sslcontext);
        return httpClientBuilder.build();
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

}
