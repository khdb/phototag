package com.khoahuy.phototag.statistic;

import java.util.Date;
import java.util.Map;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.utils.DateUtils;

import android.graphics.Color;

public class DateBarGraph extends BarGraph {

	public DateBarGraph(Date date, NFCItemProvider nfcProvider) {
		data = nfcProvider.getWaitingItemOfDayStatistic(date);
		seriesTitle = "Date chart";
		charTitle = "Thống kê ngày " + DateUtils.getDateString(date,"dd/MM/yyyy");
		XTitle = "Giờ";
		YTitle = "Số lượng";
		axesColor = Color.GREEN;
		labelColor = Color.RED;
		labelTextSize = 30;
	}

}
