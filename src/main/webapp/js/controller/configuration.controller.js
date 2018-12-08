$(document).ready(

/** CONFIGURATION TABLES * */
function() {

	var configurationtables = [];

	var children = [];

	var generaltable = $("#general").on('click', function(e) {
		updateConfigurationTables(e);
		$("#loadschemabutton").hide();
	});
	configurationtables.push(generaltable);

	var optionstable = $("#options").on('click', function(e) {
		updateConfigurationTables(e);
		$("#loadschemabutton").hide();
	});
	configurationtables.push(optionstable);

	var resourcestable = $("#resources").on('click', function(e) {
		updateConfigurationTables(e);
		$("#loadschemabutton").hide();
	});
	configurationtables.push(resourcestable);

	var filtertable = $("#filter").on('click', function(e) {
		updateConfigurationTables(e);
		$("#loadschemabutton").hide();
	});
	configurationtables.push(filtertable);

	var schematable = $("#schema").on('click', function(e) {
		updateConfigurationTables(e);
		$("#loadschemabutton").show();
	});
	configurationtables.push(schematable);

	var updateConfigurationTables = function(e) {
		$.each(configurationtables, function(index, configurationtable) {
			if (e.target.id == configurationtable[0].id) {
				$('#' + configurationtable[0].id + 'table').show();
				$('#' + configurationtable[0].id + 'table_wrapper').show();
			} else {
				$('#' + configurationtable[0].id + 'table').hide();
				$('#' + configurationtable[0].id + 'table_wrapper').hide();
			}
		});
	};

	var updateDataTableSelectAllCtrl = function(table) {
		var $table = table.table().node();
		var $chkbox_all = $('tbody input[type="checkbox"]', $table);
		var $chkbox_checked = $('tbody input[type="checkbox"]:checked', $table);
		var chkbox_select_all = $('thead input[name="select_all"]', $table).get(0);

		// If none of the checkboxes are checked
		if ($chkbox_checked.length === 0) {
			chkbox_select_all.checked = false;
			if ('indeterminate' in chkbox_select_all) {
				chkbox_select_all.indeterminate = false;
			}

			// If all of the checkboxes are checked
		} else if ($chkbox_checked.length === $chkbox_all.length) {
			chkbox_select_all.checked = true;
			if ('indeterminate' in chkbox_select_all) {
				chkbox_select_all.indeterminate = false;
			}

			// If some of the checkboxes are checked
		} else {
			chkbox_select_all.checked = true;
			if ('indeterminate' in chkbox_select_all) {
				chkbox_select_all.indeterminate = true;
			}
		}
	}

	/** CONFIGURATION DIALOG* */
	$("#dialog").dialog({
		autoOpen : false,
		draggable : true,
		dialogClass : 'ui-dialog-shadow',
		height : 'auto',
		width : 'auto',
		modal : true,
		create : function(event, ui) {
			$(".ui-widget-header").hide();
		},
		buttons : [ {
			text : "LOAD SCHEMA",
			id : 'loadschemabutton',
			click : function(e) {
				loadSchema(e);
			},
			iconCls : 'icon-save'
		}, {
			text : "OK",
			click : function(e) {
				$('#dialog').dialog('close');
				var options = $(".optionclass");
				var updatenode = getNode(globals.currentnode[0].uid);
				updatenode[0].filters = $("#filters")[0].value;
				$.each(options, function(idx, option) {
					updatenode[0].options[idx].value = option.value;
				});

				console.log(getNodes())
			},
			iconCls : 'icon-save'
		} ]
	});

	$("#editschemadialog").dialog({
		autoOpen : false,
		draggable : true,
		dialogClass : 'ui-dialog-shadow',
		height : 'auto',
		width : 'auto',
		modal : true,
		create : function(event, ui) {
			$(".ui-widget-header").hide();
		},
		buttons : [ {
			text : "Cancel",
			click : function(e) {
				$('#editschemadialog').dialog('close');
			},
			iconCls : 'icon-save'
		}, {
			text : "OK",
			click : function(e) {
				$('#editschemadialog').dialog('close');
				//******* SAVE UPDATED SCHEMA
			},
			iconCls : 'icon-save'
		} ]
	});

	/** GENERAL * */
	$('#generaltable').DataTable({
		data : [],
		"paging" : false,
		"ordering" : false,
		"autoWidth" : false,
		"fixedHeader" : {
			"header" : false,
			"footer" : false
		},
		"columns" : [ {
			"width" : "250px",
			"targets" : 0
		}, {
			"width" : "250px",
			"targets" : 1
		} ]
	});

	$('#generaltable thead').css("display", "none");
	$('#generaltable thead').css("border-bottom", "0px");
	$("#generaltable_previous").hide();
	$("#generaltable_next").hide();

	/** OPTIONS * */
	var optiontable = $('#optionstable').DataTable({
		data : [],
		"paging" : false,
		"ordering" : false,
		"autoWidth" : false,
		"fixedHeader" : {
			"header" : false,
			"footer" : false
		},
		"columns" : [ {
			"width" : "250px",
			"targets" : 0
		}, {
			"width" : "250px",
			"targets" : 1
		} ]
	});

	$('#optionstable thead').css("display", "none");
	$('#optionstable thead').css("border-bottom", "0px");
	$("#optionstable_previous").hide();
	$("#optionstable_next").hide();

	$(".paginate_button").hide();
	$(".dataTables_filter").hide();
	$(".dataTables_info").hide();
	$(".dataTables_length").hide();

	/** SCHEMA * */
	var schematable = $('#schematable').DataTable({
		data : [],
		'columnDefs' : [ {
			'targets' : 0,
			'searchable' : false,
			'orderable' : false,
			'width' : '30px',
			'className' : 'dt-body-center',
			'render' : function(data, type, full, meta) {
				return '<input type="checkbox" checked="false">';
			}
		}, {
			"width" : "190px",
			"targets" : 1
		}, {
			"width" : "190px",
			"targets" : 2
		}, {
			"width" : "190px",
			"targets" : 3
		}, {
			"width" : "190px",
			"targets" : 4
		} ],
		columns : [ {
			title : "Selected"
		}, {
			title : "Name"
		}, {
			title : "Function"
		}, {
			title : "Type"
		}, {
			title : "Group",
			visible : false
		} ],
		"drawCallback" : function(settings) {

			var api = this.api();
			var rows = api.rows({
				page : 'current'
			}).nodes();
			var last = null;

			// if (rows[1] && rows[1].children[2].innerHTML != '') {

			$('#schematable tr:eq(0) th:eq(2)').text("Parent");
			api.column(4, {
				page : 'current'
			}).data().each(function(group, i) {
				if (last !== group) {
					$(rows).eq(i).before('<tr class="group" style="cursor:pointer"><td colspan="5" class="schemagroup" id="' + group + '">' + group + '</td></tr>');
					last = group;
				}
			});
			// }

			// if (globals.currentnode) {
			// $.each(getNode(globals.currentnode[0].uid)[0].schema,
			// function(index, schemaentry) {
			// if (children.indexOf(schemaentry.name) != -1) {
			// console.log(schemaentry);
			// }
			// });
			// }
			var table = $('#schematable').DataTable();
			var data = table.rows().nodes();
			$.each(data, function(index, value) {
				if (children.indexOf($(value)[0].cells[1].textContent) != -1) {
					console.log($(value)[0].cells[1].textContent)
					$(this).find('input').prop('checked', true)
				} else {
					$(this).find('input').prop('checked', false)
				}
			});
		}
	});

	$('#schematable').on('click', 'tbody td.schemagroup', function(e) {

		var cells = schematable.cells().nodes();
		var checkboxes = $(cells).find(':checkbox');
		var checked = checkboxes[0].checked;
		$(cells).find(':checkbox').prop('checked', checked ? false : true);

		$.each(getNode(globals.currentnode[0].uid)[0].schema, function(index, schemaentry) {
			if (e.currentTarget.textContent == schemaentry.group) {

				if (children.indexOf(schemaentry.name) != -1) {
					// console.log(schemaentry.name + " already checked")
					children.splice(children.indexOf(schemaentry.name), 1);
					schemaentry.checked = false;
				} else {
					children.push(schemaentry.name)
					schemaentry.checked = true;
				}
			}
		});

		var table = $('#schematable').DataTable();
		var allData = table.rows().data();

		$(cells).find(':checkbox').prop('checked', false);

		var data = table.rows().nodes();
		$.each(data, function(index, value) {

			if (children.indexOf($(value)[0].cells[1].textContent) != -1) {
				// console.log($(value)[0].cells[1].textContent)
				$(this).find('input').prop('checked', true)
			}
		});

	});

	$('#schematable thead').on('click', 'th', function() {
		var index = schematable.column(this).index();
		if (index == 0) {
			var cells = schematable.cells().nodes();
			var checkboxes = $(cells).find(':checkbox');
			var checked = checkboxes[0].checked;
			$(cells).find(':checkbox').prop('checked', checked ? false : true);
			$.each(getNode(globals.currentnode[0].uid)[0].schema, function(index, schemaentry) {
				schemaentry.checked = checked ? false : true;
			});
		}
	});

	$('#schematable').on('click', 'tbody td', function(e) {
		var index = schematable.column(this).index();
		var rowindex = schematable.row(this).index();
		// if (index == 0) {

		var cells = schematable.cells().nodes();
		var checkboxes = $(cells).find(':checkbox');
		var checked = checkboxes[rowindex].checked;

		var updatenode = getNode(globals.currentnode[0].uid);

		var checked = updatenode[0].schema[rowindex].checked;

		console.log(checked)
		if (checked)
			updatenode[0].schema[rowindex].checked = false
		else
			updatenode[0].schema[rowindex].checked = true
	});

	$('#schematable_length').hide();
	$('#schematable').hide();
	$('#schematable_wrapper').hide();

	/** RESOURCES * */
	var resourcestable = $('#resourcestable').DataTable({
		data : [],
		'columnDefs' : [ {
			'targets' : 0,
			'searchable' : false,
			'orderable' : false,
			'width' : '30px',
			'className' : 'dt-body-center',
			'render' : function(data, type, full, meta) {
				return '<input type="checkbox" checked="true">';
			}
		}, {
			"width" : "190px",
			"targets" : 1
		} ],
		columns : [ {
			title : "Selected"
		}, {
			title : "Name"
		} ]
	});

	$('#resourcestable thead').on('click', 'th', function() {
		var index = resourcestable.column(this).index();
		if (index == 0) {
			var cells = resourcestable.cells().nodes();
			var checkboxes = $(cells).find(':checkbox');
			var checked = checkboxes[0].checked;
			$(cells).find(':checkbox').prop('checked', checked ? false : true);

			$.each(getNode(globals.currentnode[0].uid)[0].children, function(index, resourceentry) {
				resourceentry.checked = checked ? false : true;
			});
		}
	});

	$('#resourcestable').on('click', 'tbody td', function(e) {
		var index = resourcestable.column(this).index();
		var rowindex = resourcestable.row(this).index();
		if (index == 0) {
			var cells = resourcestable.cells().nodes();
			var checkboxes = $(cells).find(':checkbox');
			var checked = checkboxes[rowindex].checked;
			var updatenode = getNode(globals.currentnode[0].uid);
			updatenode[0].children[rowindex].checked = checked;
			console.log(getNodes())
		}
	});

	$('#resourcestable_length').hide();
	$('#resourcestable').hide();
	$('#resourcestable_wrapper').hide();

	$('#filtertable').hide();
});

