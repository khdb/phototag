package com.khoahuy.phototag.statistic;

import android.graphics.Color;

public class DateBarGraph extends BarGraph {

	public DateBarGraph() {
		seriesTitle = "Date chart";
		charTitle = "Thống kê ngay";
		XTitle = "Hour";
		YTitle = "Tag count";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
