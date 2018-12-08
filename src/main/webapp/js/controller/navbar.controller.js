$(document).ready(

		function() {

			var jobpoll;

			$("#currentdate").html(new Date());

			$("#acquiremenu").click(
					function() {
						$('.breadcrumbs').html(workflowmenuitems);

						$("#treedisplay").show();
						$("#treedisplay1").hide();
						$("#canvas").show();
						$("#toolbox").show();
						$("#canvas1").hide();
						
						
						$("#canvas2").hide();
						$("#canvas3").hide();

						$("#draggable").show()

						$('#viewmodel').click(function() {

							console.log('start view model')

							$("#generalloadingtext").html('&nbsp;loading model.. please wait..')
							$("#generalloading").show()

							setTimeout(function() {

								$("#modeldialogitem").html(globals.workflowname);
								$("#modeldialog").dialog({
									autoOpen : false,
									modal : true,
									height : 600,
									width : 600,
									dialogClass : 'ui-dialog-shadow',
									resizable : true,
									create : function(event, ui) {
										$(".ui-widget-header").hide();
									},
									buttons : [ {
										text : "OK",
										click : function(e) {
											$("#modeldialog").dialog('close');
										},
										iconCls : 'icon-save'
									} ]
								});
								$('#modeldialog').dialog('open');

								document.getElementById("modeljson").innerHTML = syntaxHighlight(prepareModel('123'), undefined, 2);

								console.log('end view model')

								$("#generalloading").hide()

							}, 1000);

						});

						$('#viewnodes').click(function() {
							$("#modeldialogitem").html(globals.workflowname);
							$("#modeldialog").dialog({
								autoOpen : false,
								modal : true,
								height : 600,
								width : 600,
								dialogClass : 'ui-dialog-shadow',
								resizable : true,
								create : function(event, ui) {
									$(".ui-widget-header").hide();
								},
								buttons : [ {
									text : "OK",
									click : function(e) {
										$("#modeldialog").dialog('close');
									},
									iconCls : 'icon-save'
								} ]
							});
							$('#modeldialog').dialog('open');

							var displaynode = {}, displaynodes = [];
							$.each(getNodes(), function(index, node) {
								console.log(node[0].uid);
								displaynode[node[0].uid] = node[0].id;
								displaynode[node[0].uid] = node[0].text;
								displaynodes.push(displaynode);
							});

							console.log(displaynodes);

							document.getElementById("modeljson").innerHTML = syntaxHighlight(getNodes(), undefined, 2);
						});

						$('#viewconnection').click(function() {
							$("#modeldialogitem").html(globals.workflowname);
							$("#modeldialog").dialog({
								autoOpen : false,
								modal : true,
								height : 600,
								width : 600,
								dialogClass : 'ui-dialog-shadow',
								resizable : true,
								create : function(event, ui) {
									$(".ui-widget-header").hide();
								},
								buttons : [ {
									text : "OK",
									click : function(e) {
										$("#modeldialog").dialog('close');
									},
									iconCls : 'icon-save'
								} ]
							});
							$('#modeldialog').dialog('open');

							var connections = [];
							$.each(instance.getConnections(), function(index, connection) {
								var conn = {
									id : connection.id,
									source : {
										id : connection.source.id,
										name : connection.source.textContent.substring(0, connection.source.textContent.indexOf(" ")).trim()
									},
									target : {
										id : connection.target.id,
										name : connection.target.textContent.substring(0, connection.target.textContent.indexOf(" ")).trim()
									}
								}
								connections.push(conn);
							});
							document.getElementById("modeljson").innerHTML = syntaxHighlight(connections, undefined, 2);
						});

						$('.schedulejobmenu').click(function() {

							var url = '/pages/calendar.jsp?id=' + '123';
							var $dialogc = $('<div id="caldialog"></div>').html(calendarurl).dialog({
								autoOpen : false,
								resizable : true,
								modal : true,
								height : 700,
								width : 1100,
								title : "",
								create : function(event, ui) {
									$(".ui-widget-header").hide();
								},
								buttons : [ {
									text : "Cancel",
									click : function() {
										$dialogc.dialog('close');
										alertify.error("Job Schedule Cancelled <strong>" + alertify.labels.cancel + "</strong>");
									},
									iconCls : 'icon-save'
								}, {
									text : "OK",
									click : function() {
										$dialogc.dialog('close');
										alertify.success("Job Scheduled <strong>" + alertify.labels.ok + "</strong>: ");
									},
									iconCls : 'icon-save'
								} ]
							});

							$dialogc.dialog('open');

						});

						$('#runjob').on('click', function() {

							var datamodel = prepareModel('123');
							console.log(JSON.stringify(datamodel))

							$.ajax({
								url : "/runnow/name",
								type : "POST",
								contentType : "application/json; charset=utf-8",
								data : JSON.stringify(datamodel),
								dataType : "json",
								success : function(result) {
									var response = JSON.parse(result);
									// $("#jobdialog").dialog({
									// resizable : false,
									// autoOpen : false,
									// height : 225,
									// width : 400,
									// modal : true,
									// dialogClass : 'ui-dialog-shadow',
									// create : function(event, ui) {
									// $(".ui-widget-header").hide();
									// },
									// buttons : [ {
									// text : "OK",
									// click : function() {
									// $('#jobdialog').dialog('close');
									jobpoll = setInterval(poll, 5000);
									function poll() {
										var routeid = datamodel[0].routeid;
										console.log("checking stats for  " + routeid);

										if (datamodel[0].source.itemType == 'stream') {
											return false;
										}

										$.ajax({
											url : '/getStatistics/' + routeid + "/" + datamodel[0].source.resources.length,
											type : "GET",
											success : function(statisitics) {

												console.log(statisitics);

												if (!result || result == '') {
													console.log(routeid + " still running");
												} else {

													if (!statisitics)
														return false;

													console.log(statisitics);

													var statsObj;
													try {
														statsObj = JSON.parse("[" + statisitics + "]");
													} catch (err) {
														console.log(err);
													}

													console.log("got stats for " + routeid + " clear stats polling");

													$("#runcnt").html("0");
													clearInterval(jobpoll);
													$("#statsdialog").dialog({
														autoOpen : false,
														modal : true,
														height : 600,
														width : 600,
														dialogClass : 'ui-dialog-shadow',
														resizable : true,
														create : function(event, ui) {
															$(".ui-widget-header").hide();
														},
														buttons : [ {
															text : "OK",
															click : function(e) {
																$("#statsdialog").dialog('close');
															},
															iconCls : 'icon-save'
														} ]
													});
													$(".statusbox").attr('src', '/img/job_stop.png');
													$(".jobstatus").html('completed');
													$('#statsdialog').dialog('open');
													globals.routeid = "job_" + new Date().getTime();
													document.getElementById("statsjson").innerHTML = syntaxHighlight(statsObj, undefined, 2);
												}
											},
											error : function(XMLHttpRequest, textStatus, errorThrown) {
												clearInterval(jobpoll);
												console.log("Status: " + textStatus);
												console.log("Error: " + errorThrown);
											}
										});
									}
									// },
									// iconCls : 'icon-save'
									// } ]
									// });

									$(".statusbox").attr('src', '/img/job_run.png');
									$(".jobstatus").html('running');
									$("#runcnt").html(datamodel.length);
									// $("#jobmessage").html(
									// "<table cellpadding=6><tr><td
									// valign=top><img
									// src='/img/ok.png'>&nbsp;&nbsp;</td><td><b>
									// " +
									// datamodel[0].routeid
									// + " submitted successfully</b><br>" +
									// "submitted
									// by : admin" + "<br>submitted on: " + (new
									// Date().getMonth() + 1)
									// + "/" + new Date().getDate() + "/" + new
									// Date().getYear() + " " + new
									// Date().getHours() +
									// ":" + new Date().getMinutes()
									// + ":" + new Date().getSeconds() +
									// "</td></tr><table>");

									alertify.success("Workflow Submitted <strong>" + alertify.labels.ok + "</strong>: " + datamodel[0].routeid);

									// $('#jobdialog').dialog('open');
								},
								error : function(XMLHttpRequest, textStatus, errorThrown) {
									console.log("Status: " + textStatus);
									console.log("Error: " + errorThrown);
								}
							});
						});

						$('.navbarnew').click(function() {

							$("#newworkflowdialog").dialog({
								resizable : false,
								autoOpen : false,
								height : 200,
								width : 500,
								modal : false,
								dialogClass : 'ui-dialog-shadow',
								create : function(event, ui) {
									$(".ui-widget-header").hide();
								},
								buttons : [ {
									text : "OK",
									click : function() {
										removeModel('123');
										clearNodes();
										$("#modeljson").html('');

										if (!instance) {
											globals.workflowname = $("#newworkflowname").val() != '' ? $("#newworkflowname").val().toUpperCase() : 'UNTITLED Workflow';
											$('#newworkflowdialog').dialog('close');
											return false;
										}

										instance.detachEveryConnection();
										instance.deleteEveryEndpoint();
										$.each($(".w"), function(index, elem) {
											elem.remove();
										});
										$('#newworkflowdialog').dialog('close');
										globals.workflowname = $("#newworkflowname").val() != '' ? $("#newworkflowname").val().toUpperCase() : 'UNTITLED Workflow';
										$("#treeDemo").hide();
										$('#treeloading').show();
										getAllConnections();
									},
									iconCls : 'icon-cancel'
								} ]
							});

							$('#newworkflowdialog').dialog('open');
						});

						$('.navbaropen').click(
								function() {

									$.ajax({
										url : '/getSavedLayouts/' + 'name',
										type : "GET",
										success : function(result) {
											var zNodes = JSON.parse(result);

											recurse(zNodes);

											var setting = {
												callback : {
													onClick : openClick
												},
												data : {
													simpleData : {
														enable : true
													}
												}
											};

											$.ajax({
												url : '/getSavedFiles/' + 'name',
												type : "GET",
												success : function(result) {
													var savedfiles = JSON.parse(result);
													var saveTreeObj = $.fn.zTree.getZTreeObj("openTree");
													$.each(savedfiles, function(index, file) {

														// console.log(saveTreeObj.getNodes())
														// console.log(file)

														var dirnode = saveTreeObj.getNodeByParam('id', file.parent_fldr_id);
														var dup = false;
														if (dirnode) {
															$.each(dirnode.children, function(index, child) {
																if (file.name == child.name) {
																	dup = true;
																}
															});

															if (!dup) {
																var newnode = saveTreeObj.addNodes(dirnode, {
																	id : new Date().getTime(),
																	pId : new Date().getTime(),
																	isParent : false,
																	name : file.name
																});
															}
														}
													});
												},
												error : function(XMLHttpRequest, textStatus, errorThrown) {
													console.log("Status: " + textStatus);
													console.log("Error: " + errorThrown);
												}
											});

											$.fn.zTree.init($("#openTree"), setting, zNodes);

											$("#dialog-open").dialog(
													{
														resizable : false,
														autoOpen : false,
														height : 420,
														width : 420,
														modal : false,
														dialogClass : 'ui-dialog-shadow',
														create : function(event, ui) {
															$(".ui-widget-header").hide();
														},
														buttons : [
																{
																	text : "Cancel",
																	click : function() {
																		$('#dialog-open').dialog('close');
																	},
																	iconCls : 'icon-cancel'
																},
																{
																	text : "Open",
																	click : function() {
																		var saveTreeObj = $.fn.zTree.getZTreeObj("openTree");
																		var node = saveTreeObj.getSelectedNodes()[0];
																		$.ajax({
																			url : '/open/' + node.pId + "/" + node.name,
																			type : "GET",
																			success : function(result) {

																				if (instance) {
																					instance.detachEveryConnection();
																					instance.deleteEveryEndpoint();
																					$.each($(".w"), function(index, elem) {
																						elem.remove();
																					});
																					removeModel('123');
																					clearNodes();
																				}

																				var model = JSON.parse(result.model);
																				var layout = JSON.parse(result.layout);

																				var nodes = {}
																				$.each(model, function(index, route) {

																					$.ajax({
																						url : '/getConnection/' + route.source.config.name + '/' + route.source.config.name,
																						type : "GET",
																						success : function(result) {
																							var connection = JSON.parse(result);

																							var match = recurse(connection.children, route.source.name);
																							foundnodes = [];
																							var matched = diff(match[0].schema, route.source.schema);
																							var matchmsgs = [];
																							$.each(matched, function(index, match) {
																								matchmsgs.push(match.name + " " + match.value + " "
																										+ (match.added ? 'added' : 'missing'));
																								globals.errors.push({
																									name : match.name,
																									value : match.value,
																									msg : (match.added ? 'added' : 'missing')
																								});
																							});
																							if (matched.length > 0) {
																								alertify.error("Schemas do not match: <strong>" + route.source.name
																										+ "</strong><br>" + matchmsgs.join("<br>"));
																								globals.errors.push({
																									errors : matchmsgs
																								});
																							}
																						},
																						error : function(XMLHttpRequest, textStatus, errorThrown) {
																							console.log("Status: " + textStatus);
																							console.log("Error: " + errorThrown);
																						}
																					});

																					var sconfig = route.source.config;
																					route.source.data = {};
																					route.source.data.config = sconfig;

																					var tconfig = route.target.config;
																					route.target.data = {};
																					route.target.data.config = tconfig;

																					nodes[route.source.id] = route.source
																					nodes[route.target.id] = route.target

																					globals.id += 2

																				});

																				$.each(nodes, function(index, node) {

																					var coords = $.grep(layout, function(obj) {
																						return obj.id === node.id;
																					});
																					addConnection(coords[0].x, coords[0].y, node, node.id.substring(8))
																					node.uid = node.id;
																					saveNode('123', node.id, node);
																				});

																				$.each(model, function(index, route) {
																					var sconfig = route.source.config;
																					route.source.data = {};
																					route.source.data.config = sconfig;

																					var tconfig = route.target.config;
																					route.target.data = {};
																					route.target.data.config = tconfig;

																					nodes[route.source.id] = route.source
																					nodes[route.target.id] = route.target

																					instance.connect({
																						source : route.source.id,
																						target : route.target.id,
																						type : "basic"
																					});
																				});

																				$('#dialog-open').dialog('close');

																			},
																			error : function(XMLHttpRequest, textStatus, errorThrown) {
																				console.log("Status: " + textStatus);
																				console.log("Error: " + errorThrown);
																			}
																		});
																	},
																	iconCls : 'icon-save'
																} ]
													});

											$('#dialog-open').dialog('open');

										},
										error : function(XMLHttpRequest, textStatus, errorThrown) {
											console.log("Status: " + textStatus);
											console.log("Error: " + errorThrown);
										}
									});

									var setting = {
										data : {
											simpleData : {
												enable : true
											}
										}
									};
								});

						$('.navbarclose').click(function() {
							instance.detachEveryConnection();
							instance.deleteEveryEndpoint();
							$.each($(".w"), function(index, elem) {
								elem.remove();
							});
						});

						$('.navbarsave').click(
								function() {

									$.ajax({
										url : '/getSavedLayouts/' + 'name',
										type : "GET",
										success : function(result) {
											var zNodes = JSON.parse(result);
											var setting = {
												data : {
													simpleData : {
														enable : true
													}
												}
											};

											$.ajax({
												url : '/getSavedFiles/' + 'name',
												type : "GET",
												success : function(result) {
													var savedfiles = JSON.parse(result);
													var saveTreeObj = $.fn.zTree.getZTreeObj("saveTree");
													$.each(savedfiles, function(index, file) {
														var dirnode = saveTreeObj.getNodeByParam('id', file.parent_fldr_id);
														var dup = false;
														$.each(dirnode.children, function(index, child) {
															if (file.name == child.name) {
																dup = true;
															}
														});

														if (!dup) {
															var newnode = saveTreeObj.addNodes(dirnode, {
																id : new Date().getTime(),
																pId : new Date().getTime(),
																isParent : false,
																name : file.name
															});
														}
													});
												},
												error : function(XMLHttpRequest, textStatus, errorThrown) {
													console.log("Status: " + textStatus);
													console.log("Error: " + errorThrown);
												}
											});

											$.fn.zTree.init($("#saveTree"), setting, zNodes);

											$("#dialog-save").dialog(
													{
														resizable : false,
														autoOpen : false,
														height : 420,
														width : 420,
														modal : false,
														dialogClass : 'ui-dialog-shadow',
														create : function(event, ui) {
															$(".ui-widget-header").hide();
														},
														buttons : [
																{
																	text : "Cancel",
																	click : function() {
																		$('#dialog-save').dialog('close');
																	},
																	iconCls : 'icon-cancel'
																},
																{
																	text : "Save",
																	click : function() {

																		var saveTreeObj = $.fn.zTree.getZTreeObj("saveTree");
																		var nodes = saveTreeObj.getSelectedNodes();

																		var saveTreeObj = $.fn.zTree.getZTreeObj("saveTree");
																		var nodes = saveTreeObj.getSelectedNodes();

																		if (!nodes[0].isParent) {
																			alertify.alert('please select a folder');
																			return false;
																		}

																		var layout = [];
																		$.each($(".w"), function(index, elem) {
																			layout.push({
																				x : $(elem).position().left,
																				y : $(elem).position().top,
																				id : $(elem)[0].id
																			});
																		});

																		var datamodel = {
																			'"id"' : '"' + new Date().getTime() + '"',
																			'"grp"' : '""',
																			'"parent_fldr_id"' : '"' + nodes[0].id + '"',
																			'"filename"' : '"' + $("#savefilename").val() + '"',
																			'"model"' : JSON.stringify(prepareModel('123')),
																			'"layout"' : JSON.stringify(layout),
																		};

																		$.ajax({
																			url : '/save/file',
																			type : "POST",
																			contentType : "application/json; charset=utf-8",
																			data : JSON.stringify(datamodel),
																			dataType : "json",
																			success : function(result) {
																				$('#dialog-save').dialog('close');
																				alertify.success("Workflow Saved <strong>" + alertify.labels.ok + "</strong>: "
																						+ $("#savefilename").val() + "(" + nodes[0].id + ")");

																			},
																			error : function(XMLHttpRequest, textStatus, errorThrown) {
																				console.log("Status: " + textStatus);
																				console.log("Error: " + errorThrown);
																			}
																		});
																	},
																	iconCls : 'icon-save'
																} ]
													});

											$("#savefilename").val(globals.workflowname);
											$('#dialog-save').dialog('open');
										},
										error : function(XMLHttpRequest, textStatus, errorThrown) {
											console.log("Status: " + textStatus);
											console.log("Error: " + errorThrown);
										}
									});
								});
					});

			$("#exploremenu").click(
					function() {
						$('.breadcrumbs').html(jobmenuitems);
						$("#canvas").hide();
						$("#canvas1").show();
						$("#treedisplay").hide();
						$("#treedisplay1").hide();
						$("#toolbox").hide();						
						
						$("#canvas2").hide();
						$("#canvas3").hide();

						$("#draggable").hide()

						$.ajax({
							url : "/getMetadata",
							success : function(msg) {

								var strategiesarray = [];

								console.log("==>" + msg);

								
								$.each(JSON.parse(msg), function(index, value) {
									console.log(value);

									var strategyarray = [];

									var source = JSON.parse(value.origin);
									var target = JSON.parse(value.dest);

									strategyarray.push(value.name.split("|")[0]);
									strategyarray.push(source.name);
									strategyarray.push(source.type);

									strategyarray.push(target.name);
									strategyarray.push(target.type);

									strategyarray.push(value.usr);
									strategyarray.push(value.time);

									if ("" == value.data.trim())
										value.data = "[{}]";

									if (target.type === 'trifacta') {

										console.log(value.props);
										strategyarray.push("<input type=button value='Edit' onclick=window.open('" + JSON.parse(value.props).url
												+ "')>&nbsp;<input type=button value='Delete'>");

									} else {
										strategyarray.push("<input type=button value='Edit' onclick=showSchema('" + value.data + "')>&nbsp;<input type=button value='Delete'>");
									}

									strategiesarray.push(strategyarray);

								});

								var responsiveHelper_datatable_fixed_column = undefined;
								var breakpointDefinition = {
									tablet : 1024,
									phone : 480
								};

								$('#datatable_fixed_column').DataTable().clear().rows.add(strategiesarray).draw();

								var otable = $('#datatable_fixed_column').DataTable(
										{
											"sDom" : "<'dt-toolbar'<'col-xs-12 col-sm-6 hidden-xs'f><'col-sm-6 col-xs-12 hidden-xs'<'toolbar'>>r>" + "t"
													+ "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
											"autoWidth" : true,
											/*
											 * "language" : { "emptyTable" : "<img
											 * src='img/wait-small.gif'>&nbsp;Loading..
											 * Please Wait.." },
											 */
											"preDrawCallback" : function() {
												if (!responsiveHelper_datatable_fixed_column) {
													responsiveHelper_datatable_fixed_column = new ResponsiveDatatablesHelper($('#datatable_fixed_column'), breakpointDefinition);
												}
											},
											"rowCallback" : function(nRow) {
												responsiveHelper_datatable_fixed_column.createExpandIcon(nRow);
											},
											"drawCallback" : function(oSettings) {
												responsiveHelper_datatable_fixed_column.respond();
											},
											"order" : [ [ 6, "desc" ] ]
										});

								$("#datatable_fixed_column thead th input[type=text]").on('keyup change', function() {

									otable.column($(this).parent().index() + ':visible').search(this.value).draw();

								});

							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								console.log(errorThrown);
							}
						});

					});

			$("#aboutmenu").click(function() {
				$('.breadcrumbs').html(aboutmenuitems);
			});

			$("#governmenu").click(function() {
				$('.breadcrumbs').html(toolsmenuitems);
				$("#canvas1").hide();
				$("#canvas").show();
				$("#treedisplay").hide();
				$("#treedisplay1").show();
				$("#toolbox").hide();
				
				$("#canvas2").hide();
				$("#canvas3").hide();

				$("#draggable").show()

				$("#accordion1").accordion({
					animate : true,
				});

				removeModel('123');
				clearNodes();
				$("#modeljson").html('');
				if (instance) {
					instance.detachEveryConnection();
					instance.deleteEveryEndpoint();
				}
				$.each($(".w"), function(index, elem) {
					elem.remove();
				});

				var setting = {
					callback : {
						onRightClick : myOnRightClick
					}
				};

				$.ajax({
					url : "/getPlugins",
					success : function(msg) {
						
						console.log("==>"+msg)

						var data = JSON.parse(msg.entity);
						var conformeddataSources = data.conformed;
						var conformeddataObjects = data.object;
						var available = data.datasources;

						$("#treeloading1").hide();

						zTreeObj2 = $.fn.zTree.init($("#treeDemo2"), setting, available);
						zTreeObj2.expandAll(false);

						zTreeObj3 = $.fn.zTree.init($("#treeDemo3"), setting, conformeddataSources);
						zTreeObj3.expandAll(false);

						zTreeObj4 = $.fn.zTree.init($("#treeDemo4"), setting, conformeddataObjects);
						zTreeObj4.expandAll(false);

						$("#addconformed").click(function() {
							addBasicNode("treeDemo3", "Elements", zTreeObj3)
						});

						$("#addconformed1").click(function() {
							addBasicNode("treeDemo4", "Objects", zTreeObj4)
						});

						$(".node_name").draggable({
							helper : 'clone',
							drag : function(event, ui) {
							},
							stop : function(event, ui) {
							},
							start : function(event, ui) {
								var nodeId = $(event)[0].currentTarget.id;
								nodeId = nodeId.substring(0, nodeId.length - 5);

								globals.currentnode = zTreeObj2.getNodeByParam('tId', nodeId);
								zTreeObj2.selectNode(globals.currentnode);

								console.log("**********************")
								console.log(globals.currentnode)

								if (!globals.currentnode) {
									globals.currentnode = zTreeObj3.getNodeByParam('tId', nodeId);
									zTreeObj3.selectNode(globals.currentnode);
								}

								if (!globals.currentnode) {
									globals.currentnode = zTreeObj4.getNodeByParam('tId', nodeId);
									zTreeObj4.selectNode(globals.currentnode);
								}
							}
						});
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log(errorThrown);
					}
				});

				$('#validatewf').on('click', function() {
					var schemaTypes = [], schemas = {};
					$.each(getModel('123'), function(index, route) {
						if (route.source.schema[0]) {
							schemaTypes.push(route.source.schema[0].type);
							schemas[route.source.schema[0].type] = route.source.schema;
						}
					});

					$.ajax({
						url : "/getSchemaReaders/" + "name",
						success : function(result) {
							readers = JSON.parse(result);
							for ( var key in readers) {
								for ( var key1 in readers[key].pathMap) {
									if (schemaTypes.indexOf(key1) != -1) {
										var schemadata = [];
										schemadata.push(readers[key].name);
										schemadata.push(key1);

										var currentSchema = schemas[key1];

										$.ajax({
											url : "/getSchema/" + "name",
											type : "POST",
											data : '{"id" : "' + schemadata.join(",") + '"}',
											contentType : 'application/json',
											success : function(result) {
												var schemaresult = JSON.parse(result);
												var matched = diff(currentSchema, schemaresult);

												var matchmsgs = [];
												$.each(matched, function(index, match) {
													matchmsgs.push(match.name + "  " + match.value + " " + match.index + " " + (match.added ? 'missing' : 'added'));
												});
												if (matched.length > 0) {
													alertify.error("Schemas do not match: <strong> " + currentSchema[0].type + "</strong><br>" + matchmsgs.join("<br>"));
													globals.errors.push({
														errors : matchmsgs
													});
													$("#numerrors").html(matchmsgs.length);
												} else {
													alertify.success("Workflow valid: <strong>OK</strong>");
													globals.errors = [];
													$("#numerrors").html(0);
												}
											},
											error : function(XMLHttpRequest, textStatus, errorThrown) {
												console.log("Status: " + textStatus);
												console.log("Error: " + errorThrown);
											}
										});
									}
								}
							}
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("Status: " + textStatus);
							console.log("Error: " + errorThrown);
						}
					});

				});
			});

			$("#operationmenu").click(function() {
				
				$('.breadcrumbs').html("");
				$("#canvas").hide();
				$("#canvas2").show();
				$("#treedisplay").hide();
				$("#treedisplay1").hide();
				$("#toolbox").hide();
				$("#canvas3").hide();
				$("#canvas1").hide();
				$("#draggable").hide()

			});
			
	$("#monitormenu").click(function() {
				
				$('.breadcrumbs').html("");
				$("#canvas").hide();
				$("#canvas2").hide();
				$("#treedisplay").hide();
				$("#treedisplay1").hide();
				$("#toolbox").hide();

				$("#draggable").hide()
				
				$("#canvas3").show();

			});

			/*
			 * $("#draggable, #canvas").click( function() {
			 * $('.breadcrumbs').html( "<a href=''><span id='workflowname'>" +
			 * (globals.workflowname.trim() != '' ? globals.workflowname :
			 * 'UNTITLED Workflow') + "</span></a>&nbsp; &rarr; &nbsp;<span
			 * id='currentdate'>" + new Date() + "</span>"); });
			 */
		});

