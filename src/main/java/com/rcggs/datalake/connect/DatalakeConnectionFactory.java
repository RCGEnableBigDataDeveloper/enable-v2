package com.rcggs.datalake.connect;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.rcggs.datalake.configuration.DatalakeContext;
import com.rcggs.datalake.core.model.ConnectionConfig;


public class DatalakeConnectionFactory {

	private final Logger logger = Logger.getLogger(this.getClass());

	public DataLakeConnection getConnection(String name) {
		ConnectionConfig config = DatalakeContext.getConnectionConfig(name);
		try {
			return buildConnection(config);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("failed to instantiate connection class [" + config.getClazz() + "]", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private DataLakeConnection buildConnection(ConnectionConfig config) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<? extends DataLakeConnection> clazz;
		DataLakeConnection conn;
		clazz = (Class<? extends DataLakeConnection>) Class.forName(config.getClazz());
		Class<?>[] cArg = new Class[1];
		cArg[0] = ConnectionConfig.class;
		conn = clazz.getDeclaredConstructor(cArg).newInstance(config);
		return conn;
	}
}
