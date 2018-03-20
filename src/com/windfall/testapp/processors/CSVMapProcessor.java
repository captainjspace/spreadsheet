package com.windfall.testapp.processors;


import java.util.Map;
import java.util.logging.Logger;

import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.ProcessorEvalResults;

/**
 * Driver for map to resolve cell references and evaluate expressions
 * @author joshualandman
 *
 */
public class CSVMapProcessor {

	private static final Logger LOG = Logger.getLogger(CSVMapProcessor.class.getName());
	
	private CellProcessor cp = new CellProcessor();

	/**
	 * loop resolving references and evaluating expressions until loop leaves same calc'd count as last time
	 * can add additional counter check to break
	 * usually resolves in 2 or 3
	 * could replace with a chase the reference recursion for maybe slightly better efficiency 
	 * @param csvMap mapa of cell data
	 * @throws CircularReferenceException
	 */
	public void processMap(final CSVMap csvMap) throws CircularReferenceException{

		//recursion or loops ...
		int counter=0;
		//all cells on first pass
		long k, j = csvMap.getCsvMap().entrySet().stream().filter(e -> !e.getValue().calculated).count();
		
		do {
			++counter;
			LOG.info(String.format("Map resolve/eval loop #%d:  #%d still uncalc'd",counter, j));
			resolveMap(csvMap);
			evalMap(csvMap);
			//check calc
			k=csvMap.getCsvMap().entrySet().stream().filter(e -> !e.getValue().calculated).count();
			if (j==k)  break; //abort if we're no longer calculating
			j=k; 
		} while (j > 0);
		LOG.info( String.format("Resolved in %d loops%nConverting %d cells to 0.00", counter, k));
	}

	/**
	 * Look up non calculated cell refs
	 * @param csvMap wrapper around map of cell data
	 * @throws CircularReferenceException
	 */
	public void resolveMap(CSVMap csvMap) throws CircularReferenceException{

		//private copy
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

		csvMap.setCsvMap(_csvMap); //map update
		return;
	}

	/**
	 * evaluate expressions for non-calculated cells
	 * @param csvMap wrapper around cell data
	 * @return the wrapper
	 */
	public CSVMap evalMap(CSVMap csvMap) {

		Map<String,CellData> _csvMap = csvMap.getCsvMap();

		_csvMap.entrySet().stream().filter(e -> !e.getValue().calculated).forEach( cell  -> {
			ProcessorEvalResults evalResults = CellProcessor.eval(cell.getValue());
			LOG.fine(evalResults.dump());
			cell.getValue().evaluatedValue = evalResults.evaluatedValue;
			if (evalResults.complete) {
				cell.getValue().calculated =true;
				LOG.fine(String.format("Cell: %s calculated", cell.getValue().evaluatedValue));
				LOG.info(cell.getValue().formatCellData());
			}
		});

		return csvMap;
	}
}
