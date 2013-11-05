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
import android.graphics.Paint.Align;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
		series.add("0", 0);
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
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setLegendTextSize(20);

		mRenderer.setChartTitleTextSize(20);
		mRenderer.setAxesColor(axesColor);
		// mRenderer.setMargins(new int[] { 20, 30, 0, 20 } );
		mRenderer.setLabelsColor(labelColor);

		// mRenderer.setXLabelsPadding(5);
		mRenderer.setYAxisMax(maxValue * 1.5);
		mRenderer.setYLabels(0);
		// mRenderer.setYLabelsPadding(10);

		mRenderer.setShowGrid(true);
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);

		mRenderer.setBarSpacing(1.0f);
		mRenderer.setBarWidth(50);

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

	public View getView(Context context, Map<String, Integer> data) {
		// TODO Auto-generated method stub
		// Main bar
		List<String> xText = new ArrayList<String>();
		CategorySeries series = new CategorySeries(seriesTitle);
		series.add("0", 0);
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
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setLegendTextSize(20);

		mRenderer.setChartTitleTextSize(20);
		mRenderer.setAxesColor(axesColor);
		// mRenderer.setMargins(new int[] { 20, 30, 0, 20 } );
		mRenderer.setLabelsColor(labelColor);

		// mRenderer.setXLabelsPadding(5);
		mRenderer.setYAxisMax(maxValue * 1.5);
		mRenderer.setYLabels(0);
		// mRenderer.setYLabelsPadding(10);

		mRenderer.setShowGrid(true);
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);

		mRenderer.setBarSpacing(1.0f);
		mRenderer.setBarWidth(50);

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

		View view = ChartFactory.getBarChartView(context, dataset,
				mRenderer, Type.DEFAULT);

		return view;
	}
}
