package com.rcggs.datalake.configuration;

import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.SchemaReader;
import com.rcggs.datalake.core.model.TransformerConfig;
import com.rcggs.datalake.dao.JobDao;
import com.rcggs.datalake.dao.MetadataDao;
import com.rcggs.datalake.dao.MetricsDao;
import com.rcggs.datalake.dao.WorkDao;
import com.rcggs.datalake.dao.WorkflowDao;

public class DatalakeContext {

	private static Injector injector;

	private static org.quartz.Scheduler scheduler;

	static {
		injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DatalakeConfiguration.class);
				bind(WorkflowDao.class);
				bind(JobDao.class);
				bind(MetadataDao.class);
				bind(MetricsDao.class);
				bind(WorkDao.class);
			}
		});

		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static WorkDao getWorkDao() {
		return injector.getInstance(WorkDao.class);
	}

	public static JobDao getJobDao() {
		return injector.getInstance(JobDao.class);
	}

	public static WorkflowDao getWorkflowDao() {
		return injector.getInstance(WorkflowDao.class);
	}

	public static MetadataDao getMetadataDao() {
		return injector.getInstance(MetadataDao.class);
	}

	public static MetricsDao getMetricsDao() {
		return injector.getInstance(MetricsDao.class);
	}

	public static String getProperty(final String name) {
		return injector.getInstance(DatalakeConfiguration.class).getProperties().get(name);
	}

	public static ConnectionConfig getConnectionConfig(final String name) {
		return injector.getInstance(DatalakeConfiguration.class).getConnections().get(name);
	}

	public static ConcurrentMap<String, SchemaReader> getSchemaReaders() {
		return injector.getInstance(DatalakeConfiguration.class).getSchemaReaders();
	}

	public static String getDrivers(final String name) {
		return injector.getInstance(DatalakeConfiguration.class).getDrivers().get(name);
	}

	public static ConnectionConfig getServices(final String name) {
		return injector.getInstance(DatalakeConfiguration.class).getServices().get(name);
	}

	public static ConcurrentMap<String, ConnectionConfig> getConnections() {
		return injector.getInstance(DatalakeConfiguration.class).getConnections();
	}

	public static ConcurrentMap<String, TransformerConfig> getTransformations() {
		return injector.getInstance(DatalakeConfiguration.class).getTransformations();
	}

	public static void scheduleJob(final String name, final String group, final String description,
			final Class<? extends Job> clazz, final Date start, final Date end, final String cron,
			final Map<String, Object> data) throws SchedulerException, ParseException {

		JobDetail job = JobBuilder.newJob(clazz).withIdentity(name, group)
				.usingJobData(data != null ? new JobDataMap(data) : new JobDataMap()).build();

		SimpleTrigger trigger = (SimpleTrigger) newTrigger()

				.startAt(start)

				.forJob(job).withDescription(description).build();

		scheduler.scheduleJob(job, trigger);
	}

	public static void deleteJob(final String name, final String group) throws SchedulerException {
		JobKey jobKey = new JobKey(name, group);
		TriggerKey triggerKey = new TriggerKey(name, group);
		scheduler.unscheduleJob(triggerKey);
		scheduler.deleteJob(jobKey);
	}
}
