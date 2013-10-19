package com.khoahuy.phototag.model;

public class NFCItem {

	public int id;
	public String nfcid;
	public String image;
	public Long checkIn;
	public Long checkOut;

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
