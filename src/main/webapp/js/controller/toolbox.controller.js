$(document)
		.ready(
				function() {

					$('.popup')
							.on(
									'click',
									function() {

										// var popup =
										// document.getElementById('myPopup');
										// popup.classList.toggle('show');

										var cntr = 0, color = '#fff';
										var errormsgs = '<table style="width:600px" class=bordertable><td class=bordercell>Name</td><td class=bordercell>Value</td><td class=bordercell>Position</td><td class=bordercell>Error</td><td class=bordercell>Action</td></tr>';
										console.log(globals.errors[0]);
										$
												.each(
														globals.errors[0],
														function(index, err) {
															$
																	.each(
																			err,
																			function(index, err1) {

																				if (cntr % 2 == 0)
																					color = '#eee';
																				else
																					color = '#fff';

																				console.log(err1)

																				var errdetails = err1.split(" ");
																				errormsgs += '<tr style="background-color:'
																						+ color
																						+ '"><td class=bordercell id=errorfield0>'
																						+ errdetails[0]
																						+ '</td><td class=bordercell>'
																						+ errdetails[2]
																						+ '</td><td class=bordercell>'
																						+ errdetails[3]
																						+ '</td><td class=bordercell>'
																						+ errdetails[4]
																						+ '</td><td class=bordercell nowrap>'
																						+ ((errdetails[4] == 'added') ? '<button class=small value="ignore" style="width:100px" onclick=update("add")>add column</button>&nbsp;'
																								: '<button class=small value="fix" style="width:100px" onclick=update("remove")>remove column</button>&nbsp;')
																						+ '</td></tr>';
																				cntr++;
																			});

														});
										errormsgs += '<tr><td class=small colspan=5 aign=right><br><br>Table Name:&nbsp;<input id=newtablename>&nbsp;'
												+ '<button class=small value="createnew" style="width:100px" onclick=update("createnew")>create new table</button></td></table>';

										$("#errordialog").dialog({
											autoOpen : false,
											modal : true,
											height : 500,
											width : 700,
											dialogClass : 'ui-dialog-shadow',
											resizable : true,
											create : function(event, ui) {
												$(".ui-widget-header").hide();
											},
											buttons : [ {
												text : "OK",
												click : function(e) {
													$("#errordialog").dialog('close');
												},
												iconCls : 'icon-save'
											} ]
										});

										$('#errormsgs').html(errormsgs);
										$('#errordialog').dialog('open');

									});

					$('.icon-zoom-in').on('click', function() {
						renderer.setZoom(renderer.getZoom() + .1);
					});

					$('.icon-zoom-out').on('click', function() {
						renderer.setZoom(renderer.getZoom() - .1);
					});

					$('.icon-retweet').on('click', function() {
						renderer.setZoom(1);
						// renderer.zoomToFitIfNecessary();
					});

					$('.icon-refresh').on('click', function() {
						$("#treeDemo").hide();
						$('#treeloading').show();
						getAllConnections();
					});
				});

function update(op) {
	$.ajax({
		url : '/updateSchema/' + op + '/' + $("#errorfield0").text(),
		success : function(result) {
			alertify.success("Table " + "" + " altered<strong>" + alertify.labels.ok + "</strong>: ");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("Status: " + textStatus);
			console.log("Error: " + errorThrown);
		}
	});
}

var foundnodes = [];
function recurse(nodes, match) {
	var matched = false;
	$.each(nodes, function(index, node) {
		if (node.name == match) {
			matched = true;
			foundnodes.push(node);
			return false;
		}
		if (node.children && !matched) {
			recurse(node.children, match)
		}
	});

	return foundnodes;
}

function diff(obj1, obj2) {
	var matches = [];
	var added;
	var firstArray = [], secondArray = [];
	if (obj1.length > obj2.length) {
		firstArray = obj1;
		secondArray = obj2;
		added = true;
	} else {
		firstArray = obj2;
		secondArray = obj1;
		added = false;
	}

	var diffs = {};
	var obj1arr = [], obj2arr = [];
	$.each(firstArray, function(index1, obj1item) {
		obj1arr.push({
			name : obj1item.name,
			value : obj1item.value
		});
	});
	$.each(secondArray, function(index2, obj2item) {
		obj2arr.push({
			name : obj2item.name,
			value : obj2item.value
		});
	});

	$.each(firstArray, function(index1, obj1item) {
		var matched = false, idx;
		$.each(secondArray, function(index2, obj2item) {
			if (obj1item.name == obj2item.name && obj1item.value == obj2item.value) {

				matched = true;
				idx = index2;
				return false;
			}
		});

		if (!matched) {
			obj1item.added = added;
			obj1item.index = index1;
			matches.push(obj1item);
		}
	});

	$.each(secondArray, function(index1, obj1item) {
		var matched = false, idx;
		$.each(firstArray, function(index2, obj2item) {
			if (obj1item.name == obj2item.name && obj1item.value == obj2item.value) {
				matched = true;
				idx = index2;
				return false;
			}
		});

		if (!matched) {
			obj1item.added = true;
			obj1item.index = index1;
			matches.push(obj1item);
		}
	});

	return matches;
}