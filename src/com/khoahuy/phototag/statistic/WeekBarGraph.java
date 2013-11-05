package com.khoahuy.phototag.statistic;

import java.util.Date;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.utils.DateUtils;

import android.graphics.Color;

public class WeekBarGraph extends BarGraph {
	public WeekBarGraph(Date date, NFCItemProvider nfcProvider) {
		try {
			data = nfcProvider.getWaitingItemOfWeekStatistic(date);
			seriesTitle = "Week chart";
			charTitle = "Thống kê tuần từ "
					+ DateUtils.getDateMonthString(date, -7) + " đến "
					+ DateUtils.getDateMonthString(date, 0);
			XTitle = "Ngày";
			YTitle = "Số lượng";
			axesColor = Color.GREEN;
			labelColor = Color.RED;
			labelTextSize = 30;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
