<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="common/js/plugin/rating/rating.css" />
<script type="text/javascript" charset="UTF-8" src="common/js/plugin/rating/jquery.js"></script>
<style>
.info{

	color:black;
	font:20px;
	font-weight:bold;

}

</style>

</head>
<body>


<div id="configcontainer" style="width:300px">

 <fieldset style='color:#428bca;border:1px solid #428bca;height:60px;padding:5px'>
    <legend style='color:#428bca;font:18px bold;' >打分维度设置</legend>
    <input id='val' type="text" />
    <input type="button" style='font:14px bold;width:70px;height:25px;' id="submit" value=" 确 认 "></input>

  </fieldset>
</div>
<div  style="width:300px;margin-top:20px">
 <fieldset style='color:#428bca;border:1px solid #428bca'>
    <legend style='color:#428bca;font:18px bold;'>已有维度</legend>
    <div id="banner">
    
    
    </div>

  </fieldset>

</div>


<script>
$(document).ready(function(){
	var ret=0;
$.post("rating.forthelichking",{"method":"getConfigItemList","procId":65535},function(data){
		var exist=[];
		if((data instanceof Array)&&data.length==0){
			 exist=["满意度"];
			 $.post("rating.do",{"method":"saveItem","val":exist[0],"procId":65535});
		}else{
			$.each(data,function(i,item){
				exist.push(item.itemName);
			});
		}
		
		for(var p in exist){
			var val = exist[p];
			if(val =="满意度"){
				$("#banner").append($("<div style='margin-left:5px;height:30px'><span class='info' style='width:200px;padding-right:20px'>"+val+"</span></div>"));
			}else{
				$("#banner").append($("<div style='margin-left:5px;height:30px'><span class='info' style='width:200px;padding-right:20px'>"+val+"</span><img class='delimg' style='cursor:pointer' src='common/js/plugin/rating/delete16.png' /></div>"));
			}
		}
		ret = exist.length;
});
	

	
	
	$("#submit").click(function(){
		if(ret==5){
			alert("最多只能添加5个评价维度!");
			return ;
		}
		var val = $("#val").val();
		$.post("rating.forthelichking",{"method":"saveItem","val":val,"procId":65535},function(data){
			//正式代码写这里
			if(data){
			$("#banner").append($("<div style='height:30px;margin-left:5px;' ><span class='info' style='width:200px;padding-right:20px'>"+val+"</span><img class='delimg' style='cursor:pointer' src='common/js/plugin/rating/delete16.png' /></div>"));
			ret++;
			}else{
				alert("保存失败!");
			}
		});
		
	});
	

	
	$("#banner").bind("click",function(e){
		
		if(e.target.className=="delimg"){
			$.post("rating.forthelichking",{"method":"deleteItem","val":$(e.target).prev().html(),"procId":65535},function(data){
				if(!data){
					alert("删除失败");
				}else{
					ret--;
					$(e.target).parent().remove();
					
				}
			});
			
		}
		
	});
	
	
});



</script>
</body>
</html>