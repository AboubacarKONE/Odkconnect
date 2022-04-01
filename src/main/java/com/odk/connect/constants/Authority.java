package com.odk.connect.constants;

public class Authority {
	public static final String[] ALUM_AUTHORITIES = { "user:read"};
	public static final String[] FORMATEUR_AUTHORITIES = { "user:read", "promotion:update" };
	public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create" };
	public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:delete", };

}