function addBasicNode(tree, label, zTreeObj) {
	var treeObj = $.fn.zTree.getZTreeObj(tree);

	console.log(treeObj)
	var id = uuid()

	var newNode = {
		id : id,
		pId : id,
		pId : id,
		itemType : "Conformed Data " + label,
		"type" : "data",
		"parent" : "",
		name : label.toLowerCase().substring(0, label.length - 1) + " name",
		"data" : {
			"config" : {
				"host" : "host"
			}
		}
	};

	var nodes = treeObj.getSelectedNodes();
	var treeNode = nodes[0];

	newNode = treeObj.addNodes(treeNode, newNode);

	treeObj.editName(newNode[0]);

	$(".node_name").draggable({
		helper : 'clone',
		drag : function(event, ui) {
		},
		stop : function(event, ui) {
		},
		start : function(event, ui) {
			var nodeId = $(event)[0].currentTarget.id;
			nodeId = nodeId.substring(0, nodeId.length - 5);
			currentNode = zTreeObj.getNodeByParam('tId', nodeId);
			zTreeObj1.selectNode(currentNode);

			globals.currentnode = currentNode;
		}
	});

}

function myOnRightClick() {
	alert();
}

function showSchema(schema) {
	var parsedSchema = JSON.parse(schema);
	console.log(parsedSchema);

	globals.currentschema = parsedSchema

	$("#editschemadialog").dialog('open');

	var editschematable = $('#editschematable').DataTable();

	var editschemadata = editschematable.rows().nodes();

	var editschemaitems = []
	$.each(parsedSchema, function(index, value) {
		var editschemaitem = []
		editschemaitem.push(value.name);
		editschemaitem.push(value.value);
		editschemaitem.push("<select style='width:150px;' onchange=updateSchema()>" + "<option class='udfoption'>Select...</option><option class='udfoption' value='" + value.name
				+ ":de-dup'>de-dup</option>" + "<option class='udfoption' value='" + value.name + ":filter'>filter</option>" + "<option class='udfoption' value='" + value.name
				+ ":replace'>replace</option></select>");
		editschemaitems.push(editschemaitem);
	});

	$('#editschematable').DataTable().clear().rows.add(editschemaitems).draw();

}

