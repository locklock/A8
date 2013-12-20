package com.seeyon.v3x.plugin.rating.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.v3x.plugin.rating.util.UIUtils;

public class TestServletController extends HttpServlet{
	
	
	private RatingController controller = new RatingController();
	/**
	 * 
	 */
	private static final long serialVersionUID = 2425473811857131583L;
	
	@Override
	public void init(){
		try {
			super.init();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response){
		doPost(request,response);
	}
	
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response){
		String method = request.getParameter("method");
		if(method!=null){
			if(method.equals("getConfigItemList")){
				controller.getConfigItemList(request, response);
			}
			if(method.equals("saveItem")){
				controller.saveItem(request, response);
			}
			if(method.equals("deleteItem")){
				controller.deleteItem(request, response);
			}
			if(method.equals("saveItemResult")){
				controller.saveItemResult(request, response);
			}
			if(method.equals("showCountTable")){
				
				controller.showCountTable(request, response);
			}
			if(method.equals("showPieChart")){
				controller.showPieChart(request, response);
			}
			if(method.equals("showBarChart")){
				controller.showBarChart(request, response);
			}
			if(method.equals("showBarCharComment")){
				controller.showBarCharComment(request, response);
			}
			if(method.equals("saveWeight")){
				controller.saveWeight(request, response);
			}
			if(method.equals("getWeight")){
				controller.getWeight(request, response);
			}
			if(method.equals("getTemplateData")){
				controller.getTemplateData(request, response);
			}
			
		}else{
			
			UIUtils.responseJSON("failure", response);
		}
		
	}
	
	

}
