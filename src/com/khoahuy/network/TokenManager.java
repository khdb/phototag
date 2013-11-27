package com.khoahuy.network;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.khoahuy.utils.ConstantUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {

	private Context context;

	public TokenManager(Context context) {
		this.context = context;
	}

	public String getAccesToken() {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					ConstantUtils.PREFS_NAME, Context.MODE_PRIVATE);
			String accessToken = settings.getString(ConstantUtils.ACCESS_TOKEN,
					null);
			if (accessToken == null) {
				// First time to get access token
				return requestNewAccessToken();
			} else {
				return accessToken;
			}
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String requestNewAccessToken() throws ExecutionException,
			InterruptedException, JSONException {
		Log.d("Token", "Request new Token");
		TokenAsyncTask tat = new TokenAsyncTask(context,
				ConstantUtils.TOKEN_URL, ConstantUtils.GRANT_TYPE_PASSWORD,
				ConstantUtils.CLIENT_ID, ConstantUtils.CLIENT_SECRET, 
				ConstantUtils.CLIENT_USERNAME, ConstantUtils.CLIENT_PASSWORD);
		tat.execute();
		return saveTokenFromJson(tat.get());
	}

	private String saveTokenFromJson(String jsonString) throws JSONException {
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

	public String refreshToken() {
		try {
			SharedPreferences settings = context.getSharedPreferences(
					ConstantUtils.PREFS_NAME, Context.MODE_PRIVATE);

			String refreshToken = settings.getString(
					ConstantUtils.REFRESH_TOKEN, null);

			if (refreshToken == null)
				return requestNewAccessToken();
			else{
				TokenAsyncTask tat = new TokenAsyncTask(context,
						ConstantUtils.TOKEN_URL, ConstantUtils.GRANT_TYPE_REFRESH,
						ConstantUtils.CLIENT_ID, ConstantUtils.CLIENT_SECRET, ConstantUtils.REFRESH_TOKEN);
				tat.execute();
				return saveTokenFromJson(tat.get());
			}
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
