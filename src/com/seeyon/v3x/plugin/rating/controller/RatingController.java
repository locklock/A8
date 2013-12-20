package com.seeyon.v3x.plugin.rating.controller;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.plugin.rating.model.RatingCountDataSet;
import com.seeyon.v3x.plugin.rating.model.RatingItem;
import com.seeyon.v3x.plugin.rating.model.RatingTableCountResult;
import com.seeyon.v3x.plugin.rating.model.RatingUserResult;
import com.seeyon.v3x.plugin.rating.model.RatingUserWeight;
import com.seeyon.v3x.plugin.rating.model.TemplateInfo;
import com.seeyon.v3x.plugin.rating.service.impl.RatingDao;
import com.seeyon.v3x.plugin.rating.service.impl.RatingServiceImpl;
import com.seeyon.v3x.plugin.rating.util.UIUtils;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;

public class RatingController extends BaseController{

	private RatingServiceImpl ratingService = new RatingServiceImpl();
	
	@NeedlessCheckLogin
	public ModelAndView listPage(HttpServletRequest rest, HttpServletResponse resp)throws Exception {
		
		rest.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		String affairId = rest.getParameter("affairId");
		String summaryId = rest.getParameter("summaryId");
	    ModelAndView modelAndView = new ModelAndView("ratingindex");
	    RatingDao dao = ratingService.getRatingDao();
	   Session session =  ratingService.getRatingDao().getSessionSelf();
	    try{
	    	List<Object[]> list = session.createSQLQuery("select member_id,subject from v3x_affair where id="+affairId).list();
	    	for(Object[] obj:list){
	    		modelAndView.addObject("userId", obj[0]);
	    	    modelAndView.addObject("subject", obj[1]);
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	
	    	dao.closeSessionIfNewOpen(session);
	    }
	    modelAndView.addObject("instanceId", affairId);
	    modelAndView.addObject("summaryId", summaryId);
	    
	    return modelAndView;
	}
	
	
	
	@NeedlessCheckLogin
	public ModelAndView getConfigItemList(HttpServletRequest request,
			HttpServletResponse response) {
		String procId = request.getParameter("procId");
		List<RatingItem> list = new ArrayList<RatingItem>();
		if (procId == null) {
			list = ratingService.getRatingItemList();
		} else {
			list = ratingService.getRatingItemList(Long.parseLong(procId));
		}

		UIUtils.responseJSON(list, response);
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
	}
	@NeedlessCheckLogin
	public ModelAndView saveItem(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			String data = request.getParameter("val");
			Long procId = Long.parseLong(request.getParameter("procId"));
			RatingItem item = new RatingItem();
			item.setDefaultScore(1);
			item.setMaxScore(5);
			item.setSection(5);
			item.setItemName(data);
			item.setProcId(procId);
			ratingService.persistRatingItemList(procId, Arrays.asList(item));

			UIUtils.responseJSON(true, response);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.responseJSON(false, response);
		}
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
	}
	
