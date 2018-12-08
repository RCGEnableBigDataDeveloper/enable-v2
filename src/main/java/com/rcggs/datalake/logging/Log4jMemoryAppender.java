package com.rcggs.datalake.logging;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jMemoryAppender extends AsyncAppender implements Appender {

	public static Set<String> msgs = new LinkedHashSet<String>();

	@Override
	public void doAppend(LoggingEvent event) {
		if (msgs.size() > 1000) {
			msgs.clear();
		}

		String msg = String.format("[%s] [%s] [%s] [%s] [%s] [%s]", event
				.getLevel(), event.getTimeStamp(), event
				.getLocationInformation().getClassName(), event
				.getLocationInformation().getMethodName(), event
				.getLocationInformation().getLineNumber(), event.getMessage()
				.toString());

		msgs.add(msg);
	}
}
