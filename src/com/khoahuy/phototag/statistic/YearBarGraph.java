package com.khoahuy.phototag.statistic;

import java.util.LinkedHashMap;

import com.khoahuy.database.NFCItemProvider;

import android.graphics.Color;

public class YearBarGraph extends BarGraph {

	public YearBarGraph(int year, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfYearStatistic(year);
		seriesTitle = "Year chart";
		timeDisplay = String.valueOf(year);
		charTitle = "Thống kê năm " + timeDisplay;
		XTitle = "Tháng";
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
		return String.format("Trong năm %s: %d xe.", timeDisplay, sum);
	}

}
