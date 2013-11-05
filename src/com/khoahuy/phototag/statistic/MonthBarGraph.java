package com.khoahuy.phototag.statistic;

import com.khoahuy.database.NFCItemProvider;

import android.graphics.Color;

public class MonthBarGraph extends BarGraph {

	public MonthBarGraph(int monthIndex, int year, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfMonthStatistic(monthIndex, year);
		seriesTitle = "Month chart";
		charTitle = "Thống kê tháng " + (monthIndex + 1) + "/" + year;
		XTitle = "Tuần";
		YTitle = "Số lượng";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
