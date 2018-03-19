package com.windfall.testapp.processors;

import com.windfall.testapp.models.CellData;

public class IndexToSpeadsheetLocationMapper {
	

	/**
	 * Convenience function for cleaner use
	 * @param cd cell data containing location data
	 * @return cellData with spreadsheet location added
	 */
	public CellData getCellReference (final CellData cd) {
		CellData _cd = cd;
		_cd.s_idx=getCellReference(cd.r,cd.c);
		return _cd;
	}
	
	/**
	 * Creates spreadsheet location from r,col
	 * @param r row
	 * @param col col
	 * @return string representing cell
	 */
	public String getCellReference(int r, int col) {
		
		String[] result = new String[col];
		String colName = "";

		//build stack
		for(int i = 0; i < col; i++) {

			char c = (char)('A' + (i % 26));
			colName = c + "";
			if(i > 25){
				colName =  result[(i / 26) - 1] + "" + c;
			}
			result[i] = colName;
		}
		//last entry + row
		return colName + r ;
	}
}
