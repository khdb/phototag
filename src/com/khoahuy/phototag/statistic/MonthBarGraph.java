package com.khoahuy.phototag.statistic;

import java.util.LinkedHashMap;

import com.khoahuy.database.NFCItemProvider;

import android.graphics.Color;

public class MonthBarGraph extends BarGraph {

	public MonthBarGraph(int monthIndex, int year, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfMonthStatistic(monthIndex, year);
		seriesTitle = "Month chart";
		timeDisplay = (monthIndex + 1) + "/" + year;
		charTitle = "Thống kê tháng " + timeDisplay;
		XTitle = "Tuần";
		YTitle = "Số lượng";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}
	
	@Override
	public String getTextStatistic() {
		// TODO Auto-generated method stub
		int sum = 0;
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			sum += entry.getValue();
		}
		return String.format("Trong tháng %s: %d xe.", timeDisplay, sum);
	}

}
