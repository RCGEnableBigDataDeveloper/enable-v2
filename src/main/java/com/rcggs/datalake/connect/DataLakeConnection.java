package com.rcggs.datalake.connect;

import java.util.List;
import java.util.Map;

import com.rcggs.datalake.core.model.ItemDefinition;

public interface DataLakeConnection {

	public abstract Map<ItemDefinition, List<ItemDefinition>> describe(final String name)
			throws Exception;

}