package com.khoahuy.phototag.statistic;

import java.util.LinkedHashMap;
import java.util.Map;

import com.khoahuy.database.NFCItemLocalProvider;
import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.utils.StatisticUtils;

import android.graphics.Color;

public class YearBarGraph extends BarGraph {

	public YearBarGraph(int year, NFCItemLocalProvider nfcProvider) {
		Map<String, Integer> waitingData = nfcProvider.getCheckinItemOfYearStatistic(year, MyContentProvider.WAITING_CONTENT_URI);
		Map<String, Integer> usedData = nfcProvider.getCheckinItemOfYearStatistic(year, MyContentProvider.USED_CONTENT_URI);
		data = StatisticUtils.mergeWaitingAndUsedItem(waitingData, usedData);
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
