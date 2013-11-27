package com.khoahuy.network;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class HttpPostAsyncTask extends AsyncTask<Void, Void, String> {

	
	private List<NameValuePair> nameValuePairs;
	private String URL;
	
	public HttpPostAsyncTask(Context context, String url, List<NameValuePair> params) {
		URL = url;
		nameValuePairs = params;
	}

	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL);

	    try {
	        // Add your data
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity responseEntity = response.getEntity();
	        if(responseEntity!=null) {
	            String mss = EntityUtils.toString(responseEntity);
	            //Log.d("Huy",mss);
		        return mss;
	        }
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	Log.e("Huy", e.getMessage());
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	Log.e("Huy", e.getMessage());
	    } catch (Exception e){
	    	Log.e("Huy", e.getMessage());
	    }
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		if (result != null) {	
			Log.d("Huy","Result = " + result);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
	}

}