package com.windfall.testapp.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.windfall.testapp.Spreadsheet;
import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.io.CSVFileWriter;
import com.windfall.testapp.models.CsvTestFiles;
import com.windfall.testapp.models.CSVFileReaderOutputObjects;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.processors.CSVMapProcessor;
import com.windfall.testapp.processors.CellProcessor;
import com.windfall.testapp.processors.IndexToSpeadsheetLocationMapper;
import com.windfall.testapp.processors.MapToGrid;

public class test_Spreadsheet {
	final Path path = Paths.get("resources/csv_input/input.csv");

	public static void test_main() {
		String [] args = {};
		try {
			Spreadsheet.main(args);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public static void test_CellProcessor() {
		String [] expr = {
				"10+5",
				"20-30+5",
				"100 + 4 - 56 + 19"
		};
		for (String s: expr) 
			System.out.printf("%s = %.2f%n", s, CellProcessor.eval(s));


	}

	public static void test_CSVMap()  {
		Spreadsheet s = new Spreadsheet();
		CSVMap csvMap;
		try {
			csvMap = s.getCSVMap(CsvTestFiles.SIMPLE.path());
			csvMap.dump();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public static void test_CSVMapProcessor() {
		Spreadsheet s = new Spreadsheet();
		CSVMap csvMap;
		try {
			csvMap = s.getCSVMap(CsvTestFiles.MORE_REFERENCES.path());
			CSVMapProcessor mapProcessor = new CSVMapProcessor();
			mapProcessor.processMap(csvMap);
			csvMap.dump();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void test_MapToGrid() {
		Spreadsheet s = new Spreadsheet();
		try {
			CSVFileReaderOutputObjects cfpo = s.processCSVFile(CsvTestFiles.MORE_REFERENCES.path());
			CSVMapProcessor mapProcessor = new CSVMapProcessor();
			mapProcessor.processMap(cfpo.csvMap);
			MapToGrid mtg = new MapToGrid(cfpo.fileStats);
			mtg.mapToGrid(cfpo.csvMap);
			System.out.println(mtg.getCSVOutput());
			
		} catch (Exception e) {
			System.out.println(e);
		}

		
	}
	
	public static void test_FileWrite() {
		System.out.println("File Write Test");
		Path p = Paths.get(CsvTestFiles.MORE_REFERENCES.path().getParent() + "/test_FileWrite.txt");
		System.out.println(p);
		CSVFileWriter writer = new CSVFileWriter(p);
		writer.write("test");
	}

	public static void test_GridToFile(CsvTestFiles f) {
		System.out.printf("TESTING FILE: %s%n",f.path());
		Spreadsheet s = new Spreadsheet();
		try {
			CSVFileReaderOutputObjects cfpo = s.processCSVFile(f.path());
			CSVMapProcessor mapProcessor = new CSVMapProcessor();
			mapProcessor.processMap(cfpo.csvMap);
			MapToGrid mtg = new MapToGrid(cfpo.fileStats);
			mtg.mapToGrid(cfpo.csvMap);
			Path p = Paths.get(f.path().toString() + "-output");
			CSVFileWriter writer = new CSVFileWriter(p);
			writer.write(mtg.getCSVOutput());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void test_bulkrun() {
		for (CsvTestFiles f : CsvTestFiles.values()) {
			try { 
			  test_GridToFile(f);
			} catch (Exception e) {
				System.out.printf("%s - %s", f.path(), e.getMessage());
			}
		}
	}
	
	public static void test_IndexToSpeadsheetLocationMapper() {
		IndexToSpeadsheetLocationMapper mapper = new IndexToSpeadsheetLocationMapper();
		System.out.println( mapper.getCellReference(10, 10).toString() );
		System.out.println( mapper.getCellReference(40, 40).toString() );
		System.out.println( mapper.getCellReference(100, 100).toString() );
	}
	

	
	public static void main (String[] args) throws Exception {
		
		test_CellProcessor();
		test_CSVMap();
		test_IndexToSpeadsheetLocationMapper();
		test_CSVMapProcessor();
		test_MapToGrid();
		test_FileWrite();
		test_GridToFile(CsvTestFiles.MORE_REFERENCES);
		test_bulkrun();
		test_main();


	}
}
