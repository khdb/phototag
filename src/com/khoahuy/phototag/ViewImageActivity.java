package com.khoahuy.phototag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.khoahuy.network.LoadBitmapAsyncTask;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.utils.ConstantUtils;
import com.khoahuy.utils.DateUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewImageActivity extends AbstractActivity {

	ImageView imgView;
	TextView txtCheckin;
	Button btnCheckout;
	Button btnNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);

		// To allow up navigation to home activity (Parent activity)
		getActionBar().setDisplayHomeAsUpEnabled(true);

		imgView = (ImageView) findViewById(R.id.imageView);
		btnCheckout = (Button) findViewById(R.id.btn_checkout);
		btnNew = (Button) findViewById(R.id.btn_new);
		txtCheckin = (TextView) findViewById(R.id.txt_checkin);

		Intent callerIntent = getIntent();
		Bundle packageFromCaller = callerIntent.getBundleExtra("MyPackage");
		nfcid = packageFromCaller.getString("nfcid");
		getAndDisplayNFCITem();

		btnCheckout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkoutNFCItem(nfcid);
				finish();
			}
		});

		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backToMainAndShot();
			}
		});

	}

	// overwrite the menu from super class
	// here we don't need any action overflow menus
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	protected void processNfcID() {
		if (("").equals(nfcid) || nfcid == null) {
			showMessage(R.string.error, R.string.detect_nfc_fail);
		} else {
			NFCItem nfcItem = nfcProvider.findWaitingItem(nfcid);
			if (nfcItem != null)
				getAndDisplayNFCITem();
			else
				backToMainAndShot();
		}
	}

	private void backToMainAndShot() {
		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setAction(Intent.EXTRA_UID);
		// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Bundle bundle = new Bundle();
		bundle.putString("nfcid", nfcid);
		intent.putExtra("MyPackage", bundle);
		this.startActivity(intent);
		this.finish();
	}

	

	private void getAndDisplayNFCITem() {
		NFCItem nfcItem = nfcProvider2.findWaitingItem(nfcid);
		String imageURL = ConstantUtils.STATIC_URL + nfcItem.getImage();
		// Bitmap bmp = BitmapFactory.decodeFile(imageURL);
		Log.d("Huy", "img = " + imageURL);
		new LoadBitmapAsyncTask(imageURL, imgView).execute();
		//imgView.setImageBitmap(loadBitmap(imageURL));
		if (nfcItem.getCheckIn() != null)
			txtCheckin.setText(DateUtils.getDateString(nfcItem.getCheckIn()));

		// usingSimpleImage(imgView);
	}

}
