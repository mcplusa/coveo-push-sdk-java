package com.mcplusa.coveo.sdk.pushapi.model;

import java.util.ArrayList;
import java.util.List;

public class PermissionsSetsModel {

    private boolean allowAnonymous;
    private List<IdentityModel> allowedPermissions;
    private List<IdentityModel> deniedPermissions;

    public PermissionsSetsModel() {
        this.allowAnonymous = false;
        this.allowedPermissions = new ArrayList<>();
        this.deniedPermissions = new ArrayList<>();
    }

    public PermissionsSetsModel(boolean allowAnonymous) {
        this();
        this.allowAnonymous = allowAnonymous;
    }

    public PermissionsSetsModel(boolean allowAnonymous, List<IdentityModel> allowedPermissions) {
        this();
        this.allowAnonymous = allowAnonymous;
        this.allowedPermissions = allowedPermissions;
    }

    public PermissionsSetsModel(boolean allowAnonymous, List<IdentityModel> allowedPermissions, List<IdentityModel> deniedPermissions) {
        this.allowAnonymous = allowAnonymous;
        this.allowedPermissions = allowedPermissions;
        this.deniedPermissions = deniedPermissions;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }

    public void addAllowedPermission(IdentityModel identity) {
        this.allowedPermissions.add(identity);
    }

    public List<IdentityModel> getAllowedPermissions() {
        return allowedPermissions;
    }

    public void setAllowedPermissions(List<IdentityModel> allowedPermissions) {
        this.allowedPermissions = allowedPermissions;
    }
    
    public void addDeniedPermission(IdentityModel identity) {
        this.deniedPermissions.add(identity);
    }

    public List<IdentityModel> getDeniedPermissions() {
        return deniedPermissions;
    }

    public void setDeniedPermissions(List<IdentityModel> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    @Override
    public String toString() {
        return "PermissionsSetsModel{" + "allowAnonymous=" + allowAnonymous + ", allowedPermissions=" + allowedPermissions + ", deniedPermissions=" + deniedPermissions + '}';
    }
}
