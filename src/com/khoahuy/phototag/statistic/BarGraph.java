package com.khoahuy.phototag.statistic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class BarGraph {

	protected String seriesTitle;
	protected String charTitle;
	protected String XTitle;
	protected String YTitle;
	protected int axesColor;
	protected int labelColor;
	protected int labelTextSize;
	protected XYMultipleSeriesRenderer mRenderer;
	
	private float maxValue;

	public BarGraph() {
		mRenderer = new XYMultipleSeriesRenderer();
		maxValue = 0;
	}

	public Intent getIntent(Context context, Map<String, Integer> data) {
		// Main bar
		List<String> xText = new ArrayList<String>();
		CategorySeries series = new CategorySeries(seriesTitle);
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			// myIntArray[Integer.parseInt(entry.getKey())] = entry.getValue();
			series.add(entry.getKey(), entry.getValue());
			xText.add(entry.getKey());
			if (maxValue < entry.getValue())
				maxValue = entry.getValue();
			Log.i("Huy", "Add: " + entry.getKey() + ": " + entry.getValue());
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());

		// This is how the "Graph" itself will look like
		mRenderer.setChartTitle(charTitle);
		mRenderer.setXTitle(XTitle);
		mRenderer.setYTitle(YTitle);
		mRenderer.setAxisTitleTextSize(labelTextSize);
		mRenderer.setLabelsTextSize(labelTextSize);
		mRenderer.setChartTitleTextSize(labelTextSize);
		mRenderer.setAxesColor(axesColor);
		mRenderer.setLabelsColor(labelColor);
		mRenderer.setYAxisMax(maxValue * 1.5);
		mRenderer.setLegendTextSize(labelTextSize);
		mRenderer.setYLabelsPadding(10);
		// Load custom X Axis text
		mRenderer.setXLabels(0);
		for (int i = 0; i < xText.size(); i++) {
			mRenderer.addXTextLabel(i + 1, xText.get(i));
		}
		
		// Customize bar 1
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing((float) 0.5);
		renderer.setChartValuesTextSize(30);
		mRenderer.addSeriesRenderer(renderer);

		Intent intent = ChartFactory.getBarChartIntent(context, dataset,
				mRenderer, Type.DEFAULT);
		return intent;
	}
}
