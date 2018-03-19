package com.windfall.testapp.processors;


import java.util.Map;
import java.util.logging.Logger;

import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.ProcessorEvalResults;

public class CSVMapProcessor {

	private static final Logger LOG = Logger.getLogger(CSVMapProcessor.class.getName());
	
	private CellProcessor cp = new CellProcessor();

	public void processMap(final CSVMap csvMap) throws CircularReferenceException{

		//recursion or loops ... hmmm
		int counter=1;
		long j = csvMap.getCsvMap().entrySet().stream().filter(e -> !e.getValue().calculated).count();
		long k;
		do {
			LOG.info(String.format("Map resolve - eval loop %d%n, %d still uncalc'd",counter, j));
			resolveMap(csvMap);
			evalMap(csvMap);
			k=csvMap.getCsvMap().entrySet().stream().filter(e -> !e.getValue().calculated).count();
			if (j==k || ++counter>=7)  break;
			j=k;
		}
		while (j > 0);
		LOG.info( String.format("Resolved in %d loops%n", counter-1));
	}

	public void resolveMap(CSVMap csvMap) throws CircularReferenceException{

		Map<String,CellData> _csvMap = csvMap.getCsvMap();

		_csvMap.entrySet().stream().filter(e->!e.getValue().calculated).forEach( cell -> {
			CellData cd = cell.getValue();
			try {
			  cd = cp.resolveReferences(cd, csvMap);
			} catch (CircularReferenceException c) {
				//LOG.warning(cd.formatCellData());
				throw c;
			}
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
			LOG.fine(evalResults.dump());
			cell.getValue().evaluted_value = evalResults.evaluatedValue;
			if (evalResults.complete) {
				cell.getValue().calculated =true;
				LOG.info(String.format("Cell: %s calculated", cell.getValue().evaluted_value));
				LOG.info(cell.getValue().formatCellData());
			}
		});


		return csvMap;
	}

}
