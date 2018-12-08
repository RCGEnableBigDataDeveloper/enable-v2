package com.rcggs.enable.data.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.connect.DatalakeConnectionFactory;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.Level;

public class MetadataBuilder {

	static final Logger logger = Logger.getLogger(MetadataBuilder.class);

	public static Level build(final DatalakeConnectionFactory datalakeConnectionFactory,
			final Entry<String, ConnectionConfig> entry, String name) {

		DataLakeConnection connection = datalakeConnectionFactory.getConnection(entry.getKey());
		long time = System.currentTimeMillis();
		Map<ItemDefinition, List<ItemDefinition>> results = null;
		try {
			results = connection.describe(name);
		} catch (Exception e) {
			logger.error("an error occurred " + e.getMessage());
			logger.error(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		}

		final List<Level> children = new ArrayList<Level>();
		String type = entry.getValue().getType();
		final Level level = new Level();
		level.setId(UUID.randomUUID().toString());
		level.setText(entry.getKey());
		level.setName(entry.getKey());
		level.setType(type);
		level.setIconCls("icon-blufolder");

		if (results != null) {
			for (Entry<ItemDefinition, List<ItemDefinition>> result : results.entrySet()) {

				Level childLevel = new Level();
				childLevel.setId(UUID.randomUUID().toString());
				childLevel.setText(result.getKey().getName());
				childLevel.setName(result.getKey().getName());
				childLevel.setItemType(result.getKey().getItemType());
				childLevel.setType(type);
				childLevel.setSchema(result.getKey().getSchema());
				childLevel.setIconCls("icon-blufolder");
				if (result.getValue() != null && result.getValue().size() > 0 && result.getValue().get(0) != null) {
					childLevel.setData(result.getValue().get(0).getData());
					childLevel.setParent(result.getKey().getParent());
				} else {
					childLevel.setData(result.getKey().getData());
					childLevel.setParent(result.getKey().getParent());
				}
				List<Level> leafs = new ArrayList<Level>();
				if (result.getValue() != null) {
					for (ItemDefinition def : result.getValue()) {
						Level leaf = new Level();
						leaf.setId(UUID.randomUUID().toString());
						leaf.setText(def.getName());
						leaf.setName(def.getName());

						Map<String, Object> typeDefs = new HashMap<String, Object>();
						typeDefs.put("type", def.getType());
						typeDefs.put("length", def.getLength());
						typeDefs.put("precision", def.getPrecision());
						typeDefs.put("scale", def.getScale());
						leaf.setType(def.getType());
						leaf.setParent(def.getParent() != null ? def.getParent() : "");
						leaf.setData(def.getData());
						leaf.setIconCls("icon-blufolder");
						leaf.setItemType(def.getItemType());
						leaf.setSchema(def.getSchema());
						leafs.add(leaf);

						if (def.getChildren() != null) {
							getChildren(def, leaf);
						} else {
							leaf.setIconCls("icon-cog");
						}
					}
				}
				childLevel.setChildren(leafs);
				children.add(childLevel);
			}

			createMetric(entry, time, children.size());
			level.setChildren(children);

		}

		return level;
	}

	static void getChildren(final ItemDefinition def, final Level leaf) {
		List<ItemDefinition> childItems = def.getChildren();

		List<Level> childItemList = new ArrayList<Level>();
		for (ItemDefinition childItem : childItems) {
			Level leaf1 = new Level();
			leaf1.setId(UUID.randomUUID().toString());
			leaf1.setText(childItem.getName());
			leaf1.setName(childItem.getName());
			leaf1.setType(childItem.getType());
			leaf1.setItemType(childItem.getItemType());
			leaf1.setIconCls("icon-cog");
			leaf1.setData(childItem.getData());
			leaf1.setParent(childItem.getParent());
			leaf1.setSchema(childItem.getSchema());
			childItemList.add(leaf1);

			if (childItem.getChildren() != null) {
				getChildren(childItem, leaf1);
			} else {
				leaf1.setIconCls("icon-cog");
			}
		}

		leaf.setChildren(childItemList);
	}

	private static void createMetric(final Entry<String, ConnectionConfig> entry, final long time, final int size) {

		// Metric metric = new Metric();
		// metric.setId(new java.util.Date().toString());
		// metric.setName(entry.getValue().getName());
		// metric.setDescription("");
		// metric.setType(entry.getValue().getType());
		// metric.setTotalFiles(size);
		// metric.setTime(System.currentTimeMillis() - time);
		// metric.setMessage("Successfully loaded metadata for: " +
		// entry.getKey());
		// metric.setStatus("success");
		//
		// DatalakeContext.getMetricsDao().saveMetric(metric);

	}
}
