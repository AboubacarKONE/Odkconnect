package com.odk.connect.constants;

public class SecurityConstant {
	public static final long EXPIRATION_TIME = 432000000; // 5 days expressed in milliseconds 5*24*60*60*1000
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token ne peut pas être vérifié";
	public static final String GET_COMPAGNY_LLC = "OdkConnect, LLC";
	public static final String GET_ARRAYS_ADMINISTRATION = "Portail de gestion des alumnis";
	public static final String AUTHORITIES = "authorities";
	public static final String FORBIDDEN_MESSAGE = "Vous devez vous connecter pour accéder à cette page";
	public static final String ACCESS_DENIED_MESSAGE = "Vous n'avez pas la permission d'accéder à cette page";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String[] PUBLIC_URLS = { "/odkConnect/user/login", "/odkConnect/user/registerUser",
			"/odkConnect/user/registerAlumni", "/odkConnect/log/saveLog", "/odkConnect/user/image/**", "/odkConnect/forum/category/image/**",
			"/odkConnect/forum/quiz/image/**","/odkConnect/forum/response/image/**","/odkConnect/media/image/**"};
//	public static final String[] PUBLIC_URLS = { "**" };
}
