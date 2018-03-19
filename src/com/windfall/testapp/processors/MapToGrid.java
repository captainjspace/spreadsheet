package com.windfall.testapp.processors;

import java.util.Arrays;

import com.windfall.testapp.models.CSVFileParserOutput;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;

public class MapToGrid {
	
	String[][] grid;

	@Override
	public String toString() {
		return "MapToGrid [grid=" + Arrays.toString(grid) + "]";
	}

	public MapToGrid(CSVFileParserOutput cfpo) {
		grid = new String[cfpo.fileStats.rowCount][cfpo.fileStats.maxFieldsInRow];
	}

	//Maps calculated values / non-calc'd values to cells
	public String[][] mapToGrid(CSVMap csvMap) {
		csvMap.getCsvMap().entrySet().stream().forEach( e-> {
			CellData cd = e.getValue();
			grid[cd.r-1][cd.c-1] = (cd.calculated) ?
				 String.format("%.2f", cd.evaluted_value) : cd.text;
		});
		return grid;
	}
	
	public String dump() {
		StringBuilder data=new StringBuilder();
		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length; j++) {
				data.append(grid[i][j]).append(",");
			}
			data.setLength(data.length()-1);
			data.append("\n");
		}
		return data.toString();
	}
	
}
