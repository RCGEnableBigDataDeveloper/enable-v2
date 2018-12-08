//package com.rcggs.enable.data.controller;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.UUID;
//
//import javax.ws.rs.core.Response;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.rcggs.datalake.core.model.ConnectionConfig;
//import com.rcggs.datalake.core.model.Level;
//import com.rcggs.datalake.pool.DataSource;
//import com.rcggs.enable.data.service.HttpUtil;
//
//@RestController
//public class PluginController2 {
//
//	final ObjectMapper mapper = new ObjectMapper();
//
//	// -- String url = "http://52.201.45.52:3005/v4"; *** northeast
//	String url = "http://52.2.251.18:3005/v3";
//
//	@RequestMapping(value = "/getPlugins", method = RequestMethod.GET, produces = "application/json")
//	public Response getDataElements() {
//
//		String result = null;
//		Response r = null;
//
//		List<Level> top = new ArrayList<>();
//
//		try {
//
//			Map<String, Object> dataMap = new HashMap<>();
//
//			Connection con = DataSource.getConnection();
//			Statement stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery("select * from dl_metadata");
//			List<Level> available = new ArrayList<>();
//			while (rs.next()) {
//				Level parentLevel = new Level();
//				parentLevel.setName(rs.getString(2).split("\\|")[0]);
//				parentLevel.setId(rs.getString(1));
//				parentLevel.setText(rs.getString(2));
//				parentLevel.setData(dataMap);
//				List<Level> availableChildren = new ArrayList<>();
//
//				if (rs.getString(3) == null || "".equals(rs.getString(3).trim())) {
//
//				} else {
//					JsonNode data = mapper.readTree(rs.getString(3));
//					for (Iterator<JsonNode> it = data.elements(); it.hasNext();) {
//						JsonNode node = it.next();
//						Level schemaLevel = new Level();
//						schemaLevel.setName(node.get("name").asText());
//						schemaLevel.setId(UUID.randomUUID().toString());
//						schemaLevel.setText(node.get("name").asText());
//
//						dataMap.put("config", rs.getString(8));
//						schemaLevel.setData(dataMap);
//
//						availableChildren.add(schemaLevel);
//					}
//				}
//				parentLevel.setChildren(availableChildren);
//
//				available.add(parentLevel);
//				// con.close();
//			}
//
//			top.addAll(available);
//
//			String wrangled = HttpUtil.get(url + "/wrangledDatasets");
//			JsonNode wrangledNode = mapper.readTree(wrangled);
//			Iterator<JsonNode> w = wrangledNode.get("data").elements();
//
//			while (w.hasNext()) {
//				JsonNode wrangledDetail = w.next();
//
//				String id = wrangledDetail.get("id").asText();
//				String data = HttpUtil.get(url + "/flowNodes/" + id + "/scriptGraph");
//				JsonNode wrangledNodeData = mapper.readTree(data);
//
//				ArrayNode orders = (ArrayNode) wrangledNodeData.get("nodeIds");
//				int latest = 0;
//				if (orders != null) {
//					latest = orders.get(0).asInt();
//				}
//
//				String location = wrangledNodeData.get("nodeToLines").get(String.valueOf(latest)).get(0).get("task")
//						.get("parameters").get("location").get("value").asText();
//
//				boolean isLocation = true;
//
//				JsonNode locationNode = null;
//				try {
//					locationNode = mapper.readTree(location);
//				} catch (Exception e) {
//					isLocation = false;
//				}
//
//				Level parentLevel = new Level();
//				parentLevel.setName("Available Data Elements");
//				parentLevel.setId("0");
//				parentLevel.setText("Available Data Elements");
//
//				Level dataSet = new Level();
//				ConnectionConfig config = new ConnectionConfig();
//
//				if (!isLocation) {
//					dataSet.setName(location.substring(location.lastIndexOf("/") + 1));
//					dataSet.setText(location.substring(location.lastIndexOf("/") + 1));
//					config.setHost("host");
//					dataSet.setParent("parent");
//
//					dataMap.put("config", config);
//					dataSet.setData(dataMap);
//
//				} else {
//					dataSet.setName(locationNode.get("jdbcTable").asText());
//					dataSet.setText(locationNode.get("jdbcTable").asText());
//					config.setHost("host");
//					dataSet.setParent("parent");
//
//					dataMap.put("config", config);
//					dataSet.setData(dataMap);
//
//				}
//
//				String s = HttpUtil.get(url + "/flowNodes/" + id + "/scriptGraph");
//				JsonNode n = mapper.readTree(s);
//
//				if (!isLocation) {
//					Iterator<JsonNode> i = n.get("nodeToLines").elements();
//
//					while (i.hasNext()) {
//						JsonNode x = i.next();
//
//						if (x != null) {
//
//							Iterator<JsonNode> js = x.elements();
//
//							while (js.hasNext()) {
//
//								List<Level> children = new LinkedList<>();
//								JsonNode task = js.next().get("task");
//								String taskType = task.get("type").asText();
//								JsonNode annotations = task.get("annotations").get("outputColumnAnnotations");
//
//								if (annotations != null) {
//
//									if (!taskType.toLowerCase().equals("replacepatterns")) {
//										Iterator<Entry<String, JsonNode>> columns = annotations.fields();
//
//										while (columns.hasNext()) {
//											Entry<String, JsonNode> e = columns.next();
//
//											String column = e.getKey();
//											Level l = new Level();
//											l.setName(column);
//											l.setId(UUID.randomUUID().toString());
//											l.setText(column);
//											config.setHost("host");
//											l.setParent("parent");
//											l.setItemType("Data Element");
//											l.setType("data");
//
//											dataMap.put("config", config);
//											l.setData(dataMap);
//
//											children.add(l);
//										}
//
//										dataSet.setChildren(children);
//									} else {
//
//										// System.err.println(taskType + " -- "
//										// + annotations + " /n" + x);
//
//									}
//								}
//							}
//						}
//					}
//				} else {
//					List<Level> jdbcLevels = new LinkedList<>();
//					Iterator<JsonNode> jdbc = n.get("nodeToLines").elements();
//					while (jdbc.hasNext()) {
//						JsonNode jdbcAnnotations = jdbc.next().get(0).get("task").get("annotations")
//								.get("outputColumnAnnotations");
//						if (jdbcAnnotations != null) {
//							Iterator<Entry<String, JsonNode>> jdbcAnnotationsIterator = jdbcAnnotations.fields();
//							while (jdbcAnnotationsIterator.hasNext()) {
//								Entry<String, JsonNode> columns = jdbcAnnotationsIterator.next();
//								Level level = new Level();
//								level.setName(columns.getKey());
//								jdbcLevels.add(level);
//							}
//						}
//					}
//
//					dataSet.setChildren(jdbcLevels);
//				}
//
//				top.add(dataSet);
//
//				parentLevel.setChildren(top);
//
//				result = mapper.writeValueAsString(parentLevel);
//				// System.err.println("[" + result + "]");
//
//				String confromed = null;
//				List<Level> top1 = new ArrayList<>();
//				Level parentLevel1 = new Level();
//				parentLevel1.setName("Conformed Data Elements");
//				parentLevel1.setId("0");
//				parentLevel1.setText("Conformed Data Elements");
//
//				Level add = new Level();
//				add.setName("unassigned");
//				top1.add(add);
//
//				parentLevel1.setChildren(top1);
//
//				String confromed2 = null;
//				List<Level> top2 = new ArrayList<>();
//				Level parentLevel2 = new Level();
//				parentLevel2.setName("Conformed Data Objects");
//				parentLevel2.setId("0");
//				parentLevel2.setText("Conformed Data Objects");
//
//				Level add2 = new Level();
//				add2.setName("unassigned");
//				top2.add(add2);
//
//				parentLevel2.setChildren(top2);
//
//				try {
//					confromed = mapper.writeValueAsString(parentLevel1);
//					confromed2 = mapper.writeValueAsString(parentLevel2);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				r = Response.status(201).entity("{\"datasources\" : [" + result + "], \"conformed\" : " + confromed
//						+ ", \"object\" : " + confromed2 + "}").build();
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return r;
//	}
//
//}
