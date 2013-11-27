package com.khoahuy.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadBitmapAsyncTask extends AsyncTask<Void, Void, String> {

	private String url;
	private ImageView imageView;
	private Bitmap bitmap;

	public LoadBitmapAsyncTask(String url, ImageView imgView) {
		this.url = url;
		this.imageView = imgView;
	}

	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		bitmap =  loadBitmap(url);
		//imageView.setImageBitmap(loadBitmap(url));
		//loadImageFromURL(url, imageView);
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		imageView.setImageBitmap(bitmap);
	};

	public boolean loadImageFromURL(String fileUrl, ImageView iv) {
		try {

			URL myFileUrl = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();

			InputStream is = conn.getInputStream();
			iv.setImageBitmap(BitmapFactory.decodeStream(is));

			return true;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static Bitmap loadBitmap(String url) {
		try {
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					url).getContent());
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
