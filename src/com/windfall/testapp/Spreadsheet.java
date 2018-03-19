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
	private static final String NAME = "SPREADSHEET";
	private static final String VERSION = "1.0.0.";

	public CSVFileParserOutput processCSVFile(Path p) {
		CSVFileParser fp = new CSVFileParser();
		CSVFileParserOutput cfpo=null;
		try {
			cfpo= fp.csvToMap(p);
		} catch (IOException | FieldCountMismatchException e) {
			logger.severe(e.getMessage());
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
		long start = System.currentTimeMillis();
		//initialize logging
		LoggingConfig lc=new LoggingConfig();
		Logger.getGlobal().info(String.format("%n%-20s%s%n%-20s%s", "Name", NAME, "Version", VERSION));
		lc.init();
		//initialise spreadsheet
		Spreadsheet s = new Spreadsheet();
		s.run();
		Logger.getGlobal().info(String.format("Execution Time: %.2f%n",(System.currentTimeMillis()-start)/1000.0));
	}

}

