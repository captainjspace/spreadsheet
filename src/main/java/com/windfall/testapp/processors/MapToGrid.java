package com.windfall.testapp.processors;

import java.util.Arrays;
import java.util.Map;

import com.windfall.testapp.models.CellData;

/**
 * convert CSVMap to Grid
 * @author joshualandman
 *
 */
public class MapToGrid {
	
	String[][] grid;

	public void initGrid(int row, int col) {
		grid = new String[row][col];
	}

	/** 
	 * Maps calculated values / non-calc'd values to cells
	 * @param csvMap celldata
	 * @return string array - uninit will NPE
	 */
	public String[][] mapToGrid(Map<String,CellData> csvMap) {
		csvMap.entrySet().stream().forEach( e-> {
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
