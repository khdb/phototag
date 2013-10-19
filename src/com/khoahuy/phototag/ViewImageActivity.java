package com.khoahuy.phototag;

import java.io.File;

import com.imagezoom.ImageAttacher;
import com.imagezoom.ImageAttacher.OnMatrixChangedListener;
import com.imagezoom.ImageAttacher.OnPhotoTapListener;
import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.utils.DateUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
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
	NFCItem nfcItem;

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
		String nfcid = packageFromCaller.getString("nfcid");
		nfcItem = nfcProvider.findWaitingItem(nfcid);
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
	
	private void backToMainAndShot()
	{
		Intent intent  = new Intent(this, HomeActivity.class); // need to set your Intent View here
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setAction(Intent.EXTRA_UID);
		//intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Bundle bundle=new Bundle();
		bundle.putString("nfcid", nfcItem.getNfcid());
		intent.putExtra("MyPackage", bundle);
		this.startActivity(intent);
		this.finish();
	}
	
	private void checkoutNFCItem(){
		if (nfcProvider.deleteWaitingItem(nfcItem.getNfcid()))
		{
			//Delete image file:
			removeImageFile(nfcItem.getImage());
			nfcProvider.addUsedItem(nfcItem);					
		}
	}
	
	private boolean removeImageFile(String filePath)
	{
		File file = new File(filePath);
		return file.delete();
	}
	

	private void getAndDisplayNFCITem() {
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
