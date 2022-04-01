package com.odk.connect.enumeration;

import static com.odk.connect.constants.Authority.*;

public enum Role {
	ROLE_ALUM(ALUM_AUTHORITIES),ROLE_FORMATEUR(FORMATEUR_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES), ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;

	Role(String... authorities) {
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
}
