package com.khoahuy.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.database.Cursor;

import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.phototag.model.NFCItem;

public class NFCItemNetworkProvider {
	
	public NFCItem findWaitingItem(String nfcid) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", ""));
		nameValuePairs.add(new BasicNameValuePair("password", ""));
		
		return null;
	}
}
