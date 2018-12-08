var globals = {
	identifier : 'endpoint',
	selectednodes : [],
	currentnode : undefined,
	workflowname : 'UNTITLED Workflow',
	id : 0,
	routeid : "job_" + new Date().getTime(),
	conmap : {},
	errors : []
};

var zTreeObj;

$(document).ready(function() {

	"use strict";

	getAllConnections();

	$('.icon-level-down').on('click', function() {
		zTreeObj.expandAll(true);
	});

	$('.icon-level-up').on('click', function() {
		zTreeObj.expandAll(false);
	});
});

function getAllConnections() {
	$.ajax({
		url : "/getConnections/" + "name",
		success : function(result) {

			$('#treeloading').hide();
			$("#treeDemo").show();

			var zNodes = JSON.parse(result);
			var setting = {
				callback : {
					onRightClick : myOnRightClick,
					onDblClick : myOnDoubleClick
				}
			};

			console.log(JSON.stringify(zNodes[1]))

			zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, zNodes[0]);
			zTreeObj.expandAll(false);

			zTreeObj1 = $.fn.zTree.init($("#treeDemo1"), setting, zNodes[1]);
			zTreeObj1.expandAll(false);

			$(".node_name").draggable({
				helper : 'clone',
				drag : function(event, ui) {
				},
				stop : function(event, ui) {
				},
				start : function(event, ui) {
					
					var nodeId = $(event)[0].currentTarget.id;
					nodeId = nodeId.substring(0, nodeId.length - 5);
					globals.currentnode = zTreeObj.getNodeByParam('tId', nodeId);

					if (globals.currentnode) {
						zTreeObj.selectNode(globals.currentnode);
					} else {
						globals.currentnode = zTreeObj1.getNodeByParam('tId', nodeId);
						zTreeObj1.selectNode(globals.currentnode);
					}
					
					console.log(globals.currentnode);
				}
			});

			$('#canvas, #canvas2').droppable({
				drop : function(event, ui) {

					if (ui.draggable[0].className.indexOf('node_name') == -1)
						return false;

					var node = globals.currentnode;

					var wrapper = $(this).parent();
					var parentOffset = wrapper.offset();
					var relX = event.pageX - parentOffset.left + wrapper.scrollLeft();
					var relY = event.pageY - parentOffset.top + wrapper.scrollTop();

					var nodeKey = ((node.parent) ? node.parent.replace(/\\/g, "/") : "") + node.name + "|" + node.data.config.host + "|" + node.data.config.type;

					console.log(node);
					console.log(nodeKey);

					addNode(node, nodeKey, relX, relY);
				}
			});

			function myOnRightClick(event, treeId, treeNode) {
				console.log(treeNode);
			}

			function myOnDoubleClick(event, treeId, treeNode) {
				console.log(treeNode);

				var wrapper = $("#canvas").parent();
				var parentOffset = wrapper.offset();
				var relX = event.pageX - parentOffset.left + wrapper.scrollLeft();
				var relY = event.pageY - parentOffset.top + wrapper.scrollTop();
				var node = treeNode;

				var nodeKey = ((node.parent) ? node.parent.replace(/\\/g, "/") : "") + node.name + "|" + node.data.config.host + "|" + node.data.config.type;

				console.log(node);
				console.log(nodeKey);

				addNode(node, nodeKey);

			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("Status: " + textStatus);
			console.log("Error: " + errorThrown);
		}
	});

	function addNode(node, nodeKey, relX, relY) {

		$.ajax({
			url : "/getMetadata/" + "name",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			data : "{\"key\": \"" + nodeKey.toLowerCase() + "\"}",
			success : function(result) {
				var metadata = JSON.parse(result);
				if (metadata[0]) {
					console.log(metadata[0].data);
					if (!node.schema) {
						node.schema = JSON.parse(metadata[0].data);
					}
				}

				$.ajax({
					url : '/getOptions/' + node.data.config.type,
					success : function(result) {
						var options = JSON.parse(result);
						var rows = [], advanced = [];
						$.each(options, function(index, elem) {
							var row = {
								name : elem.name,
								value : '',
								group : 'Options',
								editor : elem.editor
							};
							rows.push(row);
						});

						node.options = rows;
						node.advanced = advanced;
						node.uid = globals.identifier + globals.id

						if (!node.data || node.itemType == 'host') {
							$.messager.alert('Alert', 'cannot add <b>' + node.text + '</b> to the workflow');
							return;
						}

						saveNode(globals.id, globals.identifier + globals.id, node);
						addConnection(relX, relY, node, globals.id);
						globals.id++;
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("Status: " + textStatus);
						console.log("Error: " + errorThrown);
					}
				});
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("Status: " + textStatus);
				console.log("Error: " + errorThrown);
			}
		});
	}
}
