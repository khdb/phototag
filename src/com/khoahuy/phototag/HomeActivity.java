/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright (C) 2011 Adam Nybäck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.khoahuy.phototag;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.model.NFCItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class HomeActivity extends Activity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_OPEN_GALLERY = 4;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private String nfcid;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;

	private AlertDialog mDialog;
	private NFCItemProvider nfcProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		nfcProvider = new NFCItemProvider(this.getContentResolver());
		// Init NFC Reader

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

		// Init PhotoIntent
		// mImageView = (ImageView) findViewById(R.id.imageView1);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
			Log.i("Huy", "Load FroyoAlbumDirFactory");
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
			Log.i("Huy", "Load BaseAlbumDirFactory");
		}
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

		Intent callerIntent = getIntent();
		if (callerIntent != null && Intent.EXTRA_UID.equals(callerIntent.getAction()) ) {
			Bundle packageFromCaller = callerIntent.getBundleExtra("MyPackage");
			if (packageFromCaller != null)
			{
				nfcid = packageFromCaller.getString("nfcid");
				processNfcID();
				setIntent(callerIntent);
			}
		}
	}

	private void showMessage(int title, int message) {
		mDialog.setTitle(title);
		mDialog.setMessage(getText(message));
		mDialog.show();
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

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
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

	private void processNfcID() {
		if (("").equals(nfcid) || nfcid == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					HomeActivity.this);
			builder.setTitle("Error");
			builder.setMessage("Dected your tag fail");
			builder.setPositiveButton("Continue",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
			builder.show();
		} else if (existedUID(nfcid)) {
			displayNFCItem(nfcid);
		} else {
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	}

	private void displayNFCItem(String nfcid) {
		Intent myIntent = new Intent(this, ViewImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("nfcid", nfcid);
		myIntent.setAction(Intent.ACTION_VIEW);
		myIntent.putExtra("MyPackage", bundle);
		startActivity(myIntent);
	}

	private boolean existedUID(String nfcid) {
		if (nfcProvider.findWaitingItem(nfcid) == null)
			return false;
		else
			return true;
	}

	// Yeath! This is area of PhotoIntent
	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch (actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;

			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			if (mAlbumStorageDirFactory == null) {
				Log.w("Huy", "mAlbumStorageDirFactory is null");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
					mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
					Log.i("Huy", "Load FroyoAlbumDirFactory");
				} else {
					mAlbumStorageDirFactory = new BaseAlbumDirFactory();
					Log.i("Huy", "Load BaseAlbumDirFactory");
				}
			}

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			Log.i("Huy", "storageDir: " + storageDir);

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	// Reponse of PhotoIntent

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setIntent(data);
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				handleBigCameraPhoto();
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		case ACTION_OPEN_GALLERY: {
			if (resultCode == RESULT_OK) {
				Log.i("Huy", "onActivityResult: ACTION_OPEN_GALLERY");
				String filePath = data.getData().getPath();
				Log.i("Huy", "Path: " + filePath);
			}
			break;
		} // ACTION_OPEN_GALLERY
		} // switch
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			// setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);

		// Store to db

		NFCItem item = new NFCItem();
		item.setNfcid(nfcid);
		item.setImage(mCurrentPhotoPath);
		item.setCheckIn((new Date()).getTime());
		nfcProvider.addWaitingItem(item);
	}

}
