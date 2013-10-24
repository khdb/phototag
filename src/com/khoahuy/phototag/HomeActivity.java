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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.utils.DateUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class HomeActivity extends AbstractActivity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_M = 2;
	private static final int ACTION_OPEN_GALLERY = 4;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private ImageView img1;
	private ImageView img2;
	private TextView text1;
	private TextView text2;

	private NFCItemProvider nfcProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		resizeControl();

		nfcProvider = new NFCItemProvider(this.getContentResolver());

		img1 = (ImageView) findViewById(R.id.img_newest);
		text1 = (TextView) findViewById(R.id.txt_time_newest);

		img2 = (ImageView) findViewById(R.id.img_oldest);
		text2 = (TextView) findViewById(R.id.txt_time_oldest);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
			Log.i("Huy", "Load FroyoAlbumDirFactory");
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
			Log.i("Huy", "Load BaseAlbumDirFactory");
		}

	}

	private void resizeControl() {
		img1 = (ImageView) findViewById(R.id.img_newest);
		text1 = (TextView) findViewById(R.id.txt_time_newest);
		// img1.img1.getWidth();
	}

	private void loadContent() {
		NFCItem nfcItem = nfcProvider.getNewestWaitingItem();
		if (nfcItem != null) {
			Bitmap bmp = BitmapFactory.decodeFile(nfcItem.getImage());
			img1.setImageBitmap(bmp);
			if (nfcItem.getCheckIn() != null)
				text1.setText(DateUtils.getDate(nfcItem.getCheckIn()));
		} else {
			img1.setImageResource(R.raw.noimage);
			text1.setText(R.string.check_in_not_found);
		}

		// Get last waiting item from 24h ago
		nfcItem = nfcProvider.getOldestWaitingItemOfToday();
		if (nfcItem != null) {
			Bitmap bmp = BitmapFactory.decodeFile(nfcItem.getImage());
			img2.setImageBitmap(bmp);
			if (nfcItem.getCheckIn() != null)
				text2.setText(DateUtils.getDate(nfcItem.getCheckIn()));
		} else {
			img2.setImageResource(R.raw.noimage);
			text2.setText(R.string.check_in_not_found);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadContent();

		CharSequence title = "Photo Tag " + nfcProvider.countWaitingItem();
		Log.i("Huy", "Title = " + title);
		this.setTitle(title);

		Intent callerIntent = getIntent();
		if (callerIntent != null
				&& Intent.EXTRA_UID.equals(callerIntent.getAction())) {
			Bundle packageFromCaller = callerIntent.getBundleExtra("MyPackage");
			if (packageFromCaller != null) {
				nfcid = packageFromCaller.getString("nfcid");
				processNfcID();
				setIntent(callerIntent);
			}
		}
	}

	@Override
	protected void processNfcID() {
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
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_M);
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
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		switch (actionCode) {
		case ACTION_TAKE_PHOTO_B:
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
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
		}
		case ACTION_TAKE_PHOTO_M: {
			if (resultCode == RESULT_OK) {
				handleSmallCameraPhoto(data);
			}
			break;
		}

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

	private void handleSmallCameraPhoto(Intent intent) {

		if (mCurrentPhotoPath != null) {
			Bundle extras = intent.getExtras();
			Bitmap bm = (Bitmap) extras.get("data");
			File file = new File(mCurrentPhotoPath);
			if (file.exists())
				file.delete();
			try {
				FileOutputStream out = new FileOutputStream(file);
				bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			NFCItem item = new NFCItem();
			item.setNfcid(nfcid);
			item.setImage(mCurrentPhotoPath);
			item.setCheckIn((new Date()).getTime());
			nfcProvider.addWaitingItem(item);
			mCurrentPhotoPath = null;
		}

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
