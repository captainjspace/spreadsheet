package com.windfall.testapp.processors;

import java.util.logging.Logger;

import com.windfall.testapp.models.CellData;

public class IndexToSpeadsheetLocationMapper {
	
	private static final Logger LOG = Logger.getLogger(IndexToSpeadsheetLocationMapper.class.getName());
	

	/**
	 * Convenience function for cleaner use - modifying copy and returning
	 * @param cd cell data containing location data
	 * @return cellData with spreadsheet location added
	 */
	public void addCellReference (final CellData cd) {
		cd.s_idx=getCellReference(cd.r,cd.c);
	}
	
	/**
	 * Creates spreadsheet location from r,col.
	 * @param m row
	 * @param n col
	 * @return string representing cell
	 */
	public String getCellReference(int m, int n) {
		
		String[] result = new String[n];
		String colName = "";

		//build stack
		for(int i = 0; i < n; i++) {

			char c = (char)('A' + (i % 26));
			colName = c + "";
			if(i > 25) {
				colName =  result[(i / 26) - 1] + "" + c;
			}
			result[i] = colName;
		}
		//last entry + row
		LOG.fine(String.format("Mapped %d,%d -> %s%d%n", m,n,colName,m));
		return colName + m ;
	}
}
