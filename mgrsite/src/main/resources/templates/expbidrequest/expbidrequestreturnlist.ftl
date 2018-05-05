<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>蓝源Eloan-P2P平台(系统管理平台)</title>
<#include "../common/header.ftl"/>
<script type="text/javascript" src="/js/plugins/jquery.form.js"></script>
<script type="text/javascript" src="/js/plugins/jquery.twbsPagination.min.js"></script>
<script type="text/javascript" src="/js/My97DatePicker/WdatePicker.js" ></script>

<script type="text/javascript">
	$(function() {

		$('#pagination').twbsPagination({
			totalPages : ${pageResult.pages}||1,
			startPage : ${qo.currentPage},
			visiblePages : 5,
			first : "首页",
			prev : "上一页",
			next : "下一页",
			last : "最后一页",
			onPageClick : function(event, page) {
				$("#currentPage").val(page);
				$("#searchForm").submit();
			}
		});

		$("#query").click(function(){
			$("#currentPage").val(1);
			$("#searchForm").submit();
		});

        $(".return_money").click(function(){
            $.ajax({
                dataType: "json",
                type: "POST",
                url: "/returnMoney.do",
                data: {id: $(this).data("rid")},
                success: function (data) {
                    if (data.success) {
                        $.messager.confirm("提示", "还款成功", function () {
                            window.location.reload();
                        });
                    } else {
                        $.messager.popup(data.msg);
                    }
                }
            });
        });

	});
</script>
</head>
<body>
	<div class="container">
		<#include "../common/top.ftl"/>
		<div class="row">
			<div class="col-sm-3">
				<#assign currentMenu="expBidRequestReturnList" />
				<#include "../common/menu.ftl" />
			</div>
			<div class="col-sm-9">
				<div class="page-header">
					<h3>体验金管理</h3>
				</div>
				<form id="searchForm" class="form-inline" method="post" action="/expBidRequestReturn_list.do">
					<input type="hidden" id="currentPage" name="currentPage" value=""/>
				</form>
				<div class="panel panel-default">
					<table class="table">
						<thead>
							<tr>
                                <th>体验标</th>
                                <th>结算金额</th>
                                <th>结算体验金</th>
                                <th>结算利息</th>
                                <th>结算期限</th>
                                <th>结算状态</th>
							</tr>
						</thead>
						<tbody>
						<#list pageResult.list as data>
                        <tr>
                            <td><a href="/borrow_info.do?id=${data.bidRequestId}">${data.bidRequestTitle}</a></td>
                            <td>${data.totalAmount}元</td>
                            <td>${data.principal}元</td>
                            <td>${data.interest}元</td>
                            <td>${data.deadLine?string("yyyy-MM-dd")}</td>
                            <td>
								<#if data.state=0>
                                    <a class="btn btn-success return_money" data-returnmoney="${data.totalAmount?string('0.##')}" data-rid="${data.id}">立刻还款</a>
								<#else>
								${data.stateDisplay}
								</#if>
                            </td>
                        </tr>
						</#list>
						</tbody>
					</table>
					<div style="text-align: center;">
						<ul id="pagination" class="pagination"></ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>