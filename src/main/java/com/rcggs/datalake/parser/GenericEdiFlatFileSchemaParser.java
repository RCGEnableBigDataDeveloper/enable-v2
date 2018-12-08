package com.rcggs.datalake.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.enable.data.resource.ResourceReader;


public class GenericEdiFlatFileSchemaParser extends ResourceReader<SchemaDef> implements SchemaParser {

	private Map<String, List<SchemaDef>> mappings = new HashMap<>();

	@Override
	public List<SchemaDef> read(final String path, final String type, final Map<String, String> pathMap) {
		getMappings(path, type, pathMap);
		return mappings.get(type);
	}

	void getMappings(final String path, final String type, final Map<String, String> pathMap) {

		for (final Map.Entry<String, String> mapping : pathMap.entrySet()) {
			if (mapping.getKey().equalsIgnoreCase("abcmck")) {
				mappings.put("abcmck", getAbcMCK(mapping.getValue(), mapping.getKey()));
			} else if (mapping.getKey().equalsIgnoreCase("wrf")) {
				mappings.putAll(getWRFF(mapping.getValue(), mapping.getKey()));
			} else if (mapping.getKey().equalsIgnoreCase("tuh")) {
				mappings.putAll(getTuh(mapping.getValue(), mapping.getKey()));
			} else if (mapping.getKey().equalsIgnoreCase("mck")) {
				mappings.put("mck", getMCK(mapping.getValue(), mapping.getKey()));
			} else {
				// -- nothing yet
			}
		}
	}

