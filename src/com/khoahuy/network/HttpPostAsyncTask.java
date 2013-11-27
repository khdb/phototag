package com.khoahuy.network;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.khoahuy.phototag.model.Token;
import com.khoahuy.utils.ConstantUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HttpPostAsyncTask extends AsyncTask<Void, Void, String> {

	private String URL;
	private String accessToken = "";
	private Token tokenManager;
	private HttpEntity entities;

	public HttpPostAsyncTask(Context context, String url,
			List<NameValuePair> params) {
		URL = url;
		try {
			entities = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tokenManager = new Token(context);
	}

	public HttpPostAsyncTask(Context context, String url,
			MultipartEntityBuilder meb) {
		URL = url;
		entities = meb.build();
		tokenManager = new Token(context);

	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			accessToken = tokenManager.getAccessToken(false);
			return sendHttpRequest();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Huy", e.getMessage());
		} catch (Exception e) {
			Log.e("Huy", e.getMessage());
		}
		return "";
	}

	private String sendHttpRequest() throws ClientProtocolException,
			IOException, Exception {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);

		httppost.addHeader("Authorization", ConstantUtils.BEARER + " "
				+ accessToken);
		httppost.setEntity(entities);
		HttpResponse response = httpclient.execute(httppost);
		Log.d("Huy", "CODE reponse: "
				+ response.getStatusLine().getStatusCode());
		switch (response.getStatusLine().getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_NOT_FOUND:
		case HttpStatus.SC_CREATED:
		case HttpStatus.SC_BAD_REQUEST:
			break;
		case HttpStatus.SC_UNAUTHORIZED:
			// Refresh or create new token"
			Log.d("Huy", "Unauthorized Status 401");
			accessToken = tokenManager.getAccessToken(true);
			if (("").equals(accessToken)) {
				throw new Exception("Request have some problems...");
			}
			httppost.removeHeaders("Authorization");
			httppost.addHeader("Authorization", ConstantUtils.BEARER + " "
					+ accessToken);
			Log.d("Huy", "Request again");
			response = httpclient.execute(httppost);
			break;
		default:
			throw new Exception("Request have some problems...");
		}

		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			String mss = EntityUtils.toString(responseEntity);
			return mss;
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			Log.d("Huy", "Result = " + result);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
	}

}