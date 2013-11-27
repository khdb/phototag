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

import com.khoahuy.exception.UnauthorizedException;
import com.khoahuy.utils.ConstantUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class HttpGetAsyncTask extends AsyncTask<Void, Void, String> {

	
	private List<NameValuePair> nameValuePairs;
	private String URL;
	private String accessToken = "";
	private TokenManager tokenManager;
	
	public HttpGetAsyncTask(Context context, String url, List<NameValuePair> params) {
		URL = url;
		nameValuePairs = params;
		tokenManager = new TokenManager(context);
		accessToken = tokenManager.getAccesToken();
		
	}

	@Override
	protected String doInBackground(Void... params){
		HttpClient httpclient = new DefaultHttpClient();
	    URL += "IDBTS853";
	    HttpGet httpget = new HttpGet(URL);
	    try {
	    	httpget.addHeader("Authorization", ConstantUtils.BEARER + " " + accessToken +"1");
	        HttpResponse response = httpclient.execute(httpget);
	        switch (response.getStatusLine().getStatusCode()){
	        	case HttpStatus.SC_OK:
	        		break;
	        	case HttpStatus.SC_UNAUTHORIZED:
	        		//Refresh or create new token"
	        		accessToken = tokenManager.refreshToken();
	        		
	        	default:
	        		throw new Exception("Request have some problems...");
	        }
	    
	        HttpEntity responseEntity = response.getEntity();
	        if(responseEntity!=null) {
	            String mss = EntityUtils.toString(responseEntity);
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

	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
	}

}