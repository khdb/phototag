package com.khoahuy.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class TokenAsyncTask extends AsyncTask<Void, Void, String> {

	private List<NameValuePair> nameValuePairs;
	private String URL;
	
	public TokenAsyncTask(Context context, String url, String grantType,
			String clientID, String clientSecret, String username, String password) {
		URL = url;
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("client_id", clientID));
		nameValuePairs
				.add(new BasicNameValuePair("client_secret", clientSecret));
		nameValuePairs.add(new BasicNameValuePair("grant_type", grantType));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
	}
	
	public TokenAsyncTask(Context context, String url, String grantType,
			String clientID, String clientSecret, String refreshToken) {
		URL = url;
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("client_id", clientID));
		nameValuePairs
				.add(new BasicNameValuePair("client_secret", clientSecret));
		nameValuePairs.add(new BasicNameValuePair("grant_type", grantType));
		nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
		
	}

	@Override
	protected String doInBackground(Void... params) {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);	
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httppost = new HttpPost(URL);

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));	
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				return EntityUtils.toString(responseEntity);	
			}		

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (Exception e) {
			Log.e("Huy", e.getMessage());
		}
		return "Error";
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
	}

}