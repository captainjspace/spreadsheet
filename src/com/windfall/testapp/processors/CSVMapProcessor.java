package com.windfall.testapp.processors;


import java.util.Map;
import java.util.logging.Logger;

import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.ProcessorEvalResults;

public class CSVMapProcessor {

	private static final Logger logger = Logger.getGlobal();
	
	private CellProcessor cp = new CellProcessor();

	public void processMap(final CSVMap csvMap) throws CircularReferenceException{

		//recursion or loops ... hmmm
		int counter=1;
		do {
			logger.info(String.format("process Map resolve - eval loop %d%n",counter));
			resolveMap(csvMap);
			evalMap(csvMap);
			if (++counter>=7) break;
		}
		while (csvMap.getCsvMap().entrySet().stream().filter(e -> !e.getValue().calculated).count() > 0);
		logger.info( String.format("Resolved in %d loops%n", counter));
	}

	public void resolveMap(CSVMap csvMap) throws CircularReferenceException{

		Map<String,CellData> _csvMap = csvMap.getCsvMap();

		_csvMap.entrySet().stream().filter(e->!e.getValue().calculated).forEach( cell -> {
			CellData cd = cell.getValue();
			cd = cp.resolveReferences(cd, csvMap);
			cd.resolved=true;
			_csvMap.put(cd.s_idx, cd);
		});

		csvMap.setCsvMap(_csvMap);
		return;
	}

	public CSVMap evalMap(CSVMap csvMap) {

		Map<String,CellData> _csvMap = csvMap.getCsvMap();

		_csvMap.entrySet().stream().filter(e -> !e.getValue().calculated).forEach( cell  -> {
			ProcessorEvalResults evalResults = CellProcessor.eval(cell.getValue());
			logger.info(evalResults.dump());
			cell.getValue().evaluted_value = evalResults.evaluatedValue;
			if (evalResults.complete) {
				cell.getValue().calculated =true;
				logger.info(String.format("Cell: %s calculated", cell.getValue().evaluted_value));
			}
		});


		return csvMap;
	}

}
