var eventFire = function(el, etype) {
	if (el.fireEvent) {
		el.fireEvent('on' + etype);
	} else {
		var evObj = document.createEvent('Events');
		evObj.initEvent(etype, true, false);
		el.dispatchEvent(evObj);
	}
}

var uuid = function() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}

var syntaxHighlight = function(json) {
	if (typeof json != 'string') {
		json = JSON.stringify(json, undefined, 2);
	}
	json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	json = json.replace(/^.*pwd.*$/mg, "\t\"pwd\":  \"*****\",");
	return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {

		if (match == "\"error\":") {
			var failed = parseInt($("#failedcnt").html());
			$("#failedcnt").html(failed + 1);
			return '<span class="' + "error" + '">' + match + '</span>';
		}
		var cls = 'number';
		if (/^"/.test(match)) {
			if (/:$/.test(match)) {
				cls = 'key';
			} else {
				cls = 'string';
			}
		} else if (/true|false/.test(match)) {
			cls = 'boolean';
		} else if (/null/.test(match)) {
			cls = 'null';
		}

		return '<span class="' + cls + '">' + match + '</span>';
	});
}

var mouseDown = function(e) {

	var ctrlPressed = 0;
	var altPressed = 0;
	var shiftPressed = 0;

	if (parseInt(navigator.appVersion) > 3) {

		var evt = e ? e : window.event;

		if (document.layers && navigator.appName == "Netscape" && parseInt(navigator.appVersion) == 4) {
			var mString = (e.modifiers + 32).toString(2).substring(3, 6);
			shiftPressed = (mString.charAt(0) == "1");
			ctrlPressed = (mString.charAt(1) == "1");
			altPressed = (mString.charAt(2) == "1");
			self.status = "modifiers=" + e.modifiers + " (" + mString + ")";
		} else {
			shiftPressed = evt.shiftKey;
			altPressed = evt.altKey;
			ctrlPressed = evt.ctrlKey;
			self.status = "{" + '"shiftKey" :' + shiftPressed + ', "altKey" :' + altPressed + ', "ctrlKey" :' + ctrlPressed + "}";
		}
		if (shiftPressed || altPressed || ctrlPressed)
			return self.status;
	}
	return true;
};

var locked = false;
var autopager = window.setInterval(function() {
	if (!locked) {
		locked = true;
		window.setTimeout(function() {
			locked = false;
		}, 1000);
		$("#currentdate").html(new Date());
	}
}, 8000);
