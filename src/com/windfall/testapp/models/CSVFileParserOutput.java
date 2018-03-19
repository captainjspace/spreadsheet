package com.windfall.testapp.models;

/**
 * Wrapper to ship map and file stats
 * @author joshualandman
 *
 */
public class CSVFileParserOutput {
	public CSVFileParserOutput(FileStats fs, CSVMap csvMap) {
		this.csvMap=csvMap;
		this.fileStats=fs;
	}
	public CSVMap csvMap;
	public FileStats fileStats;
}
