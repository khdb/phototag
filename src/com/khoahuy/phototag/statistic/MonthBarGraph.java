package com.khoahuy.phototag.statistic;

import java.util.Calendar;
import java.util.Map;

import com.khoahuy.database.NFCItemProvider;

import android.graphics.Color;

public class MonthBarGraph extends BarGraph {

	public MonthBarGraph(int month, int year, NFCItemProvider nfcProvider) {
		month--;
		data = nfcProvider.getWaitingItemOfMonthStatistic(month, year);
		seriesTitle = "Month chart";
		charTitle = "Thống kê tháng " + month + "/" + year;
		XTitle = "Tuần";
		YTitle = "Số lượng";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
