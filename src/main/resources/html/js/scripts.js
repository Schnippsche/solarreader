/* Solarreader javascript file */
'use strict';

window.addEventListener('DOMContentLoaded', () => {
    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
            document.body.classList.toggle('sb-sidenav-toggled');
        }
        sidebarToggle.addEventListener('click', (event) => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', String(document.body.classList.contains('sb-sidenav-toggled')));
        });
    }
});

function setRequired(selector, status) {
    $(selector).prop('required', status);
}

(function () {
    'use strict'

    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    let forms = document.querySelectorAll('.needs-validation')

    // Loop over them and prevent submission
    Array.prototype.slice.call(forms)
        .forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }

                form.classList.add('was-validated')
            }, false)
        })
})()


/* load log file with amount of lines and put it in gui */
function showLogFile() {
    let count = localStorage.getItem('loglinecount');
    if (count === null)
        count = "100";
    $.ajax('ajax?action=setlogsize&linecount=' + count, function () {
    }).done(function (data) {
        $('#id_logentries').val(count);
        const elem = $('#id_log_content')
        if (data.success) {
            elem.text(data.message);
        } else {
            elem.text("Error:" + data.message);
        }
    })
}

function showDiskSpace() {
    const json_url = "ajax?action=diskspacechart";
    const ctx = document.getElementById('canvasdiskspace').getContext('2d');
    const diskChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: []
            ,
            datasets: []
        },
        options: {
            plugins: {
                title: {
                    display: true,
                },
            },
            responsive: true,
            scales: {
                x: {
                    stacked: true,
                    display: true,
                }
                , y: {
                    stacked: true,
                    display: true,
                    title: {
                        display: true,
                        text: "Gigabyte"
                    }
                }
            }
        }
    });
    ajaxChart(diskChart, json_url);
}

function showMemorySpace() {
    const json_url = "ajax?action=memoryspacechart";
    const ctx = document.getElementById('canvasmemoryspace').getContext('2d');
    const memoryChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: [],
            datasets: []
        },
        options: {
            plugins: {
                title: {
                    display: true,
                },
            },
            responsive: true,
            rotation: -90,
            circumference: 180,
        }
    });
    ajaxChart(memoryChart, json_url);
}

function ajaxChart(chart, url, data) {
    let data2 = data || {};
    $.getJSON(url, data2).done(function (response) {
        chart.data.labels = response.labels;
        chart.data.datasets = response.datasets;
        chart.options.plugins.title.text = response.titletext;
        chart.update(); // finally update our chart
    });
}

function handleStandardForm(formSelector, action, alertSelector) {
    $(formSelector).on('submit', function (event) {
        if (!this.checkValidity()) {
            return;
        }
        event.preventDefault();
        const form = this, $form = $(form);
        $('#loader').removeClass('d-none');
        $.post('ajax?action=' + action, $form.serialize(), function () {
        }).done(function (data) {
            $('#loader').addClass('d-none');
            if (data.success) {
                $form.off('submit').trigger('submit');
            } else {
                const elem = $(alertSelector);
                elem.text(data.message);
                elem.removeClass('d-none');
                $('.modal-body').scrollTop(1000);
            }
        })
    });

}

