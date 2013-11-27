package com.khoahuy.utils;

public class ConstantUtils {
	
	//Request token params
	public static final String CLIENT_ID = "instapart";
	public static final String CLIENT_SECRET = "123456789";
	public static final String CLIENT_USERNAME = "admin";
	public static final String CLIENT_PASSWORD = "admin";
	public static final String GRANT_TYPE_PASSWORD = "password";
	public static final String GRANT_TYPE_REFRESH = "refresh_token";
	
	//Request URL 
	public static final String DOMAIN = "http://192.168.10.41:8000/"; 
	public static final String TOKEN_URL =  DOMAIN + "o/token/";
	public static final String WAITINGITEM_URL = DOMAIN + "waitings/";
	public static final String USED_ITEM_URL = DOMAIN + "useds/";
	public static final String STATIC_URL = DOMAIN + "static/";
	
	//Token retrun object:
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String TOKEN_TYPE = "token_type";
	public static final String SCOPE = "scope";
	
	//Setting
	public static final String PREFS_NAME = "instaPrefs";
	public static final String BEARER = "bearer";
		
}
