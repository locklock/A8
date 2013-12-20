<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@ page import="org.jfree.chart.*,org.jfree.chart.plot.*,org.jfree.chart.labels.*,org.jfree.data.category.*,java.awt.*,org.jfree.ui.*,org.jfree.chart.renderer.category.BarRenderer3D,org.jfree.chart.servlet.*,org.jfree.chart.plot.PlotOrientation,org.jfree.data.general.DatasetUtilities,org.jfree.chart.title.TextTitle,org.jfree.chart.labels.*,
org.jfree.data.general.*,org.jfree.chart.servlet.ServletUtilities,java.awt.*,
java.text.NumberFormat,org.jfree.chart.plot.PiePlot3D,org.jfree.util.Rotation,com.seeyon.v3x.plugin.rating.util.*" %>

<%
double[][] data = new double[][] {{1230,1110,1120,1210}, {720,750,860,800}, {830,780,790,700,}, {400,380,390,450}};
String[] rowKeys = {"苹果", "香蕉", "橘子", "梨子"};
String[] columnKeys = {"鹤壁","西安","深圳","北京"};
CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data); 

JFreeChart chart = ChartFactory.createBarChart3D("水果销量统计图", 
                  "水果",
                  "销量",
                  dataset,
                  PlotOrientation.VERTICAL,
                  true,
                  true,
                  false);
CategoryPlot plot = chart.getCategoryPlot();  
//设置网格背景颜色
plot.setBackgroundPaint(Color.white);
//设置网格竖线颜色
plot.setDomainGridlinePaint(Color.pink);
//设置网格横线颜色
plot.setRangeGridlinePaint(Color.pink);

//显示每个柱的数值，并修改该数值的字体属性
BarRenderer3D renderer = new BarRenderer3D();
renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
renderer.setBaseItemLabelsVisible(true);

//默认的数字显示在柱子中，通过如下两句可调整数字的显示
//注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题
renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
renderer.setItemLabelAnchorOffset(10D);

//设置每个地区所包含的平行柱的之间距离
renderer.setItemMargin(0.4);
plot.setRenderer(renderer);

//设置地区、销量的显示位置
//将下方的“肉类”放到上方
//plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
//将默认放在左边的“销量”放到右方
//plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);


String filename = ServletUtilities.saveChartAsPNG(chart, 500, 300, null, session);
String graphURL = request.getContextPath() + "/bar.chart?filename=" + filename;
%>
<img src="<%= graphURL %>" width=530 height=320 border=0>
<%
//设置饼图数据集
DefaultPieDataset dataset2 = new DefaultPieDataset();

dataset2.setValue("黑心矿难", 720);
dataset2.setValue("醉酒驾驶", 530);
dataset2.setValue("城管强拆", 210);
dataset2.setValue("医疗事故", 91);
dataset2.setValue("其他", 66);

//通过工厂类生成JFreeChart对象
 chart = ChartFactory.createPieChart3D("非正常死亡人数分布图",dataset2,true,true,false);
chart.addSubtitle(new TextTitle("2010年度"));
PiePlot pieplot = (PiePlot) chart.getPlot();
pieplot.setLabelFont(new Font("宋体", 0, 11));
StandardPieSectionLabelGenerator standarPieIG = new StandardPieSectionLabelGenerator("{0}:({1},{2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
pieplot.setLabelGenerator(standarPieIG);

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
ChartRenderingInfo info = new ChartRenderingInfo();
String url = "./";
pieplot3d.setURLGenerator(new  org.jfree.chart.urls.StandardPieURLGenerator(url,"goods")); 
String filename2 = ServletUtilities.saveChartAsPNG(chart, 500, 300, null, session);
String graphURL2 = request.getContextPath() + "/pie.chart?filename=" + filename2;
%>
<img src="<%= graphURL2 %>" width=490 height=306 border=0 ></span>