	Map<String, List<SchemaDef>> getWRFF(final String path, final String type) {
		java.nio.file.Path file = java.nio.file.Paths.get(path);
		Map<String, List<SchemaDef>> schemaDefs = new HashMap<String, List<SchemaDef>>();

		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			List<String> dups = new ArrayList<>();
			int dupId = 1;
			String line = null;
			String recordType = null;
			List<SchemaDef> list = new LinkedList<>();
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.startsWith("--") || line.length() == 0 || line.startsWith("INPUT")
						|| line.startsWith("Key Field"))) {
					if (line.startsWith("Record") && line.indexOf("Tag") != -1) {
						recordType = line.substring(line.indexOf("Tag") + 3, line.indexOf(" at ")).trim()
								.replaceAll(":", "_").replaceAll("\\*", "");

					} else {

						if (recordType.trim().equals(""))
							continue;

						SchemaDef schemaDef = new SchemaDef();
						String[] def = line.split("\\s+");

						String name = recordType + ":" + def[0].trim().replaceAll(":", "_").replaceAll("\\*", "");

						if (dups.contains(name)) {
							name = name + dupId;
							dupId++;
						}

						dups.add(name);

						schemaDef.setName(name);
						schemaDef.setStart(def[3]);
						schemaDef.setValue(def[4].trim());
						schemaDef.setFixedWidth(true);
						schemaDef.setStart(def[6]);
						schemaDef.setEnd(def[6]);
						schemaDef.setWidth(Integer.parseInt(def[7]));
						schemaDef.setType("wrf");
						list.add(schemaDef);

					}

					schemaDefs.put(type, list);
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

		return schemaDefs;

	}

	Map<String, List<SchemaDef>> getTuh(final String path, final String type) {

		java.nio.file.Path file = java.nio.file.Paths.get(path);
		Map<String, List<SchemaDef>> schemaDefs = new HashMap<String, List<SchemaDef>>();
		List<SchemaDef> list = new LinkedList<>();
		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			String recordType = null;

			while ((line = reader.readLine()) != null) {
				SchemaDef schemaDef = new SchemaDef();
				line = line.trim();
				if (line.startsWith("Record") && line.endsWith("1")) {
					recordType = getTextBetweenTwoWords("Tag", "at", line.trim()).trim();
				} else {
					if (!(line.startsWith("Header") || line.startsWith("INPUT") || line.startsWith("--")
							|| line.length() == 0)) {
						line = line.replaceAll("\\*", "").replaceAll(":", "_");
						String[] def = line.split("\\s+");
						schemaDef.setName(recordType + ":" + def[0].trim().replaceAll(":", "_").replaceAll("\\*", ""));
						schemaDef.setValue(def[4]);
						schemaDef.setStart(def[6]);
						schemaDef.setEnd((def[6]));
						schemaDef.setWidth(Integer.parseInt(def[3]));
						schemaDef.setFixedWidth(true);
						schemaDef.setType(type);
						list.add(schemaDef);

						// if (schemaDefs.containsKey(recordType)) {
						// schemaDefs.get(recordType).add(schemaDef);
						// } else {
						// List<SchemaDef> list = new LinkedList<>();
						// list.add(schemaDef);
						// schemaDefs.put(recordType, list);
						// }
					}

					schemaDefs.put(type, list);
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

		return schemaDefs;
	}

	List<SchemaDef> getAbcMCK(final String path, final String type) {
		java.nio.file.Path file = java.nio.file.Paths.get(path);
		List<SchemaDef> list = new LinkedList<>();

		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.contains("Data*")) {
					break;
				}
			}

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.startsWith("--") || line.startsWith("INPUT") || line.startsWith("PHARM_HEADER")
						|| line.startsWith("HEADER_TAG") || line.startsWith("SENDERID") || line.startsWith("Header")
						|| line.startsWith("Data") || line.contains(":2") || line.length() == 0)) {
					line = line.replaceAll("\\*", "").replaceAll(":", "_");
					SchemaDef schemaDef = new SchemaDef();
					String[] def = line.split("\\s+");
					schemaDef.setName(def[0].replaceAll("#", "_"));
					schemaDef.setValue(def[4]);
					schemaDef.setType(type);
					list.add(schemaDef);
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

		return list;
	}

	List<SchemaDef> getMCK(final String path, final String type) {
		java.nio.file.Path file = java.nio.file.Paths.get(path);
		List<SchemaDef> list = new LinkedList<>();

		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.startsWith("--") || line.startsWith("INPUT") || line.startsWith("DataRecords")
						|| line.length() == 0)) {
					line = line.replaceAll("\\*", "").replaceAll(":", "_");
					SchemaDef schemaDef = new SchemaDef();
					String[] def = line.split("\\s+");
					schemaDef.setName(def[0].replaceAll("#", "_"));
					schemaDef.setValue(def[4]);
					schemaDef.setType(type);
					list.add(schemaDef);
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

		return list;
	}

	String getTextBetweenTwoWords(String firstWord, String secondword, String text) {
		return text.substring(text.indexOf(firstWord) + firstWord.length(), text.indexOf(secondword));
	}

	public Map<String, List<SchemaDef>> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, List<SchemaDef>> mappings) {
		this.mappings = mappings;
	}

	public static void main(String[] args) {

		java.nio.file.Path file = java.nio.file.Paths.get("/vbox/data/pocdata/schema/WRFFileStructure.TXT");
		String line1 = "DETAIL1    0000004BX0000003.840000VCDYND60606           IN1192                                      MGDYND60606                                           FAIRWAY 90MM                                                               ";
		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			List<String> dups = new ArrayList<>();
			int dupId = 1;
			String line = null;
			String recordType = null;
			List<SchemaDef> list = new LinkedList<>();
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.startsWith("--") || line.length() == 0 || line.startsWith("INPUT")
						|| line.startsWith("Key Field"))) {
					if (line.startsWith("Record") && line.indexOf("Tag") != -1) {
						recordType = line.substring(line.indexOf("Tag") + 3, line.indexOf(" at ")).trim()
								.replaceAll(":", "_").replaceAll("\\*", "");

					} else {

						if (recordType.trim().equals(""))
							continue;

						SchemaDef schemaDef = new SchemaDef();
						String[] def = line.split("\\s+");

						String name = recordType + ":" + def[0].trim().replaceAll(":", "_").replaceAll("\\*", "");

						if (dups.contains(name)) {
							name = name + dupId;
							dupId++;
						}

						dups.add(name);

						schemaDef.setName(name);
						schemaDef.setStart(def[3]);
						schemaDef.setValue(def[4].trim());
						schemaDef.setFixedWidth(true);
						schemaDef.setStart(def[6]);
						schemaDef.setEnd(def[6]);
						schemaDef.setWidth(Integer.parseInt(def[7]));
						schemaDef.setType("wrf");
						list.add(schemaDef);
						int start = Integer.parseInt(def[6]);
						int width = Integer.parseInt(def[7]);

						if (name.startsWith("DETAIL")) {
							// System.out.println(def[6] + " : " + def[7] + " :
							// " + name);
							String line2 = null;
							line2 = line1.substring(start - 1, start + width).trim();
							System.out.println("\t" + start + " :" + (start + width) + " : " + line2);
						}
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	@Override
	public void parse(String filePath) throws Exception {

	}
}
