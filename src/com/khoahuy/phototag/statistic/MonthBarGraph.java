package com.khoahuy.phototag.statistic;

import android.graphics.Color;

public class MonthBarGraph extends BarGraph {
	
	public MonthBarGraph() {
		seriesTitle = "Month chart";
		charTitle = "Month statistic";
		XTitle = "Week";
		YTitle = "Tag count";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
