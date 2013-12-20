
$(document).ready(function() {
	var type =[];
	$.post("rating.forthelichking",{"method":"getConfigItemList","procId":65535,"userId":"123"},function(data){
		if(data ==[]||data==""){
			data.push({"itemName":"满意度"});
		}
			
			 type = [];
			for(var p in data){
				type.push(data[p].itemName);
			}
			rendering(type);
		
		$("#submit").show();
		
	});
	
	//利用配置完成初始化，configItem 应该是后端请求回来的List<RatingItem> json 化后的结果//
	function rendering(type){
		
		var maxScore=5,maxSection=5,section = maxScore/maxSection,defaultScore=1;
		
		var html=[];
		for(var p in type){
			html.push("<div class='starContainer'><form>");
			html.push("<div style='float:left' class='Clear label'>"+type[p]+":</div>");
			html.push("<div  class='star-rating-control'>");
			for(var k =0;k<maxSection;k++){
				if((k+1)==defaultScore){
					html.push("<input type='radio'class='star' checked value='"+(section*(k+1))+"' />");
				}else{
					html.push("<input type='radio'class='star'  value='"+(section*(k+1))+"' />");
				}
			}
			html.push("</div>");	
			html.push("<div code='"+p+"' value='"+defaultScore+"' class='score'>"+defaultScore+"分</div>");
			html.push("</form></div>");
		}
		
		
		$("#rating_container").html(html.join(""));
		
		$("#rating_container").append("<div class='split'></div><div id='commentpre' class='commit' >请写下您宝贵的建议:</div>");
		$("#rating_container").append("<div class='split'></div><textarea id='commit' style='width:200px;height:120px'></textarea><div class='split'></div>");
		
		var curValue = "";
		$("input.star").rating({
			
		callback: function(value, link){
			curValue = value;
		 }
		,focus:function(value, link){ 
				//window.console.log(value);
		}
		,blur: function(value, link){ 
				//window.console.log(value);
				//alert(value);
		} 
		
		});
		$(".rating-cancel").remove();
		$(".starContainer").show();
		$(".starContainer").bind("click",function(e){
			var name = e.target.nodeName;
			if(name!="A"&&name!="a"){
				return ;
			}
			var t = $(e.target).parent().parent().parent().next();
			t.html(curValue+"分");
			t.attr("value",curValue);
		});
		
	}
	$("#submit").hide();
	/**
	 * 结构整理
	 */
	$("#submit").click(function(){
		var ret=[];
		$(".score").each(function(i,item){
			item = $(item);
			ret.push(type[parseInt(item.attr("code"))]+":"+item.attr("value"));
		});
		ret.join(",");
		/**
		 	String dataList = request.getParameter("dataList");
		String procId = request.getParameter("procId");
		String instanceId = request.getParameter("instanceId");
		String userId = request.getParameter("userId");
		String comment = request.getParameter("comment");
		 */
		$.post("rating.forthelichking",{method:"saveItemResult",userId:$("#userId").val(),"comment":$("#commit").val(),dataList:ret.join(","),procId:$("#procId").val(),instanceId:$("#instanceId").val(),subject:$("#subject").val()},function(data){
			if(!data){
				alert("保存失败");
			}else{
				$("#commentpre").html("您已经成功评价,感谢您的配合！");
				$("#submit").hide();
				//window.close();
			}
			
		});
		
	});

	

});
