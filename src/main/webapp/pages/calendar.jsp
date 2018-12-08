<script src="/js/jquery/jquery-ui.js"></script>
<script src="/js/jquery/jquery.js"></script>
<script src="/js/calendar/dhtmlxscheduler.js"
	type="text/javascript" charset="utf-8"></script>
<script src="/js/calendar/ext/dhtmlxscheduler_recurring.js"
	type="text/javascript" charset="utf-8"></script>
<script src="/js/calendar/ext/dhtmlxscheduler_minical.js"
	type="text/javascript" charset="utf-8"></script>

<link rel="stylesheet" href="/js/calendar/dhtmlxscheduler.css"
	type="text/css" media="screen" title="no title">

<link rel="stylesheet" type="text/css" href="../../../css/easyui.css">
<script type="text/javascript"
	src="../../../lib/jquery/jquery.easyui.min.js"></script>


<style type="text/css" media="screen">
html,body {
	margin: 0px;
	padding: 0px;
	height: 100%;
	overflow: hidden;
}

.calwaitdiv {
	text-align: center;
}
</style>

<script type="text/javascript" charset="utf-8">
	var event;

	function init() {
		
		var post = '<%=request.getParameter("key")%>';
		var decoded = decodeURIComponent(post);

		scheduler.config.multi_day = true;
		scheduler.config.event_duration = 35;
		scheduler.config.xml_date = "%Y-%m-%d %H:%i";
		scheduler.config.occurrence_timestamp_in_utc = true;
		scheduler.config.include_end_by = true;
		scheduler.config.repeat_precise = true;

		scheduler.attachEvent("onLightbox", function() {

			var lightbox_form = scheduler.getLightbox();
			var inputs = lightbox_form.getElementsByTagName('input');
			var date_of_end = null;
			for (var i = 0; i < inputs.length; i++) {
				if (inputs[i].name == "date_of_end") {
					date_of_end = inputs[i];
					break;
				}
			}

			scheduler.attachEvent("onEventDeleted", function(id, ev) {
				
				/* $('#dd').dialog({
					title : 'Please Wait...',
					width : 400,
					height : 200,
					closed : false,
					cache : false,
					modal : true
				});

				var data = {
					id : ev.id,
					text : ev.text,
					desc : {
						rec_type : ev.rec_type,
						rec_pattern : ev.rec_pattern,
						event_length : ev.event_length
					}
				}

				$.ajax({
					url : '../../../service/deleteJob/name',
					type : "POST",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(data),
					success : function(result) {
						window.location.reload();
						$("#caloverlay").remove();
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("Status: " + textStatus);
						console.log("Error: " + errorThrown);
					}
				}); */
			});

			scheduler.attachEvent("onEventChanged", function(id, ev) {				


				console.log(ev);
				
				var dates = scheduler.getRecDates(ev.id, 100);

				$.each(dates, function(i, date) {
					console.log(date);
				});
				
			});

			scheduler.attachEvent("onEventAdded", function(id, ev) {

				$('#dd').dialog({
					title : 'Please Wait...',
					width : 400,
					height : 200,
					closed : false,
					cache : false,
					modal : true
				});

				var dates = scheduler.getRecDates(ev.id, 100);

				$.each(dates, function(i, date) {

					var desc = {
						rec_type : ev.rec_type,
						rec_pattern : ev.rec_pattern,
						event_length : ev.event_length,
					};

					var data = {
						id : ev.id,
						text : ev.text,
						start : date.start_date,
						desc : desc,
						data : decoded
					};

					$.ajax({
						url : '../../../service/schedule',
						type : "POST",
						contentType : "application/json; charset=utf-8",
						data : JSON.stringify(data),
						success : function(result) {
							window.location.reload();
							$("#caloverlay").remove();
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("Status: " + textStatus);
							console.log("Error: " + errorThrown);
						}
					});

				});
			});

			var repeat_end_date_format = scheduler.date.date_to_str(scheduler.config.repeat_date);
			var show_minical = function() {
				if (scheduler.isCalendarVisible())
					scheduler.destroyCalendar();
				else {
					scheduler.renderCalendar({
						position : date_of_end,
						date : scheduler.getState().date,
						navigation : true,
						handler : function(date, calendar) {
							date_of_end.value = repeat_end_date_format(date);
							scheduler.destroyCalendar()
						}
					});
				}
			};
			date_of_end.onclick = show_minical;
		});

		scheduler.config.lightbox.sections = [ {
			name : "description",
			height : 200,
			map_to : "text",
			type : "textarea",
			focus : true
		}, {
			name : "recurring",
			type : "recurring",
			map_to : "rec_type",
			button : "recurring"
		}, {
			name : "time",
			height : 72,
			type : "calendar_time",
			map_to : "auto"
		} ];

		scheduler.init('schedulerdiv', new Date(), "month");

		$.ajax({
			url : '/getAllScheduledJobs/name',
			type : "POST",
			data : null,
			success : function(result) {
				var jobs = jQuery.parseJSON(result);
				$.each(jobs, function(index, job) {

					var desc = jQuery.parseJSON(job.desc);
					scheduler.addEvent({
						id : job.job_group,
						start_date : job.next_fire_time,
						end_date : job.next_fire_time,
						text : job.job_name,
						rec_pattern : desc ? desc.rec_pattern : '',
						rec_type : desc ? desc.rec_type : '',
						event_length : desc ? desc.event_length : '',
						event_pid : job.text
					});

				});
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("Status: " + textStatus);
				console.log("Error: " + errorThrown);
			}
		});
	}
</script>
</head>
<body onload="init();">
	<div id="schedulerdiv" class="dhx_cal_container"
		style='width: 100%; height: 100%;'>
		<div class="dhx_cal_navline">
			<div class="dhx_cal_prev_button">&nbsp;</div>
			<div class="dhx_cal_next_button">&nbsp;</div>
			<div class="dhx_cal_today_button"></div>
			<div class="dhx_cal_date"></div>
			<div class="dhx_minical_icon" id="dhx_minical_icon"
				onclick="show_minical()">&nbsp;</div>
			<div class="dhx_cal_tab" name="day_tab" style="right: 204px;"></div>
			<div class="dhx_cal_tab" name="week_tab" style="right: 140px;"></div>
			<div class="dhx_cal_tab" name="month_tab" style="right: 76px;"></div>
		</div>
		<div class="dhx_cal_header"></div>
		<div class="dhx_cal_data"></div>
	</div>
	<div id="dd" style='font-family: arial'>
		<br> <br>
		<div class='calwaitdiv'>
			<img src='../../../images/wait-small.gif'><br> <br>The
			calendar is being updated
		</div>
	</div>
</body>