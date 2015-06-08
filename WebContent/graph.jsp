<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String attr = request.getParameter("attr");
	String attrPath = "attr-graph/" + attr + "-jsonMap.txt";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=attr%></title>
<style>
.node {
	stroke: rgb(31, 119, 180);
	stroke-width: 1.5px;
}

.link {
	stroke: #999;
	stroke-opacity: .6;
}
</style>
</head>
<body>
	<script src="http://d3js.org/d3.v3.min.js"></script>
	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.6.2.min.js"></script>
	<script>
		var width = 960, height = 700;

		var color = d3.scale.category20();

		var force = d3.layout.force().charge(-120).linkDistance(40).size(
				[ width, height ]);

		var svg = d3.select("body").append("svg").attr("width", width).attr(
				"height", height);

		d3.json(
	"<%=attrPath%>"
		, function(error, graph) {
			force.nodes(graph.nodes).links(graph.links).start();

			var link = svg.selectAll(".link").data(graph.links).enter().append(
					"line").attr("class", "link").style("stroke-width",
					function(d) {
						if (d.value < 0.1) {
							return 0;
						} else {
							return d.value * 3;
						}

					});

			var node = svg.selectAll(".node").data(graph.nodes).enter().append(
					"circle").attr("class", "node").attr("r", function(d) {
				return d.value * 40 + 2;
			}).style("fill", function(d) {
				return color(d.group);
			}).call(force.drag);

			var tooltip = d3.select("body").append("div").style("position",
					"absolute").style("z-index", "10").style("visibility",
					"hidden").text("a simple tooltip");

			node.on("mouseover", function(d) {
				tooltip.text(d.name);
				return tooltip.style("visibility", "visible");
			}).on(
					"mousemove",
					function() {
						return tooltip.style("top", (event.pageY - 10) + "px")
								.style("left", (event.pageX + 10) + "px");
					}).on("mouseout", function() {
				return tooltip.style("visibility", "hidden");
			});

			force.on("tick", function() {
				link.attr("x1", function(d) {
					return d.source.x;
				}).attr("y1", function(d) {
					return d.source.y;
				}).attr("x2", function(d) {
					return d.target.x;
				}).attr("y2", function(d) {
					return d.target.y;
				});

				node.attr("cx", function(d) {
					return d.x;
				}).attr("cy", function(d) {
					return d.y;
				});
			});
		});
	</script>
</body>
</html>