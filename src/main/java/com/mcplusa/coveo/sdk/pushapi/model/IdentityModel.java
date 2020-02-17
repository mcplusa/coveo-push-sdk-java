package com.mcplusa.coveo.sdk.pushapi.model;

public class IdentityModel {

    private IdentityType identityType;
    private String securityProvider;

    public IdentityModel(IdentityType identityType, String securityProvider) {
        this.identityType = identityType;
        this.securityProvider = securityProvider;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    public String getSecurityProvider() {
        return securityProvider;
    }

    public void setSecurityProvider(String securityProvider) {
        this.securityProvider = securityProvider;
    }

    @Override
    public String toString() {
        return "PermissionsIdentityModel{" + "identityType=" + identityType + ", securityProvider=" + securityProvider + '}';
    }
}
