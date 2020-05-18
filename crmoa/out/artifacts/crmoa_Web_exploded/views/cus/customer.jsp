<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java"  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>客户信息管理</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/skin/css/base.css">
    <script type="application/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
</head>
<body leftmargin="8" topmargin="8" background='skin/images/allbg.gif'>

<!--  快速转换位置按钮  -->
<table width="98%" border="0" cellpadding="0" cellspacing="1" bgcolor="#D1DDAA" align="center">
<tr>
 <td height="26" background="skin/images/newlinebg3.gif">
  <table width="58%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td >
    当前位置:客户信息管理>>客户信息
 </td>
  <td>
    <input type='button' class="coolbg np" onClick="location='views/cus/customer-add.jsp';" value='添加客户信息' />
 </td>
 </tr>
</table>
</td>
</tr>
</table>

<!--  搜索表单  -->
<form action="cus" method="post" id="seform">
<table width='98%'  border='0' cellpadding='1' cellspacing='1' bgcolor='#CBD8AC' align="center" style="margin-top:8px">
  <tr bgcolor='#EEF4EA'>
    <td background='skin/images/wbg.gif' align='center'>
      <table border='0' cellpadding='0' cellspacing='0'>
        <tr>
          <td width='90' align='center'>搜索条件：</td>
          <td width='160'>
          <input type="hidden" value="1" name="pageNO" id="pg">
          <select id="mt" name='cid' style='width:150'>
            <option value='0'>选择类型...</option>
              <option value='1' <c:if test="${qo.cid==1}">selected</c:if> >客户所在公司名称</option>
          	<option value='2'  <c:if test="${qo.cid==2}">selected</c:if>>联系人姓名</option>
          </select>
        </td>
        <td width='70'>
          关键字：
        </td>
        <td width='160'>
          	<input type='text' id="info" value="${qo.keyword}"  name='keyword' style='width:120px' />
        </td>
        <td width='110'>
    		<select id="mtime" name='orderby' style='width:120px'>
                <option value='0'>排序...</option>
                <option value='1'  <c:if test="${qo.orderby==1}">selected</c:if>>编号</option>
      	    </select>
        </td>
        <td>
          &nbsp;&nbsp;&nbsp;<input name="imageField" type="image" src="${pageContext.request.contextPath}/skin/images/frame/search.gif" width="45" height="20" border="0" class="np" />
        </td>
       </tr>
      </table>
    </td>
  </tr>
</table>
</form>
<!--  内容列表   -->
<form name="form2" action="cus/delcus" method="post" id="cform">

<table id="infotb" width="98%" border="0" cellpadding="2" cellspacing="1" bgcolor="#D1DDAA" align="center" style="margin-top:8px">
<tr bgcolor="#E7E7E7" >
	<td height="24" colspan="12" background="skin/images/tbg.gif">&nbsp;需求列表&nbsp;</td>
</tr>
<tr align="center" bgcolor="#FAFAF1" height="22" id="tr2">
	<td width="4%">选择</td>
	<td width="6%">序号</td>
	<td width="10%">联系人</td>
	<td width="10%">公司名称</td>
	<td width="8%">添加时间</td>
	<td width="8%">联系电话</td>
	<td width="10%">操作</td>
</tr>

<c:forEach items="${info.list}" var="cus">
<tr align='center' bgcolor="#FFFFFF" onMouseMove="javascript:this.bgColor='#FCFDEE';" onMouseOut="javascript:this.bgColor='#FFFFFF';" height="22" >
	<td><input name="id" type="checkbox" id="id" value="${cus.id}" class="np"></td>
	<td>${cus.id}</td>
	<td>${cus.companyperson}</td>
	<td align="center">${cus.comname}</td>
	<td>
        <fmt:formatDate value="${cus.addtime}" pattern="yyyy-MM-dd"></fmt:formatDate>
    </td>
	<td>${cus.comphone}</td>
	<td>
        <a href="cus/getCusByCid?cid=${cus.id}">编辑</a> |
        <a href="cus/getCusByCid?cid=${cus.id}&flag=1">查看详情</a></td>
</tr>
</c:forEach>
    <tr>
       <td colspan="7">
           <div id="pager" style="width:20%;float:right">

           </div>
       </td>
    </tr>

    <link href="page/pagination.css"  type="text/css" rel="stylesheet" />
    <script type="text/javascript" src="page/jquery-1.10.2.min.js" ></script>
    <script type="text/javascript" src="page/jquery.pagination.js" ></script>
    <script type="text/javascript">
        //初始化分页组件
        var count=${info.total};//总条数
        var size=${info.pageSize};//每页条数
        var pageNO=${info.pageNum};//页码
        //alert(count+"==="+size+"==="+pageNO);
        $("#pager").pagination(count, {
            items_per_page:size,
            current_page:pageNO-1,
            next_text:"下一页",
            prev_text:"上一页",
            num_edge_entries:2,
            load_first_page:false,
            callback:handlePaginationClick
        });

        //回调方法（点击上一页，下一页，某个页码时执行的函数）
        function handlePaginationClick(new_page_index, pagination_container){
            $("#pg").val(new_page_index+1);//查询的页面保存到隐藏域
            $("#seform").submit();
        }
    </script>

<tr bgcolor="#FAFAF1">
<td height="28" colspan="12">
	<a href="javascript:void(0)" class="coolbg" id="chall">全选</a>
	<a href="javascript:void(0)" class="coolbg" id="chrev">反选</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="Javascript:delcus()" class="coolbg">&nbsp;删除&nbsp;</a>
	<a href="cus/exportExl" class="coolbg">&nbsp;导出Excel&nbsp;</a>
</td>
</tr>
<tr align="right" bgcolor="#EEF4EA">
	<td height="36" colspan="12" align="center"><!--翻页代码 --></td>
</tr>
</table>
</form>
<script type="text/javascript">
    $(function () {
      //全选
      $("#chall").click(function () {
         $("input[name='id']").prop("checked",true);
      });
      //反选
      $("#chrev").click(function () {
          $("input[name='id']").each(function () {
              $(this).prop("checked",!$(this).prop("checked"));
          })
      });
    })
    function delcus() {
      var len=$("input[name='id']:checked").length;
      if(len>0){
          $("#cform").submit();
      }else{
          alert("请选择要删除的数据");
      }

    }
</script>

</body>
</html>