jQuery(function () {
    const lang = $('html').attr('lang');
    const langUrl = '/lang/table_' + lang + '.json';
    const statusTable = $('#id_status_table').DataTable({
        responsive: false,
        colReorder: true,
        autoWidth: true,
        stateSave: true,
        deferRender: false,
        orderClasses: false,
        language: {
            url: langUrl,
        },
        ajax: {
            url: 'ajax?action=getstatus', dataSrc: ''
        }
        ,
        order: [],
        columns: [
            {
                data: {}, mRender: function (data) {
                    return '<img src="' + data.icon + '" width="16" />&nbsp;' + data.element;
                }
            },
            {
                data: {}, mRender: function (data) {
                    return '<span class="badge ' + data.statusclass + '">' + data.statustext + '</span>';
                }
            },
            {
                data: 'activity'
            },
            {
                data: 'info'
            },
        ]
    });

    /* reload the status table and the log every 10 seconds */
    setInterval(function () {
        statusTable.ajax.reload(null, false); // user paging is not reset on reload
        showLogFile();
    }, 10000);


    $('#devicefieldtable').DataTable({
        responsive: false, colReorder: true, autoWidth: true, deferRender: false, orderClasses: false, language: {
            url: '/lang/table_de_DE.txt',
        }, ajax: {
            url: '/api/devicefields/', dataSrc: ''
        }, order: [], columns: [{
            orderable: false, data: 'id', 'mRender': function (id) {
                return '<div class="btn-group" role="group" data-index-number="' + id + '" aria-label="Zeilenaktionen">' + '<button type="button" class="btndelete btn btn-outline-danger btn-sm bi bi-trash"></button>' + '<button type="button" class="btnedit btn btn-outline-primary btn-sm bi bi-pencil"></button>' + '</div>';
            }
        }, {orderable: true, data: 'name'}, {orderable: true, data: 'type', defaultContent: ''}, {
            orderable: true,
            data: 'register',
            defaultContent: ''
        }, {orderable: true, data: 'offset', defaultContent: ''}, {
            orderable: true,
            data: 'count',
            defaultContent: ''
        }, {orderable: true, data: 'factor', defaultContent: ''}, {
            orderable: true,
            data: 'unit',
            defaultContent: ''
        }, {orderable: true, data: 'note', defaultContent: ''}]
    });


    const tbody = $('#devicefieldtable tbody');
    tbody.on('click', '.btnedit', function () {
        console.log('edit id:' + $(this).parent().attr('data-index-number'));
    });

    tbody.on('click', '.btndelete', function () {
        console.log('delete id:' + $(this).parent().attr('data-index-number'));
    });

    /* handler for step forward button (validate, show loader and submit form*/
    /* submit handler for device setup */
    $('#id_frmdevicesetup').on('submit', function (event) {
        if (!this.checkValidity()) {
            return;
        }
        event.preventDefault();
        const form = this, $form = $(form);
        const btnForward = $('#id_device_btn_forward');
        const page = $('#id_page').val();
        btnForward.prop('disabled', true);
        $('#loader').removeClass('d-none');
        $.post('ajax?action=checkdevicestep&page=' + page, $form.serialize(), function () {
        }).done(function (data) {
            $('#loader').addClass('d-none');
            btnForward.prop('disabled', false);
            if (data.success) {
                $form.off('submit').trigger('submit');
            } else {
                const elem = $('#id_devicealert');
                elem.text(data.message);
                elem.removeClass('d-none');
                $('.modal-body').scrollTop(1000);
            }
        })
    })

    /* handler for step back button */
    $('#id_device_btn_back').on('click', function () {
        const page = $(this).val();
        $('<form method="post"><input name="step" value="showdevice"><input name="page" value="' + page + '"></form>').appendTo('body').submit().remove();
    })

    /* handler for activate/deactivate hf2211 inputs */
    $('#id_devicesetup_hf2211_enabled').on('click', function () {
        const hf2211row = $('#id_hf2211_row')
        if (this.checked) {
            hf2211row.removeClass('d-none');
            setRequired('#id_devicesetup_hf2211_ip', true);
            setRequired('#id_devicesetup_hf2211_port', true);
            $('#id_divusb').addClass('d-none');
        } else {
            hf2211row.addClass('d-none');
            $('#id_frmdevicesetup').removeClass('was-validated');
            $('#id_divusb').removeClass('d-none');
            setRequired('#id_devicesetup_hf2211_ip', false);
            setRequired('#id_devicesetup_hf2211_port', false);
        }
    })

    /* reload usb device list */
    $('#id_btn_serialreload').on('click', function () {
        // reload ports
        $(this).prop('disable', true);
        $.ajax('ajax?action=reloadcomports', function () {
        }).done(function (data) {
            if (data.success) {
                let $el = $('#id_devicesetup_com_port');
                $el.empty(); // remove old options
                $el.append(data.message); // replace
            }
            $(this).prop('disable', false);
        });
    });

    handleStandardForm('#id_frmdatabasesetup', 'checkdatabase', '#id_databasealert');

    handleStandardForm('#id_frmmqttsetup', 'checkmqtt', '#id_mqttalert');

    handleStandardForm('#id_frmopenweathersetup', 'checkopenweather', '#id_openweatheralert');

    handleStandardForm('#id_frmsolarprognosesetup', 'checksolarprognose', '#id_solarprognosealert');

    handleStandardForm('#id_frmawattarsetup', 'checkawattar', '#id_awattaralert');

    $('#id_logentries').on('change', function () {
        const count = $('#id_logentries').val();
        localStorage.setItem('loglinecount', count);
        showLogFile();
    });

    $('#id_btn_log_refresh').on('click', function () {
        showLogFile();
    });
    $('#id_btn_log_download').on('click', function () {
        window.location = "ajax?action=downloadlog";
    });

    $('#id_btndatabasedelete').on('click', function () {
        $('<form method="post"><input name="step" value="confirmdeletedatabase"></form>').appendTo('body').submit().remove();
    });

    $('#id_btndevicedelete').on('click', function () {
        $('<form method="post"><input name="step" value="confirmdeletedevice"></form>').appendTo('body').submit().remove();
    });

    $('#id_btnmqttdelete').on('click', function () {
        $('<form method="post"><input name="step" value="confirmdeletemqtt"></form>').appendTo('body').submit().remove();
    });
    /*  update gui when sourcetype was changed */
    $('#id_sourcetype').on('change', function () {
        $('#id_sourcetypes').children('div.form-floating').addClass("d-none");
        let id = ("id_" + $(this).val()).toLowerCase();
        $('#' + id).removeClass('d-none');
    });

    const collapseElements = $('.collapse');
    collapseElements.on("shown.bs.collapse", function () {
        localStorage.setItem("coll_" + this.id, "true");
    });

    collapseElements.on("hidden.bs.collapse", function () {
        localStorage.removeItem("coll_" + this.id);
    });

    collapseElements.each(function () {
        if (localStorage.getItem("coll_" + this.id) === "true") {
            $(this).collapse("show");
        } else {
            $(this).collapse("hide");
        }
    });

    showLogFile();
    showDiskSpace();
    showMemorySpace();

    /* must show modal dialogs? */
    const modal = $('div.showmodal');
    if (modal.length > 0)
        new bootstrap.Modal(modal).show();
})
;