function loadSchema() {

	$("#schemareaderdialog").dialog({
		id : 'schemaDialog',
		height : 375,
		width : 400,
		create : function(event, ui) {
			$(".ui-widget-header").hide();
		},
		buttons : [ {
			text : "Cancel",
			click : function(e) {
				$("#schemareaderdialog").dialog('close')
			}
		}, {
			text : "OK",
			click : function(e) {

				console.log('start load schema');

				var schemadata = [];
				var schematype = $("#schematype").val()
				var schemapath = $('.text')[0].textContent;

				console.log(globals.currentnode[0].parent + globals.currentnode[0].name)

				console.log(schematype);
				console.log(schemapath)

				schemadata.push(schemapath);
				schemadata.push(schematype);
				schemadata.push(document.getElementById("file-input").files[0].name);
				schemadata.push(globals.currentnode[0].parent.replace(/\\/g, "/") + globals.currentnode[0].name);

				$(this).closest('.ui-dialog-content').dialog('close');

				$.ajax({
					url : "/getSchema/" + "name",
					type : "POST",
					data : '{"id" : "' + schemadata.join(",") + '"}',
					contentType : 'application/json',
					success : function(result) {
						var schemaresult = JSON.parse(result);
						var schemaconfigs = [];
						$.each(schemaresult, function(index, schema) {
							var schemaconfig = [];
							// schemaconfig.push(schema.checked);
							schemaconfig.push(false);
							schemaconfig.push(schema.name);
							// schemaconfig.push(schema.udfname ? schema.udfname
							// : 'none');
							schemaconfig.push(schema.type);
							schemaconfig.push(schema.value);
							schemaconfig.push(schema.group);
							schemaconfigs.push(schemaconfig);
						});

						$('#schematable').DataTable().clear().rows.add(schemaconfigs).draw();
						$.each(schemaresult, function(index, schema) {
							// schema.checked = true;
						});
						globals.currentnode[0].schema = schemaresult;

						console.log('end load schema');

					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("Status: " + textStatus);
						console.log("Error: " + errorThrown);
					}
				});
			}
		} ]
	});

}