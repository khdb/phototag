package com.khoahuy.phototag.statistic;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.khoahuy.database.NFCItemLocalProvider;
import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.utils.DateUtils;
import com.khoahuy.utils.StatisticUtils;

import android.graphics.Color;

public class DateBarGraph extends BarGraph {

	public DateBarGraph(Date date, NFCItemLocalProvider nfcProvider) {
		Map<String, Integer> waitingData = nfcProvider.getCheckinItemOfDayStatistic(date, MyContentProvider.WAITING_CONTENT_URI);
		Map<String, Integer> usedData = nfcProvider.getCheckinItemOfDayStatistic(date, MyContentProvider.USED_CONTENT_URI);
		data = StatisticUtils.mergeWaitingAndUsedItem(waitingData, usedData);
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