function openClick(event, treeId, treeNode) {
	if (treeNode.isParent)
		return false;
	$("#openfilename").val(treeNode.name)
}

var calendarurl = '<iframe src="/pages/calendar.jsp" style="border: 0px; width:1000px; height:700px" id="calframe" name="calframe"></iframe>'
var viewmenuitems = "<a href='#' id='viewmodel'>VIEW MODEL</a>&nbsp; &rarr; &nbsp; <a href='#' id='viewnodes'>VIEW NODES</a>&nbsp; &rarr; &nbsp; <a href='#' id='viewconnection'>VIEW CONNECTIONS</a>&nbsp; &rarr; &nbsp; <a href='#' id='zoomin' onclick='zoomin()'>ZOOM IN</a>&nbsp; &rarr; &nbsp; <a href='#' id='zoomout' onclick='zoomout()'>ZOOM OUT</a>&nbsp; &rarr; &nbsp; <a href='#' class='zoomrefresh' onclick='zoomrefresh()'>ZOOM RESET</a>";
var jobmenuitems = ""
var workflowmenuitems = "<a href='#' class='navbarnew'>NEW WORKFLOW</a>&nbsp; &rarr; &nbsp; <a href='#' class='navbaropen'>OPEN WORKFLOW</a>&nbsp; &rarr; &nbsp; <a href='#' class='navbarsave'>SAVE WORKFLOW</a>&nbsp; &rarr; &nbsp; <a href='#' class='navbarclose'>CLOSE WORKFLOW</a>&nbsp; &rarr; &nbsp; <a href='#' id='runjob'>RUN JOB</a>&nbsp; &rarr; &nbsp; <a href='#' class='schedulejobmenu'>SCHEDULE JOB</a>&nbsp; &rarr; &nbsp; <a href='#' id='viewmodel'>VIEW MODEL</a>&nbsp; &rarr; &nbsp; <a href='#' id='viewnodes'>VIEW NODES</a>&nbsp; &rarr; &nbsp; <a href='#' id='viewconnection'>VIEW CONNECTIONS</a>"
var toolsmenuitems = "";
var aboutmenuitems = "<a href='#' id=''>ABOUT</a>&nbsp; &rarr; &nbsp; <a href='#' id=''>HELP</a>";

function updateSchema() {

	var ins = $('#editschematable').find("td select").map(function() {
		var x = $(this).find(":selected").val()
		if (x != 'Select...')
			return x;
	}).get()

	if (ins != 'Select...') {
		$.each(ins, function(idx, selecteditem) {
			var currentselecteditem = selecteditem.split(":");
			currentselectedname = currentselecteditem[0]
			currentselectedfunction = currentselecteditem[1]
			$.each(globals.currentschema, function(idx1, schemaitem) {

				if (schemaitem.name = currentselectedname){
					schemaitem.udfname = currentselectedfunction;
					return false;
				}
				
			});

		});
		
		console.log(globals.currentschema);
	}

}
function zoomin() {
	renderer.setZoom(renderer.getZoom() + .1);
}

function zoomout() {
	renderer.setZoom(renderer.getZoom() - .1);
}

function zoomrefresh() {
	renderer.setZoom(1);
}