const baseUrl = "http://localhost:8080/";

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
            res = JSON.parse(res);
            console.log("Fillday " + date + " total " + total + " : " + res);

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
            button: {
                text: "Add",
                closeModal: false,
            },
        })
        .then(value => {
            if (isNaN(value)) {
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
            button: {
                text: "Update",
                closeModal: false,
            },
        })
        .then(value => {
            if (isNaN(value)) {
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