(function() {

	$('#dialog-save').hide();
	$('#dialog-open').hide();

	function repositionElement(id, posX, posY) {
		$('#' + id).css('left', posX);
		$('#' + id).css('top', posY);
		jsPlumb.repaint(id);
	}

	function update(datamodel) {
		$.ajax({
			url : "service/update/name",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(datamodel),
			dataType : "json",
			success : function(result) {
				console.log(result);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("Status: " + textStatus);
				console.log("Error: " + errorThrown);
			}
		});
	}

	newFolder = function() {

		var saveTreeObj = $.fn.zTree.getZTreeObj("saveTree");
		var nodes = saveTreeObj.getSelectedNodes();

		if (nodes.length == 0 || !nodes[0].isParent) {
			alertify.alert('please select a folder');
			return false;
		}

		// console.log(nodes[0])

		alertify.prompt("please enter a folder name", function(e, str) {
			if (e) {
				treeNode = nodes[0];
				var newnode = saveTreeObj.addNodes(treeNode, {
					id : new Date().getTime(),
					pId : new Date().getTime(),
					isParent : true,
					name : str
				});

				var data = JSON.stringify(saveTreeObj.getNodes());

				$.ajax({
					url : '/saveFolder/' + data,
					// type : "POST",
					// contentType : "application/json",
					// data : data,
					// dataType : "json",
					success : function(result) {
						console.log(result);
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("Status: " + textStatus);
						console.log("Error: " + errorThrown);
					}
				});

				alertify.success("Workflow Saved <strong>" + alertify.labels.ok + "</strong>: " + str);
			} else {
				alertify.error("Workflow Save Cancelled <strong>" + alertify.labels.cancel + "</strong>");
			}
		}, "New Folder");

		// var newFolderName = alertify.prompt("Please enter your name", "New
		// Folder");
		// if (newFolderName != null) {
		// treeNode = nodes[0];
		// var newnode = saveTreeObj.addNodes(treeNode, {
		// id : new Date().getTime(),
		// pId : new Date().getTime(),
		// isParent : true,
		// name : newFolderName
		// });
		//
		// var data = JSON.stringify(saveTreeObj.getNodes());
		// console.log(data)
		//
		// $.ajax({
		// url : '/saveFolder/' + data,
		// //type : "POST",
		// // contentType : "application/json",
		// // data : data,
		// // dataType : "json",
		// success : function(result) {
		// console.log(result);
		// },
		// error : function(XMLHttpRequest, textStatus, errorThrown) {
		// console.log("Status: " + textStatus);
		// console.log("Error: " + errorThrown);
		// }
		// });
		// }
	};
}());