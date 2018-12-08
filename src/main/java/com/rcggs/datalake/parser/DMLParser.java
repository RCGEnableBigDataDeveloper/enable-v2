package com.rcggs.datalake.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.datalake.resource.ResourceReader;
import com.rcggs.datalake.resource.ResourceType;



public class DMLParser extends ResourceReader<SchemaDef> implements SchemaParser {

	@Override
	public List<SchemaDef> read(String path, String type, Map<String, String> pathMap) {
		try {
			String record = IOUtils.toString(loadResource("/record.dml", ResourceType.CLASSPATH.name()));
			String schema = StringUtils.substringBetween(record, "record", "end;");
			String[] schemaDefs = schema.split("\n");

			List<SchemaDef> _schemaDefs_ = new LinkedList<>();

			for (String schemaDef : schemaDefs) {
				if (schemaDef.trim().length() > 0) {
					SchemaDef _schemaDef_ = new SchemaDef();
					schemaDef = schemaDef.trim();
					String name = schemaDef.substring(schemaDef.lastIndexOf(")") + 1).trim();
					String schemaType = schemaDef.substring(0, schemaDef.indexOf("("));
					String delimiter = StringUtils.substringBetween(schemaDef, "(", ")").replaceAll("\"", "");
					String defaultValue = null;
					String format = null;
					List<String> defgroup = getDefinitionGroups(schemaDef);
					if (defgroup.size() > 1) {
						if (schemaType.equals("date")) {
							name = schemaDef.substring(schemaDef.lastIndexOf(")") + 1, schemaDef.length() - 1).trim();
							format = defgroup.get(0);
							delimiter = defgroup.get(1);
						} else {
							name = schemaDef.substring(schemaDef.indexOf(")") + 1, schemaDef.indexOf("=")).trim();
							defaultValue = defgroup.get(1);
							delimiter = defgroup.get(0);
						}
					}

					_schemaDef_.setName(name);
					_schemaDef_.setValue(schemaType);

					if (NumberUtils.isNumber(delimiter)) {
						_schemaDef_.setFixedWidth(true);
						_schemaDef_.setStart(delimiter);
					} else {
						_schemaDef_.setDelimiter(delimiter);
					}
					_schemaDef_.setDefaultValue(defaultValue);
					_schemaDef_.setFormat(format);
					_schemaDefs_.add(_schemaDef_);
				}
			}

			System.err.println(mapper.writeValueAsString(_schemaDefs_));

			return _schemaDefs_;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parse(String filePath) throws Exception {

	}

	List<String> getDefinitionGroups(final String def) {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("\\((.*?)\\)");
		Matcher regexMatcher = regex.matcher(def);
		while (regexMatcher.find()) {
			matchList.add(regexMatcher.group(1).replaceAll("\"", ""));
		}
		return matchList;
	}

	public static void main(String[] args) {
		DMLParser p = new DMLParser();
		try {
			p.read("","",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
