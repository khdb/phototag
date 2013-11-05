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

	public Intent getIntent(Context context, Map<String, Integer> data) {

		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();
		int[] colors = new int[] { Color.BLUE, Color.DKGRAY, Color.MAGENTA,
				Color.YELLOW, Color.GRAY, Color.CYAN, Color.RED, Color.GREEN,
				Color.LTGRAY, Color.WHITE, Color.BLACK, Color.WHITE };
		int i = 0;
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			// myIntArray[Integer.parseInt(entry.getKey())] = entry.getValue();
			if (entry.getValue() <= 0)
				continue;

			series.add(entry.getKey(), entry.getValue());
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[++i]);
			r.setShowLegendItem(true);
			renderer.addSeriesRenderer(r);
			Log.i("Huy", "Add: " + entry.getKey() + ": " + entry.getValue());
		}

		renderer.setChartTitle("Pie Chart Demo");
		renderer.setChartTitleTextSize(30);
		renderer.setDisplayValues(true);
		renderer.setChartTitleTextSize(30);
		renderer.setPanEnabled(false);
		renderer.setExternalZoomEnabled(false);
		renderer.setLegendTextSize(30);
		renderer.setZoomEnabled(false);
		renderer.setLabelsTextSize(30);

		Intent intent = ChartFactory.getPieChartIntent(context, series,
				renderer, "Pie");
		return intent;
	}
	
	//Khoa
	public View getView(Context context, Map<String, Integer> data) {
		
		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();
		int[] colors = new int[] { Color.BLUE, Color.DKGRAY, Color.MAGENTA,
				Color.YELLOW, Color.GRAY, Color.CYAN, Color.RED, Color.GREEN,
				Color.LTGRAY, Color.WHITE, Color.BLACK, Color.WHITE };
		int i = 0;
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			// myIntArray[Integer.parseInt(entry.getKey())] = entry.getValue();
			if (entry.getValue() <= 0)
				continue;

			series.add(entry.getKey(), entry.getValue());
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[++i]);
			r.setShowLegendItem(true);
			renderer.addSeriesRenderer(r);
			Log.i("Huy", "Add: " + entry.getKey() + ": " + entry.getValue());
		}

		renderer.setChartTitle("Thời gian gửi xe");
		renderer.setChartTitleTextSize(60);
		renderer.setDisplayValues(true);
		//renderer.setChartTitleTextSize(30);
		renderer.setPanEnabled(false);
		renderer.setExternalZoomEnabled(false);
		renderer.setLegendTextSize(50);
		renderer.setZoomEnabled(false);
		renderer.setLabelsTextSize(40);

		View view = ChartFactory.getPieChartView(context, series, renderer);
		return view;
		
	}
	
}
