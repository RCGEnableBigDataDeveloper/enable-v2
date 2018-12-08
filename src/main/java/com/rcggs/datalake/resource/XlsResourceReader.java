package com.rcggs.datalake.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

public abstract class XlsResourceReader<T> extends ResourceReader<T> {

	HSSFWorkbook workbook;
	FormulaEvaluator evaluator;

	List<List<SimpleImmutableEntry<?, ?>>> getRowList(final HSSFSheet sheet) {
		return getRowList(sheet, 0, 0);
	}

	List<List<SimpleImmutableEntry<?, ?>>> getRowList(final HSSFSheet sheet, final int startRow) {
		return getRowList(sheet, startRow, 0);
	}

	public HSSFSheet getSheet(final String fileName, final int sheetIndex) {
		try {
			return getSheet(new FileInputStream(new File(fileName)), sheetIndex);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HSSFSheet getSheet(final String fileName, final String sheetName) {
		try {
			return getSheet(new FileInputStream(new File(fileName)), sheetName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HSSFSheet getSheet(final InputStream file, final int sheetIndex) {
		return getWorkbook(file).getSheetAt(sheetIndex);
	}

	public HSSFSheet getSheet(final InputStream file, final String sheetName) {
		return getWorkbook(file).getSheet(sheetName);
	}

	HSSFWorkbook getWorkbook(final InputStream file) {
		try {
			workbook = new HSSFWorkbook(file);
			evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			return workbook;

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected List<List<SimpleImmutableEntry<?, ?>>> getRowList(final HSSFSheet sheet, final int startRow, final int headerRow) {

		List<List<SimpleImmutableEntry<?, ?>>> rowsList = new LinkedList<List<SimpleImmutableEntry<?, ?>>>();
		Iterator<Row> rowIterator = sheet.iterator();

		for (int i = 0; i < startRow; i++)
			rowIterator.next();

		while (rowIterator.hasNext()) {
			List<SimpleImmutableEntry<?, ?>> rowList = new LinkedList<SimpleImmutableEntry<?, ?>>();
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int columnIndex = 0;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String header = sheet.getRow(headerRow).getCell(columnIndex).getStringCellValue();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					rowList.add(new AbstractMap.SimpleImmutableEntry<>(header, cell.getBooleanCellValue()));
					break;
				case Cell.CELL_TYPE_NUMERIC:
					rowList.add(new AbstractMap.SimpleImmutableEntry<>(header, cell.getNumericCellValue()));
					break;
				case Cell.CELL_TYPE_STRING:
					rowList.add(new AbstractMap.SimpleImmutableEntry<>(header, cell.getStringCellValue()));
					break;
				case Cell.CELL_TYPE_FORMULA:
					String eval = evaluator.evaluateInCell(cell).toString();
					rowList.add(new AbstractMap.SimpleImmutableEntry<>(header, eval));
					break;
				}

				columnIndex++;
			}

			rowsList.add(rowList);
		}

		return rowsList;
	}
}
