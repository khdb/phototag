package com.khoahuy.phototag.statistic;

import android.graphics.Color;

public class WeekBarGraph extends BarGraph {
	public WeekBarGraph() {
		seriesTitle = "Week chart";
		charTitle = "Week statistic";
		XTitle = "Day";
		YTitle = "Tag count";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}
}
