package com.khoahuy.phototag.statistic;

import android.graphics.Color;

public class YearBarGraph extends BarGraph {

	public YearBarGraph() {
		seriesTitle = "Year chart";
		charTitle = "Year statistic";
		XTitle = "Month";
		YTitle = "Tag count";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
