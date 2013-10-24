package com.khoahuy.phototag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomItemView extends LinearLayout {
	public ImageView image;
	public TextView time;

	public CustomItemView(Context context) {
		super(context);

		// Sử dụng LayoutInflater để gán giao diện trong list.xml cho class này
		LayoutInflater li = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.item_view, this, true);

		// Lấy về các View qua Id
		image = (ImageView) findViewById(R.id.img_view);
		time = (TextView) findViewById(R.id.txt_time);
	}
}
