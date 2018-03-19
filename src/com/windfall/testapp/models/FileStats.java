package com.windfall.testapp.models;

/**
 * Input File stats
 * @author joshualandman
 *
 */
public class FileStats {
	
	public String path="";
	public int rowCount=0;
	public long cellCount=0;
	public int maxFieldsInRow=0;
	public boolean allRowsHaveSameFieldCount=false;
	
	public String getStats() {
		String fmt = "%n%s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%s";
		String[] keys = {"File Stats:","Path:","Row Count:", "Cell Count:","Max/Expected Field Count:","AllRowsHaveSameFieldCount:"};
		return String.format(fmt, 
				keys[0],
				keys[1],this.path,
				keys[2],this.rowCount,
				keys[3],this.cellCount,
				keys[4],this.maxFieldsInRow,
				keys[5],this.allRowsHaveSameFieldCount);
	}

}
