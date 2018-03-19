package com.windfall.testapp.models;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Testing enumeration of files
 * @author joshualandman
 *
 */
public enum CsvTestFiles {		
	SIMPLE(Paths.get("resources/csv_input/input.csv")),
	MISMATCH_FIELDS(Paths.get("resources/csv_input/field-count-mismatch.csv")),
	MORE_REFERENCES(Paths.get("resources/csv_input/references-stack.csv")),
	SELF_REFERENCE(Paths.get("resources/csv_input/self-circular-reference.csv")),
	CROSS_REFERENCE(Paths.get("resources/csv_input/cross-circular-reference.csv")),
	GENERIC_DATA(Paths.get("resources/csv_input/datafile.csv")),
	BAD_FILE(Paths.get("resources/csv_input/bad-name.csv"));

	private Path path;
	
	CsvTestFiles(Path p) {
		this.path = p;
	}
	public Path path() {
		return path;
	}
}


