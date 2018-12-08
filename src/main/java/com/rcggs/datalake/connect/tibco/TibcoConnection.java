package com.rcggs.datalake.connect.tibco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TopicInfo;

public class TibcoConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	final Logger logger = LoggerFactory.getLogger(getClass());

	java.sql.Connection connection;
	ConnectionConfig config;

	public TibcoConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void put() throws Exception {
		Connection connection = null;
		Session session = null;
		MessageProducer msgProducer = null;
		Destination destination = null;

		try {
			ObjectMessage msg;
			ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(
					"tcp://" + config.getHost() + ":" + config.getPort());

			connection = factory.createConnection(config.getUser(), "");
			session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic("sample");
			msgProducer = session.createProducer(null);
			msg = session.createObjectMessage();
			msg.setObjectProperty("data", "");

			msgProducer.send(destination, msg);
			connection.close();

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {

		Map<ItemDefinition, List<ItemDefinition>> dest = new HashMap<ItemDefinition, List<ItemDefinition>>();

		TibjmsAdmin _admin = new TibjmsAdmin(config.getHost(), config.getUser(), "");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);
		List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();
		com.tibco.tibjms.admin.QueueInfo[] info = _admin.getQueues();
		for (int i = 0; i < info.length; i++) {
			String queueName = info[i].getName();
			if (!queueName.startsWith("$") && !queueName.startsWith(">")) {
				ItemDefinition cd = new ItemDefinition();
				cd.setName(queueName);
				cd.setData(data);
				cd.setType("queue");
				columnDefs.add(cd);

			}
		}
		TopicInfo[] tinfo = _admin.getTopics();
		for (int i = 0; i < tinfo.length; i++) {
			String topicName = tinfo[i].getName();
			if (!topicName.startsWith("$") && !topicName.startsWith(">")) {
				ItemDefinition cd = new ItemDefinition();
				cd.setName(topicName);
				cd.setData(data);
				cd.setType("topic");
				columnDefs.add(cd);

			}
		}

		ItemDefinition idef = new ItemDefinition();
		idef.setName(config.getHost());
		idef.setItemType("jms");

		dest.put(idef, columnDefs);

		return dest;
	}
}