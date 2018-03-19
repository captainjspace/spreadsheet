package com.windfall.testapp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.io.CSVFileParser;
import com.windfall.testapp.io.CSVFileWriter;
import com.windfall.testapp.models.*;
import com.windfall.testapp.processors.CSVMapProcessor;
import com.windfall.testapp.processors.MapToGrid;


import java.util.logging.Logger;


public class Spreadsheet {

	private static final Logger LOG = Logger.getLogger(Spreadsheet.class.getName());

	//APP IDENTIFICATION
	private static final String NAME = "SPREADSHEET";
	private static final String VERSION = "1.0.0.";

	public CSVFileParserOutput processCSVFile(Path p) throws IOException, FieldCountMismatchException {
		CSVFileParser fp = new CSVFileParser();
		CSVFileParserOutput cfpo=null;
		try {
			cfpo= fp.csvToMap(p);
		} catch (IOException | FieldCountMismatchException e) {
			LOG.severe(e.getMessage());
			throw e;
		}
		return cfpo;	
	}

	public CSVMap getCSVMap (Path p) throws IOException, FieldCountMismatchException {
		return processCSVFile(p).csvMap;
	}

	/* default */
	public void run(){
		CsvTestFiles file = CsvTestFiles.MORE_REFERENCES;
		run(file.path());
	}

	/* args */
	public void run(String ...args){

		if (args==null||args.length==0) run();
		for (String path : args) {
			run(Paths.get(path));
		}
	}

	/* main execution */
	public void run(Path p) {

		CSVFileParserOutput cfpo = null;

		try {
			cfpo = this.processCSVFile(p);
		} catch (IOException | FieldCountMismatchException e1) {
			//LOG.severe(e1.getMessage());
			return;
		}

		if (cfpo == null) return;
		CSVMapProcessor mapProcessor = new CSVMapProcessor();
		try {
			mapProcessor.processMap(cfpo.csvMap);
		} catch (CircularReferenceException e) {
			LOG.warning(e.getMessage());
			return;
		}

		MapToGrid mtg = new MapToGrid(cfpo);
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
		s.run(args);
		Logger.getGlobal().info(String.format("Execution Time: %.2f%n",(System.currentTimeMillis()-start)/1000.0));
	}

}

