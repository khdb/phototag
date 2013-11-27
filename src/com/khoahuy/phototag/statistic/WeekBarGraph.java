package com.khoahuy.phototag.statistic;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.khoahuy.database.NFCItemLocalProvider;
import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.utils.DateUtils;
import com.khoahuy.utils.StatisticUtils;

import android.graphics.Color;

public class WeekBarGraph extends BarGraph {
	public WeekBarGraph(Date date, NFCItemLocalProvider nfcProvider) {
		try {
			Map<String, Integer> waitingData = nfcProvider.getCheckinItemOfWeekStatistic(date, MyContentProvider.WAITING_CONTENT_URI);
			Map<String, Integer> usedData = nfcProvider.getCheckinItemOfWeekStatistic(date, MyContentProvider.USED_CONTENT_URI);
			data = StatisticUtils.mergeWaitingAndUsedItem(waitingData, usedData);
			seriesTitle = "Week chart";
			timeDisplay = DateUtils.getDateMonthString(date, -6) + " đến "
					+ DateUtils.getDateMonthString(date, 0);
			charTitle = "Thống kê tuần từ " + timeDisplay;
			XTitle = "Ngày";
			YTitle = "Số lượng";
			axesColor = Color.GREEN;
			labelColor = Color.RED;
			labelTextSize = 30;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public String getTextStatistic() {
		// TODO Auto-generated method stub
		int sum = 0;
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			sum += entry.getValue();
		}
		return String.format("7 ngày gần nhất từ %s: %d xe.", timeDisplay, sum);
	}
}
