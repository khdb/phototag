package com.khoahuy.phototag.statistic;

import java.util.LinkedHashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.utils.DateUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

public class PieGraph {
	protected CategorySeries series;
	protected DefaultRenderer renderer;
	protected String timeDisplay;	
	protected Map<String, Integer> data;
	
	public PieGraph(long from, long to, int[] thresholdArray, NFCItemProvider nfcProvider){
		data = nfcProvider.getUsedItemStatistic(
				from, to, thresholdArray);
		timeDisplay = " từ " + DateUtils.getDateString(from) + " tới " + DateUtils.getDateString(to);
	}
	
	public void createGraphic() {
		series = new CategorySeries("Pie Graph");
		renderer = new DefaultRenderer();
		int[] colors = new int[] { Color.BLUE, Color.DKGRAY, Color.MAGENTA,
				Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW, Color.rgb(255,165,0),
				Color.rgb(148,  0,  211), Color.rgb(0,100,10)}; //Orange, Puple, Darkgreen
		int i = 0;
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			// myIntArray[Integer.parseInt(entry.getKey())] = entry.getValue();
			if (entry.getValue() <= 0)
				continue;
			series.add(entry.getKey(), entry.getValue());
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			i++;
			r.setShowLegendItem(true);
			renderer.addSeriesRenderer(r);
			//Log.i("Huy", "Add: " + entry.getKey() + ": " + entry.getValue());
		}

		renderer.setChartTitle("Thống kê thời gian vào - ra " + timeDisplay);
		renderer.setChartTitleTextSize(30);
		renderer.setDisplayValues(true);
		renderer.setChartTitleTextSize(30);
		renderer.setAxesColor(Color.RED);
		renderer.setLegendTextSize(30);

		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		
		renderer.setFitLegend(true);
	}

	public String getTextStatistic() {
		// TODO Auto-generated method stub
		String result = "Thống kê thời gian vào - ra " + timeDisplay + ": \n";
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			result += "\t" + entry.getKey() + ": " +  entry.getValue() + " xe.\n";
		}
		return result;
	}

	public Intent getIntent(Context context) {
		createGraphic();
		Intent intent = ChartFactory.getPieChartIntent(context, series,
				renderer, "Pie");
		return intent;
	}

	// Khoa
	public View getView(Context context) {
		createGraphic();
		View view = ChartFactory.getPieChartView(context, series, renderer);
		return view;

	}

}
