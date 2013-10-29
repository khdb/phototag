package com.khoahuy.phototag.statistic;

import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

public class BarGraph{

	public Intent getIntent(Context context, HashMap<String, Integer> data) 
	{	
		//normalization data
		//Main bar
		int[] myIntArray = new int[25]; //0->24
		CategorySeries series = new CategorySeries("Tag checkin");
		for (HashMap.Entry<String, Integer> entry : data.entrySet()) {
			myIntArray[Integer.parseInt(entry.getKey())] = entry.getValue();
		}
		
		//Loop skip 0 index, get 1 -> 24
		for (int i = 1; i < myIntArray.length; i++){
			series.add(String.valueOf(i), myIntArray[i]);
			Log.i("Huy", "Add: " + i + ": " + myIntArray[i]);
		}
		
		
		
		
				
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());

		// This is how the "Graph" itself will look like
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setChartTitle("PhotoTag Statistic");
		mRenderer.setXTitle("Hour");
		mRenderer.setYTitle("Tag number");
		mRenderer.setAxesColor(Color.GREEN);
	    mRenderer.setLabelsColor(Color.RED);
	    // Customize bar 1
		XYSeriesRenderer renderer = new XYSeriesRenderer();
	    renderer.setDisplayChartValues(true);
	    renderer.setChartValuesSpacing((float) 0.5);
	    mRenderer.addSeriesRenderer(renderer);

	    
		Intent intent = ChartFactory.getBarChartIntent(context, dataset,mRenderer, Type.DEFAULT);
		return intent;
	}
	
	private HashMap<String, Integer> normalization(HashMap<String, Integer> data){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (int i = 1; i <= 24; i++ )
		{
			result.put(String.valueOf(i), 0);
		}
		for (HashMap.Entry<String, Integer> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