	@NeedlessCheckLogin
	public ModelAndView deleteItem(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			String data = request.getParameter("val");
			Long procId = Long.parseLong(request.getParameter("procId"));
			ratingService.deleteRatingItem(procId, data);
			UIUtils.responseJSON(true, response);
		} catch (Exception e) {
			UIUtils.responseJSON(false, response);
		}
		ModelAndView modelAndView = new ModelAndView();
		return modelAndView;
	}
	@NeedlessCheckLogin
	public ModelAndView saveItemList(HttpServletRequest request,
			HttpServletResponse response) {

		String dataList = request.getParameter("dataList");
		String procId = request.getParameter("procId");
		ArrayList list = UIUtils.parseJson(dataList, ArrayList.class);
		List<RatingItem> retlist = new ArrayList<RatingItem>();
		for (Object json : list) {
			retlist.add(UIUtils.parseJson(String.valueOf(json),
					RatingItem.class));
		}
		ratingService.persistRatingItemList(Long.parseLong(procId), retlist);

		UIUtils.responseJSON("success", response);
		ModelAndView modelAndView = new ModelAndView();
		return modelAndView;
	}
	
	@NeedlessCheckLogin
	public ModelAndView saveItemResult(HttpServletRequest request,
			HttpServletResponse response){
		
		String dataList = request.getParameter("dataList");
		String procId = request.getParameter("procId");
		String instanceId = request.getParameter("instanceId");
		String userId = request.getParameter("userId");
		String subject = request.getParameter("subject");
		String comment = request.getParameter("comment");
		String[] sections = dataList.split(",");
		List<RatingUserResult> retList = new ArrayList<RatingUserResult>();
		for(String sec:sections){
			String[] kes = sec.split(":");
			RatingUserResult userRet = new RatingUserResult();
			userRet.setComment(comment);
			userRet.setProcId(Long.parseLong(procId));
			userRet.setInstanceId(Long.parseLong(instanceId));
			userRet.setRatingItem(kes[0]);
			userRet.setScore(Integer.parseInt(kes[1]));
			userRet.setUserId(Long.parseLong(userId));
			userRet.setTime(new Date());
			userRet.setInstanceName(subject);
			retList.add(userRet);
		}
		ratingService.persistRatingResultList(retList);
		
		UIUtils.responseJSON(true, response);
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
	}
	
	@NeedlessCheckLogin
	public ModelAndView ratingCount(HttpServletRequest request,
			HttpServletResponse response){
		String procId = request.getParameter("procId");
		RatingCountDataSet dataSet = ratingService.getRatingCountDataSet(Long.parseLong(procId));
		UIUtils.responseJSON(dataSet, response);
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
	}
	
	@NeedlessCheckLogin
	public ModelAndView showConfig(HttpServletRequest request,
			HttpServletResponse response){
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
	    ModelAndView modelAndView = new ModelAndView("ratingconfig");
	    
	    return modelAndView;
	}
	public void showBarCharComment(HttpServletRequest request,
			HttpServletResponse response){
		String instanceId = request.getParameter("instanceId");
		String templateId = request.getParameter("templateId");
		String time = request.getParameter("time");
		String type = request.getParameter("type");
		try{

			Map<Long, List<RatingUserResult>> retMap = ratingService.getProcessResultList(Long.parseLong(templateId), Integer.parseInt(time),Integer.parseInt(type));
			List<RatingUserResult> retList = retMap.get(Long.parseLong(instanceId));
			if(retList==null){
				retList = new ArrayList<RatingUserResult>();
			}
			List<RatingItem> itemList = ratingService.getRatingItemList();
			Map<String,String>itemMap = new HashMap<String,String>();
			for(RatingItem item:itemList){
				itemMap.put(item.getItemName(), item.getItemName());
			}
			List<RatingTableCountResult> tbl = new ArrayList<RatingTableCountResult>();
			Map<String,Set<String>> maps = new HashMap<String,Set<String>>();
			for(RatingUserResult ret:retList){
				if(itemMap.get(ret.getRatingItem())==null){
					continue;
				}
				Set<String> set = maps.get(ret.getUserName());
				if(set == null){
					set = new HashSet<String>();
					maps.put(ret.getUserName(), set);
				}
				set.add(ret.getComment());
			}
			String[] name = {"A","B","C","D","E","F","G","H","I","J"};
			int index =0;
			for(Map.Entry<String, Set<String>> entry:maps.entrySet()){
				
				for(String comment:entry.getValue()){
					RatingTableCountResult rrr = new RatingTableCountResult();
					rrr.setInstanceName(name[index]);
					rrr.setScore(comment);
					tbl.add(rrr);
				}
				index++;
			}
			UIUtils.responseJSON(tbl, response);
			
		}catch(Exception e){
			
			
		}
	}
	@NeedlessCheckLogin
	public ModelAndView showChart(HttpServletRequest request,
			HttpServletResponse response){
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
	    ModelAndView modelAndView = new ModelAndView("ratingcount");
	    
	    return modelAndView;
	}
	@NeedlessCheckLogin
	public void showCountTable(HttpServletRequest request,
			HttpServletResponse response){
		String procId = request.getParameter("procId");
		Integer type = Integer.parseInt(request.getParameter("type"));
		Integer month = Integer.parseInt(request.getParameter("time"));
		List<RatingTableCountResult> data =  ratingService.getRatingTableCountResult(Long.parseLong(procId), month, type);
		UIUtils.responseJSON(data,response);

	}
	
	private String getSection(double score){
		if(score>=5d){
			return "5分";
		}
		if(score<5&&score>=4d){
			return "4-5分";
		}
		if(score<4&&score>=3d){
			return "3-4分";
		}
		if(score<3&&score>=2d){
			return "2-3分";
		}
		return "1-2分";
		
	}
	@NeedlessCheckLogin
	public ModelAndView showPieChart(HttpServletRequest request,
			HttpServletResponse response){
//		
//		try {
//			String instanceName = new String(request.getParameter("instanceName").getBytes("iso8859-1"),"utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String templateId = request.getParameter("templateId");
		String time = request.getParameter("time");
		String type = request.getParameter("type");
		List<RatingTableCountResult> retList = ratingService.getRatingTableCountResult(Long.parseLong(templateId), Integer.parseInt(time),Integer.parseInt(type));
		
		Map<String,Integer>pieChartDataSet = new HashMap<String,Integer>();
		
		for(RatingTableCountResult rst:retList){
			String section = getSection(Double.parseDouble(rst.getScore()));
			Integer count = pieChartDataSet.get(section);
			if(count==null){
				count=1;
			}else{
				count=count+1;
			}
			pieChartDataSet.put(section,count);
		}
		
		//设置饼图数据集
		DefaultPieDataset dataset2 = new DefaultPieDataset();
		
		for(Map.Entry<String, Integer> entry:pieChartDataSet.entrySet()){
			
			dataset2.setValue(entry.getKey(),entry.getValue());
		}

		//通过工厂类生成JFreeChart对象
		JFreeChart chart = ChartFactory.createPieChart3D("",dataset2,true,true,false);
		chart.addSubtitle(new TextTitle("评价分数分布图"));
		PiePlot pieplot = (PiePlot) chart.getPlot();
		pieplot.setLabelFont(new Font("宋体", 0, 11));
		StandardPieSectionLabelGenerator standarPieIG = new StandardPieSectionLabelGenerator("{0}:({2},{1}个)", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
		pieplot.setLabelGenerator(standarPieIG);
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));

		//没有数据的时候显示的内容
		pieplot.setNoDataMessage("无数据显示");
		pieplot.setLabelGap(0.02D);

		PiePlot3D pieplot3d = (PiePlot3D)chart.getPlot();
		//设置开始角度
		pieplot3d.setStartAngle(120D);
		//设置方向为”顺时针方向“
		pieplot3d.setDirection(Rotation.CLOCKWISE);
		//设置透明度，0.5F为半透明，1为不透明，0为全透明
		pieplot3d.setForegroundAlpha(0.7F);
		//ChartRenderingInfo info = new ChartRenderingInfo();
		pieplot3d.setURLGenerator(new  org.jfree.chart.urls.StandardPieURLGenerator("rating.forthelichking?method=showCountTable")); 
		UIUtils.responseChart(chart, request, response);
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
		
	}
	@NeedlessCheckLogin
	public ModelAndView showBarChart(HttpServletRequest request,
			HttpServletResponse response){
		
		
		try{

			String instanceId = request.getParameter("instanceId");
			String instanceName = new String(request.getParameter("instanceName").getBytes("iso8859-1"),"utf-8");
			String templateId = request.getParameter("templateId");
			String time = request.getParameter("time");
			String type = request.getParameter("type");
			
			Map<Long, List<RatingUserResult>> retMap = ratingService.getProcessResultList(Long.parseLong(templateId), Integer.parseInt(time),Integer.parseInt(type));
			
			List<RatingUserResult> retList = retMap.get(Long.parseLong(instanceId));
			
			if(retList==null){
				retList = new ArrayList<RatingUserResult>();
			}
			
			List<RatingItem> itemList = ratingService.getRatingItemList(65535L);
		
			List<String > rowKeys= new ArrayList<String>();
			Map<Long,String> userMap = new HashMap<Long,String>();
			for(int i=0;i<itemList.size();i++){
				rowKeys.add(itemList.get(i).getItemName());
			}
			Map<Long,List<RatingUserResult>>userResultMap = new HashMap<Long,List<RatingUserResult>>();
			for(RatingUserResult ret:retList){
				List<RatingUserResult> list = userResultMap.get(ret.getUserId());
				if(list == null){
					list = new ArrayList<RatingUserResult>();
					userResultMap.put(ret.getUserId(), list);
				}
				if(rowKeys.contains(ret.getRatingItem())){
					list.add(ret);
					userMap.put(ret.getUserId(), ret.getUserName());
				}
			}
			Map<Long,Map<String,Double>> finalResult = new HashMap<Long,Map<String,Double>>();
			for(Map.Entry<Long, List<RatingUserResult>> entry:userResultMap.entrySet()){
				Map<String,Double> dt = finalResult.get(entry.getKey());
				if(dt == null){
					dt = new HashMap<String,Double>();
					finalResult.put(entry.getKey(),dt);
				}
				for(RatingUserResult ret:entry.getValue()){
					dt.put(ret.getRatingItem(),ret.getScore()/1.0d);
				}
				
			}
			String[] rows = new String[rowKeys.size()];
			for(int i=0;i<rowKeys.size();i++){
				rows[i] = rowKeys.get(i);
			}
			Long [] columnids=new Long [userMap.keySet().size()];//)userMap.keySet().toArray();
			int index = 0;
			for(Long id :userMap.keySet()){
				columnids[index] = id;
				index++;
			}
			
			String [] columns = new String[columnids.length];
			double[][] data = new double[rows.length][columnids.length];
			 index = 0;
			for(Long id :columnids){
				columns[index] = userMap.get(id);
				Map<String,Double> rr = finalResult.get(id);
				if(rr!=null&&!rr.isEmpty()){
					for(int i=0;i<rows.length;i++){
						Double d = rr.get(rows[i]);
						if(d!=null){
							data[i][index] = d;
						}
						
					}
				}
				index++;
			}
			List<String> anny =Arrays.asList("A","B","C","D","E","F","G","H","I","J");
			String[] temp = new String[columns.length];
		
			for(int i=0;i<temp.length;i++){
				temp[i] = anny.get(i);
			}
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rows, temp, data); 

			JFreeChart chart = ChartFactory.createBarChart3D(instanceName+"评分情况", 
			                  "人员",
			                  "分数",
			                  dataset,
			                  PlotOrientation.VERTICAL,
			                  true,
			                  true,
			                  false);
			CategoryPlot plot = chart.getCategoryPlot();  
			chart.getTitle().setFont(new Font("宋体", Font.BOLD,12));
			//设置网格背景颜色
			plot.setBackgroundPaint(Color.white);
			//设置网格竖线颜色
			plot.setDomainGridlinePaint(Color.pink);
			//设置网格横线颜色
			plot.setRangeGridlinePaint(Color.pink);
			plot.getDomainAxis().setLabelFont(new Font("宋体", Font.PLAIN, 12)); 
			ValueAxis numberaxis = plot.getRangeAxis();
			numberaxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
			numberaxis.setLabelFont(new Font("黑体", Font.PLAIN, 12)); 
			chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
			//显示每个柱的数值，并修改该数值的字体属性
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelsVisible(true);

			//默认的数字显示在柱子中，通过如下两句可调整数字的显示
			//注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题
			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
			renderer.setItemLabelAnchorOffset(10D);
			  //设置bar的最小宽度，以保证能显示数值
		       renderer.setMinimumBarLength(0.01);
		      
		       //最大宽度
		       renderer.setMaximumBarWidth(0.03);
			//设置每个地区所包含的平行柱的之间距离
			renderer.setItemMargin(0.4);
			plot.setRenderer(renderer);
			UIUtils.responseChart(chart, request, response);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			
		}
		ModelAndView modelAndView = new ModelAndView();
		 return modelAndView;
	}
	
	@NeedlessCheckLogin
	public ModelAndView saveWeight(HttpServletRequest request,HttpServletResponse response)
	{
		String userName = request.getParameter("userName");
		try{
			userName = unescape(userName);
		}catch(Exception e){
			
		}finally{
			
		}
		String procId = request.getParameter("procId");	
		String weight = request.getParameter("weight");
//		userName = "刘鹏";
//		procId ="-3259229776818974829"; 
//		weight = 89+"";
		Long id = -1L;
//		String sql = "select workflow from v3x_templete where id="+procId;
		String sql = "select * from v3x_templete where id="+procId;
		
		Session session = ratingService.getRatingDao().getSessionSelf();
		 RatingUserWeight usrweight = null;
		try{
			Templete obj = (Templete)session.createSQLQuery(sql).addEntity(Templete.class).list().get(0);
			 id = this.getAccountName(obj.getWorkflow(), userName);
			 
			 sql="select * from "+ratingService.getRatingDao().RATING_USER_WEIGHT_TABLE_NAME+" where userId="+id+" and procId="+procId;
			 List<RatingUserWeight> weightList =  session.createSQLQuery(sql).addEntity(RatingUserWeight.class).list();
			 //有结果证明是新增需要update就行
			 if(weightList != null&&!weightList.isEmpty()){
				 usrweight = weightList.get(0);
				 usrweight.setWeight(Integer.parseInt(weight));
			 }else{
				usrweight = new RatingUserWeight();
			  	usrweight.setProcId(Long.parseLong(procId));
				usrweight.setUserId(id);
				usrweight.setWeight(Integer.parseInt(weight));
			 }
			ratingService.saveRatingUserWeight(usrweight);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ratingService.getRatingDao().closeSessionIfNewOpen(session);
		}
	
		
		ModelAndView modelAndView = new ModelAndView();
		return modelAndView;
	}
	
	
	public  Long getAccountName(String str,String name){
		int index = str.indexOf(name);
		 str = str.substring(index,str.length());
		int index2 = str.indexOf("accountId");
		String[] account = str.substring(index2,index2+32).split("\"");
		for(String acc:account){
			try{
				Long accId = Long.parseLong(acc);
				return accId;
			}catch(Exception e){
				
			}
		}
		return -1L;
	
	}
	
	public void getTemplateData(HttpServletRequest request,HttpServletResponse response){
		Collection<TemplateInfo> collection = ratingService.getTemplateInfoList().values();
		Map<String,String> setIndicator = new HashMap<String,String>();
		List<TemplateInfo> infoList = new ArrayList<TemplateInfo>();
		
		for(TemplateInfo info:collection){
			if(setIndicator.get(info.getTemplateId())!=null){
				continue;
			}else{
				setIndicator.put(info.getTemplateId(), info.getTemplateId());
			}
			
			infoList.add(info);
		}
		UIUtils.responseJSON(infoList, response);
	}
	public  void getWeight(HttpServletRequest request,HttpServletResponse response){
		String userName = request.getParameter("userName");
		try{
			userName = unescape(userName);
		}catch(Exception e){
			
		}
		String procId = request.getParameter("procId");	
		String sql = "select * from v3x_templete where id="+procId;
		
		Session session = ratingService.getRatingDao().getSessionSelf();
		Long id =-1L;
		RatingUserWeight weight = null;
		Integer val = 50;
		try{
			Templete obj = (Templete)session.createSQLQuery(sql).addEntity(Templete.class).list().get(0);
			 id = this.getAccountName(obj.getWorkflow(), userName);
			 sql="select * from "+ratingService.getRatingDao().RATING_USER_WEIGHT_TABLE_NAME+" where userId="+id+" and procId="+procId;
			 List<RatingUserWeight> weightList =  session.createSQLQuery(sql).addEntity(RatingUserWeight.class).list();
			 if(weightList != null&&!weightList.isEmpty()){
				 weight = weightList.get(0);
			 }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ratingService.getRatingDao().closeSessionIfNewOpen(session);
		}
		
		
		if(weight != null){
			val = weight.getWeight();
		}
		
		UIUtils.responseJSON(val, response);
	
	}
	public static String unescape(String s) {  
        StringBuffer sbuf = new StringBuffer();  
        int i = 0;  
        int len = s.length();  
        while (i < len) {  
            int ch = s.charAt(i);  
            if (ch == '+') {                        // + : map to ' '    
                sbuf.append(' ');  
            } else if ('A' <= ch && ch <= 'Z') {    // 'A'..'Z' : as it was   
                sbuf.append((char)ch);  
            } else if ('a' <= ch && ch <= 'z') {    // 'a'..'z' : as it was   
                sbuf.append((char)ch);  
            } else if ('0' <= ch && ch <= '9') {    // '0'..'9' : as it was   
                sbuf.append((char)ch);  
            } else if (ch == '-' || ch == '_'       // unreserved : as it was   
                || ch == '.' || ch == '!'  
                || ch == '~' || ch == '*'  
                || ch == '/' || ch == '('  
                || ch == ')') {  
                sbuf.append((char)ch);  
            } else if (ch == '%') {  
                int cint = 0;  
                if ('u' != s.charAt(i+1)) {         // %XX : map to ascii(XX)   
                    cint = (cint << 4) | val[s.charAt(i+1)];  
                    cint = (cint << 4) | val[s.charAt(i+2)];  
                    i+=2;  
                } else {                            // %uXXXX : map to unicode(XXXX)   
                    cint = (cint << 4) | val[s.charAt(i+2)];  
                    cint = (cint << 4) | val[s.charAt(i+3)];  
                    cint = (cint << 4) | val[s.charAt(i+4)];  
                    cint = (cint << 4) | val[s.charAt(i+5)];  
                    i+=5;  
                }  
                sbuf.append((char)cint);  
            }  
            i++;  
        }  
        return sbuf.toString();  
    }  

	private final static String[] hex = {  
        "00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",  
        "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",  
        "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",  
        "30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",  
        "40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",  
        "50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",  
        "60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",  
        "70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",  
        "80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",  
        "90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",  
        "A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",  
        "B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",  
        "C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",  
        "D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",  
        "E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",  
        "F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"  
    };  
    private final static byte[] val = {  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,  
        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F  
    };  


}
