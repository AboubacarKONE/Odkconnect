package com.odk.connect.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangerMotDePasseUser {
	private Long id;
	private String ancienmotdepasse;
	private String MotDePasse;
	private String confirmeMotDePasse;
}
