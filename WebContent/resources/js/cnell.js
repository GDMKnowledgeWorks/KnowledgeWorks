var WORDNUM = 20;

$("#search_buttom").click(function() {
	var time = $("#search_input").val();
	console.log(time);
	if (time == "") {
		$("#search_input").addClass("bg-danger");
		return;
	}
	$("form .loadingimg").removeClass("hidden");
	var split = time.split("-");
	var year = split[0].substring(2, 4);
	var month = split[1];
	var date = split[2];
	console.log(split);
	console.log(year);
	console.log(month);
	console.log(date);
	var dateStr = year + month + date;
	var href = "/KnowledgeWorks/cnell/relation?date=" + dateStr;
	location.href = href;
});

$("#search_input").keypress(function(e) {
	$("#search_input").removeClass("bg-danger");
	if (e.which == 13) {
		$("#search_buttom").click();
		return false;
	}
});

$(".panel-heading").on('tap', function() {
	// console.log("tap");
	// console.log($(this).next());
	$(this).next(".collapse").collapse();
});

function isEmpty(obj) {
	for ( var name in obj) {
		return false;
	}
	return true;
};
