package com.windfall.testapp.models;
import java.util.Map;
import java.util.HashMap;

/**
 * Wrapper for csvMap state
 * @author joshualandman
 *
 */
public class CSVMap {
	
	private Map<String, CellData> csvMap = new HashMap<>();

	public Map<String, CellData> getCsvMap() {
		return csvMap;
	}

	public void setCsvMap(Map<String, CellData> csvMap) {
		this.csvMap = csvMap;
	}
	
	public void dump() {
		csvMap.entrySet().stream().forEach((e) -> {
			System.out.printf("ID: %5s%n%s%n", e.getKey(),e.getValue().toString());
		});
	}
	
}
