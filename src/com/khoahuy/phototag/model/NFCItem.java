package com.khoahuy.phototag.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.khoahuy.utils.ConstantUtils;

public class NFCItem {

	public int id;
	public String nfcid;
	public String image;
	public Long checkIn;
	public Long checkOut;
	public String checkInDate;
	public String checkOutDate;
	
	public static final String ID_FIELD = "id";
	public static final String NFCID_FIELD = "nfcid";
	public static final String IMAGE_FIELD = "image";
	public static final String CHECKIN_FIELD = "checkin";
	public static final String CHECKOUT_FIELD = "checkout";
	
	
	public static NFCItem deserializationFromJSON(String jsonString)
	{
		//Example: {"nfcid": "IDBTS853", "image": "image path", "checkin": "2013-11-23T20:24:07"}
		Log.d("Huy", "jsonString deserialization: " + jsonString);
		try {
			JSONObject tokenObject = new JSONObject(jsonString);
			NFCItem nfc = new NFCItem();
			nfc.id = tokenObject.optInt(NFCItem.ID_FIELD);
			nfc.nfcid = tokenObject.getString(NFCItem.NFCID_FIELD);
			nfc.image = tokenObject.optString(NFCItem.IMAGE_FIELD);
			nfc.checkInDate = tokenObject.getString(NFCItem.CHECKIN_FIELD);
			nfc.checkOutDate = tokenObject.optString(NFCItem.CHECKOUT_FIELD);
			return nfc;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getNfcid() {
		return nfcid;
	}

	public void setNfcid(String nfcid) {
		this.nfcid = nfcid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(Long checkIn) {
		this.checkIn = checkIn;
	}

	public Long getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(Long checkOut) {
		this.checkOut = checkOut;
	}
	
	

}
