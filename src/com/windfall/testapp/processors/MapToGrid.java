package com.windfall.testapp.processors;

import java.util.Arrays;

import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.FileStats;

/**
 * convert CSVMap to Grid
 * @author joshualandman
 *
 */
public class MapToGrid {
	
	String[][] grid;

	public MapToGrid(FileStats fs) {
		grid = new String[fs.rowCount][fs.maxFieldsInRow];
	}

	/** 
	 * Maps calculated values / non-calc'd values to cells
	 * @param csvMap celldata
	 * @return string array - uninit will NPE
	 */
	public String[][] mapToGrid(CSVMap csvMap) {
		csvMap.getCsvMap().entrySet().stream().forEach( e-> {
			CellData cd = e.getValue();
			grid[cd.r-1][cd.c-1] = String.format("%.2f", (cd.calculated) ? cd.evaluatedValue:0) ;
				 //String.format("%.2f", cd.evaluatedValue) : cd.text; //to output text when not eval'd
		});
		return grid;
	}
	
	/** 
	 * output CSV
	 * @return
	 */
	public String getCSVOutput() {
		StringBuilder data=new StringBuilder();
		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length; j++) data.append(grid[i][j]).append(",");
			data.setLength(data.length()-1); //remove trailing comma
			data.append("\n"); //return
		}
		return data.toString();
	}
	
	@Override
	public String toString() {
		return "MapToGrid [grid=" + Arrays.toString(grid) + "]";
	}
}
