<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="common/js/plugin/rating/rating.css" />
<script type="text/javascript" charset="UTF-8" src="common/js/plugin/rating/jquery.js"></script>
</head>
<body>


<script>
$(document).ready(function(){
	$.post("rating.do",{method:"showCountTable",procId:65535},function(data){
		
		//console.log(data);
		var ret=[];
		ret.push("<tr><td>序号</td><td>公文标题（现在时ID）</td><td>分数</td></tr>");
		var tag =1;
		for(var p in data){
			//console.log();
			var tr1 = data[p];
			
			$.each(tr1,function(index,item){
				ret.push("<tr><td>"+tag+"</td>");
				ret.push("<td>"+item+"</td>");
				ret.push("<td>"+p+"</td>");
				ret.push("</tr>");
				tag++;
			});
			
		}
		$("#countTable").html(ret.join(""));
		
	});

});

</script>
<div style="width:800px;height:600px" >
<img src="rating.do?method=showPieChart&procId=65535&width=500&height=300" />
<div style="float:right">
<table id="countTable" style="border:1px solid;">



</table>
</div>
<img src="rating.do?method=showBarChart&procId=65535&instanceId=65535&width=500&height=300" />
<table id="commentTable" style="border:1px solid;">



</table>
</div>
</body>
</html>




