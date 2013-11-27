package com.khoahuy.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;


import com.khoahuy.network.HttpGetAsyncTask;
import com.khoahuy.network.HttpPostAsyncTask;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.utils.ConstantUtils;

public class NFCItemNetworkProvider {
	
	private Context context;
	
	public NFCItemNetworkProvider(Context context){
		this.context = context;
	}
	
	public void addWaitingItem(NFCItem item) {

		
		try {
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			final File imageFile = new File(item.image);
			builder.addPart("file", new FileBody(imageFile));
			builder.addTextBody("nfcid", item.nfcid);
			builder.addTextBody("image", "unknow");
			HttpPostAsyncTask hat = new HttpPostAsyncTask(context,
					ConstantUtils.WAITINGITEM_URL, builder);
			String text = hat.execute().get();
			Log.d("Huy", text);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d("Huy", e.toString());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return null;
	}

	public NFCItem findWaitingItem(String nfcid) {
		HttpGetAsyncTask hat = new HttpGetAsyncTask(context,
				ConstantUtils.WAITINGITEM_URL, nfcid, null);
		try {
			String jsonString = hat.execute().get();
			return NFCItem.deserializationFromJSON(jsonString);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d("Huy", e.toString());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
