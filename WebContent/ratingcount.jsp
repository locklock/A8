<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="common/js/plugin/rating/rating.css" />
 <link rel="stylesheet" href="common/js/plugin/rating/pagination.css" />


        <!-- Load data to paginate -->
<script type="text/javascript" charset="UTF-8" src="common/js/plugin/rating/jquery.js"></script>
<script type="text/javascript" src="common/js/plugin/rating/jquery.pagination.js"></script>

<!-- 可选的Bootstrap主题文件（一般不用引入） -->

<link rel="stylesheet" type="text/css" href="common/js/plugin/rating/bootstrap3/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="common/js/plugin/rating/bootstrap3/bootstrap-theme.min.css" />
<style>
.con{
	width:90%;
}
.borders{
	border: 1px solid lightblue;

}
.borderTable{

}
.pagination{

margin-top:2px;
}
table.gridtable {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}
</style>
</head>
<body>


<script>
$(document).ready(function(){
	var countTablePagingData=false,commitTableData=false;
	
	  function getPagingOptions(callback){
          var opt = {callback: callback?callback:pageselectCallback};
 		  opt.items_per_page =5;
 		  opt.num_display_entries =10;
 		  opt.num_edge_entries =2;
 		  opt.prev_text="上一页";
 		  opt.next_text="下一页";
          return opt;
      }
	  var pOpt = getPagingOptions();
	  function pageselectCallback(page_index, jq){
            drawCountTable(pOpt.items_per_page,page_index);
            return false;
       }
	  function pageselectCallback2(page_index, jq){
		  drawCommentTable(pOpt.items_per_page,0);
          return false;
     }
var templateType = 1;	  

//---------华丽的分割线--------//
function initEventHandler(){
	$("#monthselect option[value='"+(new Date().getMonth()+1)+"']").attr("selected","selected");
	
	$("#monthselect").change(function(){
		loading();
	});
	
	$("#liuchengradio").change(function(){
		templateType =1;
		$("#liucheng").show();
		$("#gongwen").hide();
		loading();
	});
	
	$("#gongwenradio").change(function(){
		templateType=2;
		$("#liucheng").hide();
		$("#gongwen").show();
		loading();
	});
	
	$("#liucheng").change(function(){
		loading();
	});
	
	$("#gongwen").change(function(){
		loading();
		
	});
	$.post("rating.forthelichking",{method:"getTemplateData"},function(data){
		
		//console.log(data);
		$("#liuchengselect").html("");
		var liucheng=[],gongwen=[];
		$(data).each(function(index,item){
			//console.log(item);
			var str = "<option value='"+item.templateId+"'>"+item.templateName+"</option>";
			if(item.type==1){
				liucheng.push(str);
			}else{
				gongwen.push(str);
			}
			
		});
		$("#liuchengselect").html(liucheng.join(""));
		$("#gongwenselect").html(gongwen.join(""));
		$("#liuchengradio").trigger("change");
	});
	
	
}
function getTemplateType(){
	
	return templateType;
	
}
initEventHandler();

function loading(){
	//loading pie all
	//alert(getTemplateType());
	var time =$("#monthselect").val(),type=templateType,selectTemplate="";
	if(type==1){
		selectTemplate=$("#liuchengselect").val();
	}else{
		selectTemplate=$("#gongwenselect").val();
	}
	if(selectTemplate){
		$("#countBar").hide();
		$("#tableparent").hide();
	$.post("rating.forthelichking",{method:"showCountTable",procId:selectTemplate,"type":type,time:time},function(data){
		 countTablePagingData = data;
		 var optInit = getPagingOptions();
		  $("#pagination").pagination(countTablePagingData.length||0, optInit);
		//console.log(data);
		  drawCountTable(optInit.items_per_page,0);
		
	});
	$("#countPie")[0].src ='rating.forthelichking?method=showPieChart&&time='+time+'&type='+type+'&templateId='+selectTemplate+'&width=500&height=300';
	}
	
	
}
function getTemplateId(){
	
	if(templateType==1){
		return $("#liuchengselect").val();
	}
	return $("#gongwenselect").val();;
	
}

function getMonthTime(){
	return $("#monthselect").val();
}
	  
	  
	  
//-----------for the lich king-------------//

	
function drawCountTable(num_per_page,index){
		var ret=[];
		ret.push("<tr><th width='15%'>序号</th><th>公文标题</th><th width='15%'>分数</th></tr>");
		
		var tag = 1+index*num_per_page,count=0;
		for(var p in countTablePagingData){
			//console.log();
			if(p<(tag-1)){
				continue;
			}
			if(count>=num_per_page){
				break;
			}
			var item = countTablePagingData[p];

			ret.push("<tr ><td>"+tag+"</td>");
			ret.push("<td><a style='color:lightblue;cursor:pointer;text-decoration:underline;' id='"+item.instanceId+"____"+item.instanceName+"'>"+item.instanceName+"</a></td>");
			ret.push("<td>"+item.score+"</td>");
			ret.push("</tr>");
			tag++;
			count++;
			
			
		}
		$("#countTable").html(ret.join(""));
		
		
	}

function drawCommentTable(per_page,index){
	var tbl =$("#commentTable");
	var ret = [],tag=1+index*per_page,count=0;
	ret.push("<tr><th>序号</th><th>人员</th><th>评价</th>");
	for(var p in commitTableData){
		if(count>=per_page){
			break;
		}
		if(p<(tag-1)){
			continue;
		}
		ret.push("<tr>");
		ret.push("<td>"+tag+"</td>");
		ret.push("<td><a style='color:lightblue;text-decoration:none'>"+commitTableData[p].instanceName+"</a></td>");
		ret.push("<td>"+commitTableData[p].score+"</td>");
		ret.push("</tr>");
		tag++;
		count++;
	}
	tbl.html(ret.join(""));
}	
$("#countTable").click(function(e){
		var t = e.target;
		var id = $(t).attr("id");
		if(id){
			var ss = id.split("____");
			if(ss.length!=2){
				return ;
			}
			//show tabel
			var templateId = getTemplateId();
			$.post("rating.forthelichking",{method:"showBarCharComment","templateId":templateId,type:templateType,time:getMonthTime(),instanceId:ss[0],instanceName:ss[1]},function(data){
				
				$("#tableparent").show();
				if(data){
					var opt = getPagingOptions(pageselectCallback2);
					$("#pagination2").pagination(data.length||0, opt);
					commitTableData = data;
					drawCommentTable(opt.items_per_page,0);
				}
				
				
			});
			$("#countBar").show();
			//alert(ss[0]);
			//alert(ss[1]);
			//alert(escape(ss[1]));
			//alert(encodeURIComponent(ss[1]));
			$("#countBar")[0].src="rating.forthelichking?method=showBarChart&templateId="+templateId+"&width=1000&time="+getMonthTime()+"&height=400&type="+templateType+"&instanceId="+ss[0]+"&instanceName="+encodeURIComponent(ss[1]);
			  //$("#countBar")[0].src=encodeURI("rating.forthelichking?method=showBarChart&width=500&height=300&instanceId="+ss[0]+"&instanceName="+ss[1]+"&procId=65535");
		}
	});
	$("#countBar").hide();
	$("#tableparent").hide();
});

