package com.rcggs.datalake.connect.fs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jackson.map.ObjectMapper;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

public class LocalFileSystemConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	ConnectionConfig config;

	public LocalFileSystemConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {
		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);
		
		
		
		
		
	
		
		Files.walkFileTree(FileSystems.getDefault().getPath(config.getPath()), new FileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				ItemDefinition idef = new ItemDefinition();
				idef.setName((dir.getParent().toString() + "/" + dir.getFileName().toString()).replaceAll("\\\\", "/"));
				idef.setItemType("folder");
				idef.setData(data);
				map.put(idef, new ArrayList<ItemDefinition>());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

				String filename = file.getFileName().toString();
				ItemDefinition cd = new ItemDefinition();
				cd.setName(filename);
				BufferedReader br = null;

				try {
					if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {
						cd.setSchema(getSchema(file));
					} else {
						br = new BufferedReader(new FileReader(file.toFile()));
						String text = br.readLine();
						if (text != null) {
							cd.setType(text);
						} else {
							cd.setType("file");
						}
					}
					cd.setItemType("file");
					String f = file.toString();
					cd.setParent(f.substring(0, f.lastIndexOf(System.getProperty("file.separator")) + 1));
					cd.setData(data);
					
					
					
					
					for (Map.Entry<ItemDefinition, List<ItemDefinition>> defs : map.entrySet()) {
						
							
						//if (defs.getKey().getName().equals(file.getParent().toString())) {
							
							defs.getValue().add(cd);
						//}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});

		return map;
	}

	List<SchemaDef> getSchema(final Path file) {

		List<SchemaDef> schemas = new ArrayList<SchemaDef>();
		HSSFWorkbook workbook = null;
		FileInputStream xlsfile;
		try {
			xlsfile = new FileInputStream(file.toFile());

			workbook = new HSSFWorkbook(xlsfile);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			List<String> headerRows = new ArrayList<String>();
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				headerRows.add(cell.getStringCellValue());
				ItemDefinition columnDef = new ItemDefinition();
				columnDef.setName(cell.getStringCellValue());
				columnDef.setItemType("column");
				columnDefs.add(columnDef);
			}

			row = rowIterator.next();
			cellIterator = row.cellIterator();

			List<String> dataRows = new ArrayList<String>();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int type = cell.getCellType();

				switch (type) {
				case 0:
					dataRows.add("NUMERIC");
					break;
				case 1:
					dataRows.add("STRING");
					break;
				default:
					dataRows.add("STRING");
				}
			}

			for (int i = 0; i < dataRows.size(); i++) {
				SchemaDef schemaDef = new SchemaDef();
				schemaDef.setName(columnDefs.get(i).getName());
				schemaDef.setValue(dataRows.get(i));
				schemaDef.setChecked(true);
				schemas.add(schemaDef);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return schemas;
	}
}
