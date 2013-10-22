package com.khoahuy.phototag;

import java.io.File;

import com.imagezoom.ImageAttacher;
import com.imagezoom.ImageAttacher.OnMatrixChangedListener;
import com.imagezoom.ImageAttacher.OnPhotoTapListener;
import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.utils.DateUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewImageActivity extends Activity {

	ImageView imgView;
	TextView txtCheckin;
	Button btnCheckout;
	Button btnNew;
	NFCItemProvider nfcProvider;
	String nfcid;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);

		imgView = (ImageView) findViewById(R.id.imageView);
		btnCheckout = (Button) findViewById(R.id.btn_checkout);
		btnNew = (Button) findViewById(R.id.btn_new);
		txtCheckin = (TextView) findViewById(R.id.txt_checkin);

		nfcProvider = new NFCItemProvider(this.getContentResolver());

		Intent callerIntent = getIntent();
		Bundle packageFromCaller = callerIntent.getBundleExtra("MyPackage");
		nfcid = packageFromCaller.getString("nfcid");

		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();
		mAdapter = NfcAdapter.getDefaultAdapter(this);
				
		if (mAdapter == null) {
			showMessage(R.string.error, R.string.no_nfc);
			finish();
			return;
		}
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		getAndDisplayNFCITem();

		btnCheckout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkoutNFCItem();
				finish();
			}
		});

		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkoutNFCItem();
				backToMainAndShot();
			}
		});

	}

	@Override
	public void onNewIntent(Intent intent) {
		nfcid = "";
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(intent
					.getByteArrayExtra(NfcAdapter.EXTRA_ID));
			Log.i("Huy", "NDEF DISCOVERED = " + nfcid);

		} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(intent
					.getByteArrayExtra(NfcAdapter.EXTRA_ID));
			Log.i("Huy", "TAG DISCOVERED = " + nfcid);

		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(getIntent().getByteArrayExtra(
					NfcAdapter.EXTRA_ID));
			Log.i("Huy", "TECH DISCOVERED = " + nfcid);
		}
		setIntent(intent);
		processNfcID();
		// resolveIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				showWirelessSettingsDialog();
			}
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	};

	private void processNfcID() {
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

	private void showWirelessSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.nfc_disabled);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						finish();
					}
				});
		builder.create().show();
		return;
	}

	private String ByteArrayToHexString(byte[] inarray) {
		int i, j, in;
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F" };
		String out = "";

		for (j = 0; j < inarray.length; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	private void showMessage(int title, int message) {
		mDialog.setTitle(title);
		mDialog.setMessage(getText(message));
		mDialog.show();
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

	private void checkoutNFCItem() {
		NFCItem nfcItem = nfcProvider.findWaitingItem(nfcid);
		if (nfcItem != null) {
			if (nfcProvider.deleteWaitingItem(nfcid)) {
				// Delete image file:
				removeImageFile(nfcItem.getImage());
				nfcProvider.addUsedItem(nfcItem);
			}
		}
	}

	private boolean removeImageFile(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}

	private void getAndDisplayNFCITem() {
		NFCItem nfcItem = nfcProvider.findWaitingItem(nfcid);
		Bitmap bmp = BitmapFactory.decodeFile(nfcItem.getImage());
		Log.i("ViewImageActivity", "img = " + nfcItem.getImage());
		imgView.setImageBitmap(bmp);
		if (nfcItem.getCheckIn() != null)
			txtCheckin.setText(DateUtils.getDate(nfcItem.getCheckIn()));

		// usingSimpleImage(imgView);
	}

	public void usingSimpleImage(ImageView imageView) {
		ImageAttacher mAttacher = new ImageAttacher(imageView);
		ImageAttacher.MAX_ZOOM = 2.0f; // Double the current Size
		ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
		MatrixChangeListener mMaListener = new MatrixChangeListener();
		mAttacher.setOnMatrixChangeListener(mMaListener);
		PhotoTapListener mPhotoTap = new PhotoTapListener();
		mAttacher.setOnPhotoTapListener(mPhotoTap);
	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
