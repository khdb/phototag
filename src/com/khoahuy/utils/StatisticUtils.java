package com.khoahuy.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


public class StatisticUtils {
	
	public static Map<String, Integer> normalizationDateData(
			Map<String, Integer> data) {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < 24; i++)
		{
			result.put(String.valueOf(i), 0);
		}
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static Map<String, Integer> normalizationWeekData(
			Map<String, Integer> data, int dateEndOfWeek) {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (int i = dateEndOfWeek - 6; i <= dateEndOfWeek; i++)
		{
			result.put(String.valueOf(i), 0);
		}
		for (LinkedHashMap.Entry<String, Integer> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
