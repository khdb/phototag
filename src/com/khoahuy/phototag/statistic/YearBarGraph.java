package com.khoahuy.phototag.statistic;

import com.khoahuy.database.NFCItemProvider;

import android.graphics.Color;

public class YearBarGraph extends BarGraph {

	public YearBarGraph(int year, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfYearStatistic(year);
		seriesTitle = "Year chart";
		charTitle = "Thống kê năm " + year;
		XTitle = "Tháng";
		YTitle = "Số lượng";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
