package com.windfall.testapp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.io.CSVFileParser;
import com.windfall.testapp.io.CSVFileWriter;
import com.windfall.testapp.models.*;
import com.windfall.testapp.processors.CSVMapProcessor;
import com.windfall.testapp.processors.MapToGrid;


import java.util.logging.Logger;


public class Spreadsheet {

	private static final Logger logger = Logger.getLogger(Spreadsheet.class.getName());
	
	//APP IDENTIFICATION
	private static final Map<String,String> P_DATA = new TreeMap<>();
	static {
		P_DATA.put("NAME", "SPREADSHEET");
		P_DATA.put("VERSION", "1.0.0");
	}
	public static Map<String,String> getProgramData() {
		return P_DATA;
	}

	public CSVFileParserOutput processCSVFile(Path p) {
		CSVFileParser fp = new CSVFileParser();
		CSVFileParserOutput cfpo=null;
		try {
			cfpo= fp.csvToMap(p);
		} catch (IOException | FieldCountMismatchException e) {
			logger.warning(e.getMessage());
			System.exit(-1); //file should be checked
		}
		return cfpo;	
	}

	public CSVMap getCSVMap (Path p) {
		return processCSVFile(p).csvMap;
	}

	public void run() {
		CSVFILES file = CSVFILES.GENERIC_DATA;
		Spreadsheet s = new Spreadsheet();
		CSVFileParserOutput cfpo = s.processCSVFile(file.path());
		CSVMapProcessor mapProcessor = new CSVMapProcessor();
		try {
			mapProcessor.processMap(cfpo.csvMap);
		} catch (CircularReferenceException e) {
			logger.warning(e.getMessage());
			System.exit(-1); //clean circular references
		}

		MapToGrid mtg = new MapToGrid(cfpo);
		Path p = Paths.get(file.path().toString() + "-output");
		CSVFileWriter writer = new CSVFileWriter(p);
		
		mtg.mapToGrid(cfpo.csvMap);
		writer.write(mtg.dump());
	}


	public static void main(String[] args) throws FieldCountMismatchException {
		logger.info("Spreadsheet Evaluate");
		Spreadsheet s = new Spreadsheet();
		s.run();
	}

}

