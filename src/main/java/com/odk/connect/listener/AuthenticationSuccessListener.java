package com.odk.connect.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.odk.connect.model.UserPrincipal;
import com.odk.connect.service.LogginAttemptService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
	private final LogginAttemptService loginAttemptService;

	@EventListener
	public void onAuthenticationSucces(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof UserPrincipal) {
			UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(userPrincipal.getUsername());
		}
	}

}
