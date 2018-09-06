const baseUrl = "http://localhost:8080/";

function addTicketBtnOnClick() {
	var div = $('<div></div>');
	
	$('<input>').attr({
	    id: 'add-ticket',
	    class: 'add-input form-control',
	    placeholder: 'ticket'
	}).appendTo(div);
	
	$('<input>').attr({
	    id: 'add-date',
	    type: 'date',
	    class: 'add-input form-control'
	}).appendTo(div);
	
	$('<input>').attr({
	    id: 'add-hour',
	    type: 'text',
	    class: 'add-input form-control',
		placeholder: 'hours'
	}).appendTo(div);
	
	swal({
	    text: "Add worklog for a new ticket\nFNBAE-401 for non-imputable.",
	    content: div[0],
	    buttons: true
	})
	.then((value) => {
		if (value === null) {
			throw null;
		}
		var ticket = $("#add-ticket").val();
		var date = $("#add-date").val();
		var value = $("#add-hour").val();
		
		console.log("Adding worklog for new ticket " + ticket + " at date " + date + " for " + value + " hours.");

		// validate ticket
		var splited = ticket.split("-");
		if (splited.length != 2 || isNaN(splited[1])) {
            swal("Add worklog", "ticket format incorrect", "error");
            throw null;
		}
		
		// validate date
		if (date == "") {
            swal("Add worklog", "Date empty", "error");
            throw null;
		}
		date = new Date(date);
		var oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
		var now = new Date();
		var diffDays = Math.round(Math.abs((now.getTime() - date.getTime())/(oneDay)));
		
		if (diffDays > 30 || diffDays < -30) {
            swal("Add worklog", "input date has 30 days diff than today", "error");
            throw null;
		}
		date = $("#add-date").val(); // back to the string

		// validate hours value
        if (value == "" || isNaN(value)) {
            swal("Add worklog", "input must be an integer or a float!", "error");
            throw null;
        }

        value = Number(value);

        if (value > 8 || value < 0) {
            swal("Add worklog", "input must < 8 or > 0", "error");
            throw null;
        }
        
        // ajax
        const url = baseUrl + "rest/add";

        return $.ajax({
            type: "POST",
            url: url,
            data: {
                "ticket": ticket,
                "date": date,
                "value": value
            }
        });
	})
    .then(results => {
        console.log(results);
        return JSON.parse(results);
    })
    .then(json => {
        swal("Add worklog", "Add success.", "success");
        refresh();
    })
    .catch(err => {
        if (err) {
            swal("Add worklog", "The AJAX request failed!", "error");
        } else {
            swal.stopLoading();
            swal.close();
        }
    });

}

function fillDayBtnOnclick(btn) {
    btn = $(btn); // transform to jquery object
    var date = btn.attr("data-date");
    var total = btn.attr("data-total");
    total = total.substring(0, total.length - 1);
    console.log("Filling day " + date + ", old total " + total);

    const url = baseUrl + "rest/fillday";
    btn.html('<i class="fa fa-spinner fa-spin"></i>');
    $.ajax({
        type: "POST",
        url: url,
        data: {
            "date": date,
            "total": total
        },
        success: function(res) {
            console.log("Fillday " + date + " total " + total + " : " + res);
            res = JSON.parse(res);

            if (res["data"]) {
                btn.prop("disabled", true);
                btn.html("OK");
                // swal("Fillday", "Fillday success.", "success");
            } else {
                swal("Fillday", "Fillday response error, see log.", "error");
                btn.html('Fill');
            }
        },
        error: function() {
            swal("Fillday", "Fillday request error", "error");
        }
    });
}

function addTimeToTicketOfDate(btn) {
    btn = $(btn); // transform to jquery object
    var date = btn.attr("data-date");
    var ticket = btn.attr("data-ticket");
    console.log("Adding time to ticket " + ticket + " of date " + date);

    const url = baseUrl + "rest/add";
    swal({
            text: "Update worklog for ticket " + ticket + " of " + date,
            content: "input",
            buttons: true
        })
        .then(value => {
        	if (value == null) {
        		throw null;
        	}
        	
            if (value == "" || isNaN(value)) {
                swal("Add worklog", "input must be an integer or a float!", "error");
                throw null;
            }

            value = Number(value);

            if (value > 8 || value < 0) {
                swal("Add worklog", "input must < 8 or > 0", "error");
                throw null;
            }

            return $.ajax({
                type: "POST",
                url: url,
                data: {
                    "ticket": ticket,
                    "date": date,
                    "value": value
                }
            });
        })
        .then(results => {
            console.log(results);
            return JSON.parse(results);
        })
        .then(json => {
            swal("Add worklog", "Add success.", "success");
            refresh();
        })
        .catch(err => {
            if (err) {
                swal("Add worklog", "The AJAX request failed!", "error");
            } else {
                swal.stopLoading();
                swal.close();
            }
        });
}

function updateTimeToTicketOfDate(a) {
    a = $(a); // transform to jquery object
    var date = a.attr("data-date");
    var ticket = a.attr("data-ticket");
    var id = a.attr("data-worklogid");
    console.log("Updating worklog id " + id + " of ticket " + ticket + " of date " + date);

    const url = baseUrl + "rest/update";
    swal({
            text: "Update worklog for ticket " + ticket + " of " + date,
            content: "input",
            buttons: true
        })
        .then(value => {
        	if (value == null) {
        		throw null;
        	}
        	
            if (value == "" || isNaN(value)) {
                swal("Update worklog", "input must be an integer or a float!", "error");
                throw null;
            }

            value = Number(value);

            if (value > 8 || value < 0) {
                swal("Update worklog", "input must < 8 or > 0", "error");
                throw null;
            }

            return $.ajax({
                type: "POST",
                url: url,
                data: {
                    "ticket": ticket,
                    "id": id,
                    "value": value
                }
            });
        })
        .then(results => {
            console.log(results);
            return JSON.parse(results);
        })
        .then(json => {
            swal("Update worklog", "Update success.", "success");
            refresh();
        })
        .catch(err => {
            if (err) {
                swal("Update worklog", "The AJAX request failed!", "error");
            } else {
                swal.stopLoading();
                swal.close();
            }
        });
}

function refresh() {
    $.get("http://localhost:8080/", function(res) {
        document.open();
        document.write(res);
        document.close();
    });
}