package com.khoahuy.network;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.khoahuy.phototag.model.Token;
import com.khoahuy.utils.ConstantUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HttpGetAsyncTask extends AsyncTask<Void, Void, String> {

	private List<NameValuePair> nameValuePairs;
	private String URL;
	private String accessToken = "";
	private Token tokenManager;

	public HttpGetAsyncTask(Context context, String url, String nfcid,
			List<NameValuePair> params) {
		URL = url + nfcid;
		nameValuePairs = params;
		tokenManager = new Token(context);
		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			String paramString = URLEncodedUtils.format(params, "utf-8");
			if (!URL.endsWith("?"))
				URL += "?";
			URL += paramString;

		}
	}

	public HttpGetAsyncTask(Context context, String url,
			List<NameValuePair> params) {
		URL = url;
		nameValuePairs = params;
		tokenManager = new Token(context);
		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			String paramString = URLEncodedUtils.format(params, "utf-8");
			if (!URL.endsWith("?"))
				URL += "?";
			URL += paramString;

		}
	}

	@Override
	protected void onPreExecute() {

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
		HttpGet httpget = new HttpGet(URL);

		httpget.addHeader("Authorization", ConstantUtils.BEARER + " "
				+ accessToken);
		HttpResponse response = httpclient.execute(httpget);
		Log.d("Huy", "CODE reponse: "
				+ response.getStatusLine().getStatusCode());
		switch (response.getStatusLine().getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_NOT_FOUND:
			break;
		case HttpStatus.SC_UNAUTHORIZED:
			// Refresh or create new token"
			Log.d("Huy", "Unauthorized Status 401");
			accessToken = tokenManager.getAccessToken(true);
			if (("").equals(accessToken)) {
				throw new Exception("Request have some problems...");
			}
			httpget.removeHeaders("Authorization");
			httpget.addHeader("Authorization", ConstantUtils.BEARER + " "
					+ accessToken);
			Log.d("Huy", "Request again");
			response = httpclient.execute(httpget);
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

	}

}