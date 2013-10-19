package com.khoahuy.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String getDate(Long timeStamp){

	    try{
	        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
	        Date netDate = (new Date(timeStamp));
	        return sdf.format(netDate);
	    }
	    catch(Exception ex){
	        return "Parse time Error";
	    }
	}
}