</script>
<div class="con container" style="padding:20px">
<table style="width:100%;" class="con borders">
<tr style="border: 1px solid lightblue;padding-top:10px">
<td style="padding-top:10px">
<label for="monthselect" style="padding-left:170px">月份选择</label>
<select id="monthselect">
  <option value ="1">一月份</option>
  <option value ="2">二月份</option>
  <option value="3">三月份</option>
  <option value="4">四月份</option>
  <option value="5">五月份</option>
  <option value="6">六月份</option>
  <option value="7">七月份</option>
  <option value="8">八月份</option>
  <option value="9">九月份</option>
  <option value="10">十月份</option>
  <option value="11" >十一月份</option>
  <option value="12">十二月份</option>
</select>
<span style="float:right">
<label for="liuchengradio" style="padding-left:150px">流程</label><input id="liuchengradio" type="radio" name="templeteType" checked value="liuchen"/>
<label for="gongwenradio" style="padding-left:20px">公文</label><input id="gongwenradio" type="radio" name="templeteType" value="gongwen"/>
</span>


</td>
<td>
<span id="liucheng" style="padding-left:150px">
<label for="liuchengselect">流程模版名称选择</label>
<select id="liuchengselect">

</select>
</span>
<span id="gongwen" style="padding-left:150px">
<label for="gongwenselect">公文模版名称选择</label>
<select id="gongwenselect">

</select>
</span>

</td>
</tr>
<tr><td style='width:600px;margin-right:10px;border: 1px solid lightblue'><div class="alert">


<img id="countPie" src="common/js/plugin/rating/big_load.gif" /></div></td><td style="border: 1px solid lightblue">
<div class='alert' >

<table id="countTable" class="con borders gridtable">

</table>
<div id="pagination" class="pagination"></div>
</div></td></tr>

<tr><td colspan="2">
<div class='alert' id='tableparent' style="border: 1px solid lightblue">
<center>
<img id= "countBar" src="common/js/plugin/rating/big_load.gif" />
<div style="width:2px;height:20px"></div>
<table id="commentTable" class="con borders gridtable">

</table>
</center>
<div style="margin-left:60px">
<div id="pagination2" class="pagination"></div>
</div>
</div>

</td></tr>
</table>






</div>
</body>
</html>




