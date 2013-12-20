package com.seeyon.v3x.plugin.rating.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingCountDataSet {
	
	private Map<Long,List<RatingUserResult>> barChartDataSet = new HashMap<Long,List<RatingUserResult>>();
	private Map<String,List<Object>> pieChartDataSet = new HashMap<String,List<Object>>();
	
	private List<RatingTableCountResult> tableDataSet = new ArrayList<RatingTableCountResult>();
	
	public Map<Long, List<RatingUserResult>> getBarChartDataSet() {
		return barChartDataSet;
	}
	public void setBarChartDataSet(Map<Long, List<RatingUserResult>> barChartDataSet) {
		this.barChartDataSet = barChartDataSet;
	}

	public Map<String, List<Object>> getPieChartDataSet() {
		return pieChartDataSet;
	}
	public void setPieChartDataSet(Map<String, List<Object>> pieChartDataSet) {
		this.pieChartDataSet = pieChartDataSet;
	}
	public List<RatingTableCountResult> getTableDataSet() {
		return tableDataSet;
	}
	public void setTableDataSet(List<RatingTableCountResult> tableDataSet) {
		this.tableDataSet = tableDataSet;
	}
	
	

}
