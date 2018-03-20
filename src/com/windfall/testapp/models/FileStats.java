package com.windfall.testapp.models;

/**
 * Input File stats
 * @author joshualandman
 *
 */
public class FileStats {
	
	public String path="";
	public long size=-1;
	public int rowCount=0;
	public long cellCount=0;
	public int maxFieldsInRow=0;
	public boolean allRowsHaveSameFieldCount=false;
	
	public String getStats() {
		String fmt = "%n%s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%s";
		String[] keys = {"File Stats:","Path:","File Size:","Row Count:", "Cell Count:","Max/Expected Field Count:","AllRowsHaveSameFieldCount:"};
		return String.format(fmt, 
				keys[0],
				keys[1],this.path,
				keys[2],this.size,
				keys[3],this.rowCount,
				keys[4],this.cellCount,
				keys[5],this.maxFieldsInRow,
				keys[6],this.allRowsHaveSameFieldCount);
	}

}
