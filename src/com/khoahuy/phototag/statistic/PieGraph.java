package com.khoahuy.phototag.statistic;

import java.util.LinkedHashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

public class PieGraph {
	protected CategorySeries series;
	protected DefaultRenderer renderer;
	
	public void createGraphic(Map<String, Integer> data) {
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
			Log.i("Huy", "Add: " + entry.getKey() + ": " + entry.getValue());
		}

		renderer.setChartTitle("Pie Chart Demo");
		renderer.setChartTitleTextSize(30);
		renderer.setDisplayValues(true);
		renderer.setChartTitleTextSize(30);
		renderer.setAxesColor(Color.RED);
		renderer.setLegendTextSize(30);
		renderer.setInScroll(true);

		renderer.setZoomRate(2);
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(true);
		
		renderer.setFitLegend(true);
	}

	public Intent getIntent(Context context, Map<String, Integer> data) {
		createGraphic(data);
		Intent intent = ChartFactory.getPieChartIntent(context, series,
				renderer, "Pie");
		return intent;
	}

	// Khoa
	public View getView(Context context, Map<String, Integer> data) {
		createGraphic(data);
		View view = ChartFactory.getPieChartView(context, series, renderer);
		return view;

	}

}
