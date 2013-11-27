package com.khoahuy.phototag.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.khoahuy.utils.ConstantUtils;

public class Token {

	private String accessToken;
	private Context context;
	
	public Token(Context context)
	{
		this.context = context;
	}

	private HttpPost getTokenRequest()
			throws UnsupportedEncodingException {

		String url = ConstantUtils.TOKEN_URL;
		List<NameValuePair> nameValuePairs;

		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("client_id",
				ConstantUtils.CLIENT_ID));
		nameValuePairs.add(new BasicNameValuePair("client_secret",
				ConstantUtils.CLIENT_SECRET));
		nameValuePairs.add(new BasicNameValuePair("grant_type",
				ConstantUtils.GRANT_TYPE_PASSWORD));
		nameValuePairs.add(new BasicNameValuePair("username",
				ConstantUtils.CLIENT_USERNAME));
		nameValuePairs.add(new BasicNameValuePair("password",
				ConstantUtils.CLIENT_PASSWORD));
		return createPostRequest(url, nameValuePairs);

	}

	public String getAccessToken(boolean force) {
		this.context = context;
		SharedPreferences settings = context.getSharedPreferences(
				ConstantUtils.PREFS_NAME, Context.MODE_PRIVATE);

		accessToken = settings.getString(ConstantUtils.ACCESS_TOKEN, null);

		if (accessToken != null && force == false)
			return accessToken;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = getTokenRequest();
			HttpResponse response = httpclient.execute(httppost);
			handlerResponse(response);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				return saveTokenFromJson(EntityUtils.toString(responseEntity));
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (Exception e) {
			Log.e("Huy", e.getMessage());
			Toast.makeText(context, "Can not authorization", Toast.LENGTH_SHORT).show();
		}
		return "";
	}

	private void handlerResponse(HttpResponse reponse) throws Exception{
		switch (reponse.getStatusLine().getStatusCode()) {
		case HttpStatus.SC_OK:
			break;
		default:
			throw new Exception("Request have some problems...");
		}
	}
	
	private String saveTokenFromJson(String jsonString) throws JSONException {
		Log.d("Token", "Save token Token: " + jsonString);
		JSONObject tokenObject = new JSONObject(jsonString);
		String accessToken = tokenObject.getString(ConstantUtils.ACCESS_TOKEN);
		String refreshToken = tokenObject
				.getString(ConstantUtils.REFRESH_TOKEN);
		String tokenType = tokenObject.getString(ConstantUtils.TOKEN_TYPE);
		String scope = tokenObject.getString(ConstantUtils.SCOPE);

		SharedPreferences settings = context.getSharedPreferences(
				ConstantUtils.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ConstantUtils.ACCESS_TOKEN, accessToken);
		editor.putString(ConstantUtils.REFRESH_TOKEN, refreshToken);
		editor.putString(ConstantUtils.TOKEN_TYPE, tokenType);
		editor.putString(ConstantUtils.SCOPE, scope);
		editor.commit();
		Log.d("Token", "Receive and save Token complete");
		return accessToken;
	}

	private HttpPost createPostRequest(String url,
			List<NameValuePair> nameValuePairs)
			throws UnsupportedEncodingException {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		return httppost;

	}
}
