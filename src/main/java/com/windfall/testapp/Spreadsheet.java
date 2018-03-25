package com.windfall.testapp;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.io.CSVFileReader;
import com.windfall.testapp.io.CSVFileWriter;
import com.windfall.testapp.models.*;
import com.windfall.testapp.processors.CSVMapProcessor;
import com.windfall.testapp.processors.MapToGrid;

import java.util.Map;
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
	private static final String VERSION = "1.0.1";

	//File Stats
	public int m;
	public int n;
	public int cellCount;
	public boolean allRowsHaveSameFieldCount=false;
	public long size;
	public String path;

	/**
	 * App Main entry 
	 * @param args, list of valid file paths.  
	 * @throws FieldCountMismatchException
	 */
	public static void main(String[] args) throws Exception {
		//start 
		long start = System.currentTimeMillis(); 
		System.err.printf(String.format("%n\t%-20s%s%n\t%-20s%s%n", "Name", NAME, "Version", VERSION));

		//initialize logging
		LoggingConfig lc=new LoggingConfig();
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

	/* default no args run */
	public void run() throws Exception {

		run(Paths.get("src/main/resources/csv_input/_input.csv_"));
	}

	/* args multiple files on command line */
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

		//sending self ref for capturing file stats
		Map<String,CellData> csvMap =  new CSVFileReader().csvToMap(p, this);

		//process map directly
		CSVMapProcessor mapProcessor = new CSVMapProcessor();
		mapProcessor.processMap(csvMap);

		//initialize map to grid, map the "CSVMap" to the grid[][]
		MapToGrid mtg = new MapToGrid();
		mtg.initGrid(this.m,this.n);
		mtg.mapToGrid(csvMap);

		//send CSV to stdout
		System.out.println(mtg.getCSVOutput());

		//write the grid to file
		CSVFileWriter writer = new CSVFileWriter(p);
		writer.write(mtg.getCSVOutput());
	}


	//For Reporting MisMatched Field Counts
	public String getFileStats() {
		String fmt = "%n%s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%s";
		String[] keys = {"File Stats:","Path:","File Size:","Row Count:", "Cell Count:","Max/Expected Field Count:","AllRowsHaveSameFieldCount:"};
		return String.format(fmt, 
				keys[0],
				keys[1],this.path,
				keys[2],this.size,
				keys[3],this.m,
				keys[4],this.cellCount,
				keys[5],this.n,
				keys[6],this.allRowsHaveSameFieldCount);
	}
}

