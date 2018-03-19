package com.windfall.testapp.models;

/**
 * Input File stats
 * @author joshualandman
 *
 */
public class FileStats {
	public int rowCount=0;
	public long cellCount=0;
	public int maxFieldsInRow=0;
	public boolean allRowsHaveSameFieldCount=false;
	
	private String format= "RowCount: %-10d%nCellCount: %-10d%nMax Field Count%-10d%nAllRowsHaveSameFieldCount: %s%n";
	
	public String getStats() {
		return String.format(format, rowCount,cellCount,maxFieldsInRow,allRowsHaveSameFieldCount);
	}

}
