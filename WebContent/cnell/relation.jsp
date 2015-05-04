<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="knowledgeBase.*"%>
<%@ page import="dao.CNELL_DAO"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="cnell.structure.Triple"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	// Currently I use js to replace some functions in JSP
	// Change to Java is OK

	/***********TEMPLATE************/
	String siteTitle = "CNELL";
	String cssPath = "../resources/css/dashboard.css";
	int defaultFuncIndex = 1;
	// functions & links & introduction & template
	List<String[]> functions = new ArrayList<String[]>();
	functions.add(new String[]{"Introduction","introduction","Brief introduction", "#"});
	functions.add(new String[]{"Relation","relation","Brief introduction","SEARCH"});
	functions.add(new String[]{"Download","#","Brief introduction","#"});
	functions.add(new String[]{"People","#","Brief introduction","#"});
	functions.add(new String[]{"Publication","#","Brief introduction","#"});
	functions.add(new String[]{"GDM Lab","http://gdm.fudan.edu.cn","Brief introduction","#"});
	
	String date =request.getParameter("date");
	if(date==null){
		date = CNELL_DAO.currDate();
	}
	// placeholder of search form
	String yyyy = "20"+date.substring(0,2);
	String mm = date.substring(2,4);
	String dd = date.substring(4,6);
	
	int currentFuncIndex = defaultFuncIndex;
	String[] curr_func = functions.get(currentFuncIndex);
	request.setAttribute("curr_tpl",curr_func[3]);
	request.setAttribute("functions",functions);
	request.setAttribute("currentFunc", curr_func[0]);
	// template for search
	// TODO
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">

<title><%=siteTitle%></title>

<!-- Bootstrap core CSS -->
<link href="../resources/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="../resources/css/dashboard.css" rel="stylesheet">


<!-- move to the top cause the datatable plug must be initialized at the beginning-->
<script src="../resources/js/jquery.min.js"></script>
<script src="../resources/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../resources/media/js/jquery.dataTables.js"></script>

<!-- initialize datatable -->   
 <script type="text/javascript">	
	$(document).ready(function() {
		$('#datatable').dataTable(
			{
				"searching":false,
				"paging":false
			}
		);		
	});
</script> 

<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]--> 
    
</head>

<body>

	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand"
					href="http://gdm.fudan.edu.cn/KnowledgeWorks/cnell"><%=siteTitle%></a><a
					class="navbar-brand subbrand" href="/KnowledgeWorks">Knowledge
					Works</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="..">Knowledge Works</a></li>
					<li><a href="#">Log in</a></li>
				</ul>
				<form class="navbar-form navbar-right">
					<input type="text" class="form-control" placeholder="Search...">
				</form>
			</div>
		</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			<!-- LEFT -->
			<div class="col-sm-3 col-md-2 sidebar">
				<ul class="nav nav-sidebar">
					<c:forEach items="${functions}" var="func">
						<c:if test="${currentFunc == func[0]}">
							<li class='active'><a href="${func[1]}"><c:out
										value="${func[0]}"></c:out><span class='sr-only'>(current)</span></a></li>
						</c:if>
						<c:if test="${currentFunc != func[0]}">
							<li><a href="${func[1]}"><c:out value="${func[0]}"></c:out><span
									class='sr-only'>(current)</span></a></li>
						</c:if>
					</c:forEach>
				</ul>
				<footer class="sidebar-footer">
					Copy Right <span class="fontArial">©</span>
					<script>
						document.write(new Date().getFullYear());
					</script>
					GDM Lab <br> Fudan University
				</footer>
			</div>
			<!-- MAIN -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<div class="page-header">
					<h1><%=curr_func[0]%></h1>
					<h5 class="text-muted"><%=curr_func[2]%></h5>
				</div>
				<!-- Search From -->
				<form action="search" method="get" class='form-inline'
					id='search_form' role='form'>
					<div class='input-group'>
						<input type='date' id='search_input' class='form-control'
							name='word'> <span class='input-group-btn'>
							<button class='btn btn-default' id='search_buttom' type='button'>Search</button>
						</span>
					</div>
					<img class="hidden loadingimg" src="../resources/image/loading.gif">
				</form>
				<br>
				<!-- End Search From -->
				<!--Information -->
				<div class="panel panel-default">
					<div class="panel-heading" data-toggle="collapse"
						data-parent="#accordion" data-target="#collapseInformation">
						<h4 class="panel-title">
							<span class="glyphicon glyphicon-search"></span><%=date%>
						</h4>
					</div>
					<div id="collapseInformation" class="panel-collapse collapse in">
						<div class="panel-body">
							<div class="table-responsive">
								<c:forEach items="${status}" var="statusItem">
									<c:if test="${statusItem[0]=='1'}">
										<p>
											<c:out value="${statusItem[1]}"></c:out>
										</p>
									</c:if>
									<c:if test="${statusItem[0]=='2'}">
										<p>
											<c:out value="${statusItem[1]}"></c:out>
										</p>
									</c:if>
								</c:forEach>
								
						<!--  	<table id="test" class="table table-striped table-bordered">
									<thead>
										<tr>
											<th>Name</th>
											<th>Position</th>
											<th>Office</th>
											<th>Age</th>
											<th>Start date</th>
											<th>Salary</th>
										</tr>
									</thead>	
									<tbody>
										<tr>
											<td>Tiger Nixon</td>
											<td>System Architect</td>
											<td>Edinburgh</td>
											<td>61</td>
											<td>2011/04/25</td>
											<td>$320,800</td>
										</tr>
										<tr>
											<td>Garrett Winters</td>
											<td>Accountant</td>
											<td>Tokyo</td>
											<td>63</td>
											<td>2011/07/25</td>
											<td>$170,750</td>
										</tr>
										<tr>
											<td>Ashton Cox</td>
											<td>Junior Technical Author</td>
											<td>San Francisco</td>
											<td>66</td>
											<td>2009/01/12</td>
											<td>$86,000</td>
										</tr>
									</tbody>
								</table>  
							-->
								<table class="table table-hover table-striped"  id="datatable">
									<thead>
										<tr>
											<th  class="sortable">Relation</th>
											<th>Instances</th>
											<th>News Link</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${data}" var="triple">
											<tr>
												<td class="col-xs-4">[<c:out value="${triple[1]}"></c:out>]
												</td>
												<td class="col-xs-4">( <a href="#"><c:out
															value="${triple[0]}"></c:out>,</a> <a href="#"><c:out
															value="${triple[2]}"></c:out></a> )
												</td>
												<td class="col-xs-4"><a href="${triple[3]}"
													target="_blank"><c:out value="${triple[3]}"></c:out></a></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	
	
	
	<script src="../resources/js/jquery.mobile.custom.min.js"></script>	
	<script src="../resources/js/cnell.js"></script>
	<script src="../resources/assets/js/ie-emulation-modes-warning.js"></script>
	<!-- <script src="assets/js/docs.min.js"></script> -->
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="../resources/assets/js/ie10-viewport-bug-workaround.js"></script>
		
	<script type="text/javascript">	
		$(document).ready(function() {
			var date = new Date();
			var yyyy =
	<%=yyyy%>
		;
			var mm =
	<%=mm%>
		;
			mm = mm - 1;
			var dd =
	<%=dd%>
		;
			date.setFullYear(yyyy, mm, dd);
			document.getElementById('search_input').valueAsDate = date;
		});
	</script>

	
</body>
</html>

