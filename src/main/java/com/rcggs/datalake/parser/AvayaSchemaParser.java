package com.rcggs.datalake.parser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AvayaSchemaParser implements SchemaParser {

	private final Logger logger = LoggerFactory.getLogger(AvayaSchemaParser.class);
	private ConcurrentMap<String, String> tables;

	@Override
	public void parse(String filePath) throws Exception {

		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		try {
			doc = db.parse(new File(filePath));
			doc.getDocumentElement().normalize();

			// read all tables
			NodeList properties = doc.getElementsByTagName("Group");
			logger.debug("Number of Groups: {}", properties.getLength());
			for (int i = 0; i < properties.getLength(); ++i) {

				// get tables
				NodeList tables = ((Element) properties.item(i)).getElementsByTagName("GroupHeader").item(0)
						.getChildNodes();
				Node nNode = tables.item(1);
				// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String tableName = eElement.getElementsByTagName("FormattedValue").item(0).getTextContent();
				logger.debug("table: {}", tableName);

				// get all columns
				NodeList columns = ((Element) properties.item(i)).getElementsByTagName("Details");
				for (int j = 0; i < columns.getLength(); ++j) {
					Node n = columns.item(j);
					if (n.getNodeType() == Node.ELEMENT_NODE) {

					}
				}

			}

		} catch (SAXException | IOException e) {
			logger.error("Unable to parse file", e);
			throw e;
		}

	}

}
