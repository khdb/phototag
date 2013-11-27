package com.khoahuy.utils;

public class ConstantUtils {
	
	//Request token params
	public static final String CLIENT_ID = "instapart";
	public static final String CLIENT_SECRET = "123456789";
	public static final String CLIENT_USERNAME = "admin";
	public static final String CLIENT_PASSWORD = "admin";
	public static final String GRANT_TYPE_PASSWORD = "password";
	public static final String GRANT_TYPE_REFRESH = "refresh";
	
	//Request URL 
	public static final String DOMAIN = "http://192.168.1.169:8000/"; 
	public static final String TOKEN_URL =  DOMAIN + "o/token/";
	public static final String WAITINGITEM_URL = DOMAIN + "waitings/";
	public static final String USED_ITEM = DOMAIN + "useds/";
	
	
	//Token retrun object:
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "access_token";
	public static final String EXPIRES_IN = "access_token";
	public static final String TOKEN_TYPE = "access_token";
	public static final String SCOPE = "access_token";
	
	//Setting
	public static final String PREFS_NAME = "instaPrefs";
	public static final String BEARER = "bearer";
		
}
