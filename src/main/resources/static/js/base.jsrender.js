function refresh() {
    $.get("http://localhost:8080/rest/index", function(res) {
    	res = JSON.parse(res);
    	res.headers = JSON.parse(res.headers);
    	res.table = JSON.parse(res.table);
    	res.fillDayRow = JSON.parse(res.fillDayRow);
    	res.totalRow = JSON.parse(res.totalRow);
    	console.log(res);
    	var tmpl = $.templates("#tableTemplate");
    	var html = tmpl.render(res);
    	$('#main-div').html(html);
    });
}

$(function() {
	refresh();
});