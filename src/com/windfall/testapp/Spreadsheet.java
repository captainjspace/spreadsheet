package com.windfall.testapp;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.io.CSVFileReader;
import com.windfall.testapp.io.CSVFileWriter;
import com.windfall.testapp.models.*;
import com.windfall.testapp.processors.CSVMapProcessor;
import com.windfall.testapp.processors.MapToGrid;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spreadsheet eval tool -reads input CSV, resolves references in spreadsheet notation, evaluates cells
 * outputs CSV in floats
 * replaces all non expressions with 0.00
 * Exits on Exceptions
 * @author joshualandman
 *
 */
public class Spreadsheet {

	private static final Logger LOG = Logger.getLogger(Spreadsheet.class.getName());

	//APP IDENTIFICATION
	private static final String NAME = "SPREADSHEET";
	private static final String VERSION = "1.0.0.";

	/**
	 * isolated for exceptions and testing, reference to Reader unnecessary
	 * @param p path
	 * @return CSVFileReaderOutputObjects
	 * @throws Exception
	 */
	public CSVFileReaderOutputObjects processCSVFile(Path p) throws Exception {
		return new CSVFileReader().csvToMap(p);
	}

	/* test entry only */
	public CSVMap getCSVMap (Path p) throws Exception {
		return processCSVFile(p).csvMap;
	}

	/* default no args run */
	public void run() throws Exception {
		CsvTestFiles file = CsvTestFiles.MORE_REFERENCES;
		run(file.path());
	}

	/* args multiple files */
	public void run(String ...args) throws Exception {
		if (args==null||args.length==0) run();
		// loop args
		for (String path : args) {
			run(Paths.get(path));
			LOG.info("END: " + path);
		}
	}

	/* main runner  */
	public void run(Path p) throws Exception {

		//parse input returns (FileStats, CSVMap)
		CSVFileReaderOutputObjects parserOutput = processCSVFile(p);
		
		//process CSVMap CellData
		CSVMapProcessor mapProcessor = new CSVMapProcessor();
		mapProcessor.processMap(parserOutput.csvMap);
		
		//initialize map to grid - uses FileStats name, r, c
		MapToGrid mtg = new MapToGrid(parserOutput.fileStats);
		
		//map the "CSVMap" to the grid[][]
		mtg.mapToGrid(parserOutput.csvMap);
		
		//write the grid to file
		CSVFileWriter writer = new CSVFileWriter(p);
		writer.write(mtg.getCSVOutput());
		return;
	}


	/**
	 * App Main entry 
	 * @param args, list of valid file paths.  
	 * @throws FieldCountMismatchException
	 */
	public static void main(String[] args) throws Exception {
		//start 
		long start = System.currentTimeMillis(); 
		
		//initialize logging
		LoggingConfig lc=new LoggingConfig();
		LOG.info(String.format("%n\t%-20s%s%n\t%-20s%s", "Name", NAME, "Version", VERSION));
		lc.init();
		
		//initialize spreadsheet
		Spreadsheet s = new Spreadsheet();
		try { 
		  s.run(args);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		
		//log exection time
		LOG.info(String.format("Execution Time: %.2f%n",(System.currentTimeMillis()-start)/1000.0));
	}

}

