package com.khoahuy.phototag.statistic;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.utils.DateUtils;

import android.graphics.Color;

public class DateBarGraph extends BarGraph {

	public DateBarGraph(Date date, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfDayStatistic(date);
		seriesTitle = "Date chart";
		timeDisplay = DateUtils.getDateString(date,"dd/MM/yyyy");
		charTitle = "Thống kê ngày " + timeDisplay;
		XTitle = "Giờ";
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
		return String.format("Trong ngày %s: %d xe.", timeDisplay, sum);
	}

}
