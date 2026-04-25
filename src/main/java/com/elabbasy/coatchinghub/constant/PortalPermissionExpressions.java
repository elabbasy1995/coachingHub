package com.elabbasy.coatchinghub.constant;

public final class PortalPermissionExpressions {

    public static final String ADMINS = "hasAuthority('ADMINS')";
    public static final String COACHES = "hasAuthority('COACHES')";
    public static final String BOOKING = "hasAuthority('BOOKING')";
    public static final String COUPONS = "hasAuthority('COUPONS')";
    public static final String COACHES_OR_BOOKING = "hasAnyAuthority('COACHES', 'BOOKING')";

    private PortalPermissionExpressions() {
    }
}
