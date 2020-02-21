package com.mcplusa.coveo.sdk.pushapi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DocumentTest {

    /**
     * Test of toJson method, of class Document.
     */
    @Test
    public void testToJson() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        Document doc = new Document("file://my-doc");
        doc.setTitle("Document Title");
        doc.setFileExtension(".html");
        doc.setData("<body><h1>Title</h1></body>");
        doc.setDate(LocalDateTime.parse("1994-08-23T04:02:01", formatter));
        doc.setCreateddate(LocalDateTime.parse("2013-09-21T14:42:21", formatter));
        doc.addMetadata("list_numbers", Arrays.asList(1, 2, 3), List.class);
        doc.addMetadata("list_string", Arrays.asList("a", "b", "c"), List.class);
        doc.addMetadata("number", 1234567, Integer.class);
        doc.addMetadata("string", "abcdefg", String.class);

        String expResult = "{\"documentId\":\"file://my-doc\",\"title\":\"Document Title\",\"data\":\"\\u003cbody\\u003e\\u003ch1\\u003eTitle\\u003c/h1\\u003e\\u003c/body\\u003e\",\"fileExtension\":\".html\",\"date\":\"1994-08-23T04:02:01.000Z\",\"createddate\":\"2013-09-21T14:42:21.000Z\",\"number\":1234567,\"list_string\":[\"a\",\"b\",\"c\"],\"string\":\"abcdefg\",\"list_numbers\":[1,2,3]}";
        String result = doc.toJson();
        assertEquals(expResult, result);
    }

    /**
     * Test of toJson method with empty values, of class Document.
     */
    @Test
    public void testToJsonEmpty() {
        Document doc = new Document("file://my-doc");
        doc.setData("My data");

        String expResult = "{\"documentId\":\"file://my-doc\",\"data\":\"My data\"}";
        String result = doc.toJson();
        assertEquals(expResult, result);
    }

    /**
     * Test of toJson method with permissions, of class Document.
     */
    @Test
    public void testToJsonPermissions() {
        Document doc = new Document("file://my-secret-doc");
        doc.setData("My secure data");

        PermissionsSetsModel permission = new PermissionsSetsModel();
        permission.addAllowedPermission(new IdentityModel(IdentityType.GROUP, "My Group"));
        permission.addAllowedPermission(new IdentityModel(IdentityType.USER, "user@example.com"));

        doc.setPermissions(Arrays.asList(permission));

        String expResult = "{\"documentId\":\"file://my-secret-doc\",\"data\":\"My secure data\",\"permissions\":[{\"allowAnonymous\":false,\"allowedPermissions\":[{\"identityType\":\"GROUP\",\"securityProvider\":\"My Group\"},{\"identityType\":\"USER\",\"securityProvider\":\"user@example.com\"}],\"deniedPermissions\":[]}]}";
        String result = doc.toJson();
        assertEquals(expResult, result);
    }
}
