package com.khoahuy.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticUtils {

	public static Map<String, Integer> normalizationDateData(
			Map<String, Integer> data) {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < 24; i++) {
			result.put(String.valueOf(i), 0);
		}
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static Map<String, Integer> normalizationWeekData(
			Map<String, Integer> data, Date dateEndOfWeek) throws Exception {
		String[] dateArray = DateUtils.getDateName7DayAgo(dateEndOfWeek);
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = dateArray.length - 1; i >= 0; i--) {
			result.put(dateArray[i], 0);
		}
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static int[] convertToTimestamp(int[] inputs) {
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = inputs[i] * 60;
		}
		return inputs;
	}

	public static Map<String, Integer> convert2ArrayIntoMap(int[] timestamps,
			int[] qualities) {
		String key;
		int value;
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < timestamps.length; i++) {
			key = " < " + DateUtils.convertTimestampToHourText(timestamps[i]);
			value = qualities[i];
			result.put(key, value);
		}
		key = " > "
				+ DateUtils
						.convertTimestampToHourText(timestamps[timestamps.length - 1]);
		value = qualities[timestamps.length];
		result.put(key, value);
		return result;
	}
	
	public static Map<String, Integer> mergeWaitingAndUsedItem(Map<String, Integer> data1, Map<String, Integer> data2)
	{
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		result.putAll(data1);
		for (LinkedHashMap.Entry<String, Integer> entry : data2.entrySet()) {
			int value = result.get(entry.getKey()) + entry.getValue();
			result.put(entry.getKey(), value);
		}
		return result;
	}
}
