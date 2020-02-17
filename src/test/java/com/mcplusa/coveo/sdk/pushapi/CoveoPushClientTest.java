package com.mcplusa.coveo.sdk.pushapi;

import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mcplusa.coveo.sdk.CoveoEnvironment;
import com.mcplusa.coveo.sdk.CoveoResponse;
import com.mcplusa.coveo.sdk.pushapi.model.Document;
import com.mcplusa.coveo.sdk.pushapi.model.PushAPIStatus;
import java.net.URLEncoder;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Before;

public class CoveoPushClientTest {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);

    private final CoveoEnvironment env = CoveoEnvironment.PRODUCTION;
    private final String testServer = "http://localhost:8089";
    private final String accessToken = "secret-token";
    private final String organizationId = "org";
    private final String sourceId = "org-1";

    private CoveoPushClient client;

    @Before
    public void before() {
        client = new CoveoPushClient(HttpClients.createDefault(), accessToken, organizationId, sourceId, testServer);
    }

    @BeforeClass
    public static void beforeClass() {
        wireMockRule.start();
    }

    @AfterClass
    public static void afterClass() {
        wireMockRule.shutdown();
    }

    /**
     * Test of setSourceStatus method, of class CoveoPushClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetSourceStatus() throws Exception {
        stubFor(post(urlEqualTo("/push/v1/organizations/" + organizationId + "/sources/" + sourceId + "/status?statusType=" + PushAPIStatus.IDLE))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withHeader("Accept", WireMock.equalTo("application/json"))
                .withHeader("Authorization", WireMock.equalTo("Bearer " + this.accessToken))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(201).withBody("done")));

        CoveoResponse result = client.updateSourceStatus(PushAPIStatus.IDLE);
        assertEquals(HttpStatus.SC_CREATED, result.getStatusLine().getStatusCode());
    }

    /**
     * Test of pushSingleDocument method, of class CoveoPushClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPushSingleDocument() throws Exception {
        Document document = new Document("file://doc");

        stubFor(put(urlEqualTo("/push/v1/organizations/" + organizationId + "/sources/" + sourceId + "/documents?documentId=" + URLEncoder.encode("file://doc", "UTF-8")))
                .withRequestBody(WireMock.equalTo(document.toJson()))
                .withHeader("Authorization", WireMock.equalTo("Bearer " + this.accessToken))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(202).withBody("done")));

        CoveoResponse result = client.pushSingleDocument(document);
        assertEquals(HttpStatus.SC_ACCEPTED, result.getStatusLine().getStatusCode());
    }
}
