package com.seeyon.v3x.plugin.rating.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.util.Calendar;

public final class UIUtils {

	private static final Gson GSON = new GsonBuilder().create();

	public static void responseJSON(Object data, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control",
				"no-store, max-age=0, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter out = response.getWriter();
			out.write(GSON.toJson(data));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	public static void responseChart(JFreeChart chart,HttpServletRequest request, HttpServletResponse response) {
	
		  int width = 600;  
          int height = 400;     
          try{  
              width =Integer.parseInt(request.getParameter("width"));  
              height = Integer.parseInt(request.getParameter("height"));        
          }catch(Exception e){  
              e.printStackTrace();  
          }  
          response.setContentType("image/png");
          try {
			ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, width,height,null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static String readContent(String url,String encoding){

		InputStream ins = null;
		StringBuilder stb = new StringBuilder();
		try {
				
			ins = new URL(url).openConnection().getInputStream();

			byte[] buffer = new byte[2046];
			int read = 0;
			
			while ((read = ins.read(buffer)) > 0) {
				if (read == 2046) {
					stb.append(new String(buffer,encoding));
				} else {
					byte[] real = new byte[read];
					System.arraycopy(buffer, 0, real, 0, read);
					stb.append(new String(real,encoding));
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ins!=null){
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return stb.toString();
	
	}
	public static String readContent(String url) {
		
		return readContent(url,"UTF-8");
	}
	
	
	public static <T> T parseJson(String json,Class<T> t){
		
		return GSON.fromJson(json, t);
		
	}
	
	
	public static Date[] flatDatePairByMonth(Date date,int month_of_year){
		
		Date[] dates = new Date[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		//System.out.println(calendar.get(Calendar.YEAR));
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MILLISECOND, 0);
		//System.out.println(calendar.get(Calendar.MONTH));
		//calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - monthCount);
		
		calendar.set(Calendar.MONTH,(month_of_year-1)<0?0:(month_of_year-1),1);
		
		calendar.set(Calendar.YEAR, year);
		dates[0]=calendar.getTime();
		//System.out.println(calendar.get(Calendar.YEAR));
		calendar.add(Calendar.MONTH, 1);
		//System.out.println(calendar.get(Calendar.YEAR));
		dates[1]=calendar.getTime();
		return dates;
		
	}
	public static Date[] flatDatePairByMonth(int month_of_year){
		
		
		return flatDatePairByMonth(new Date(), month_of_year);
		
	}
	public static void main(String[] args){
		
//		Date[] dates = flatDatePairByMonth(12);
//		Timestamp s1= new Timestamp(dates[0].getTime());
//		Timestamp s2= new Timestamp(dates[1].getTime());
//		System.out.println(s1);
//		System.out.println(s2);
		
	}
	


}
