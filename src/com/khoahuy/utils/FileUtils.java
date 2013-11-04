package com.khoahuy.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	


	public static File createImageFileByCurrentTime(String albumName) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir(albumName);
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}
	
	public static File createImageFileTemp(String albumName) throws IOException {
		// Create an image file name
		String imageFileName = JPEG_FILE_PREFIX + "temp" + JPEG_FILE_SUFFIX;
		File albumF = getAlbumDir(albumName);
		File imageF = new File(albumF, imageFileName);
		return imageF;
	}
	
	private static File getAlbumDir(String albumName) {
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
					.getAlbumStorageDir(albumName);

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
			Log.v("Phototag",
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}
	

}
