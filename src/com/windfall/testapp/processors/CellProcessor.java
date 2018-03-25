package com.windfall.testapp.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;




import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.ProcessorEvalResults;

/**
 * CellProcessor - handles expression evaluation, reference resolution
 */
public class CellProcessor {

	private static final Logger LOG = Logger.getLogger(CellProcessor.class.getName());
	
	private ScriptEngineManager mgr = new ScriptEngineManager();
	private ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	public ProcessorEvalResults eval (CellData cd) {
		ProcessorEvalResults per = new ProcessorEvalResults();
		try {
			per.startFormula = cd.evaluatedFormula;
			per.complete = false;
			per.evaluatedValue = eval(per.startFormula);
			per.complete = true;
			per.endFormula = per.startFormula;
		} catch (Exception e) {
			//catch failed eval
			LOG.fine(String.format("Cell still has references:%n%-10s%-10s", per.startFormula, cd.s_idx));
		}
		
		return per;
	}

	/**
	 * The laziest eval ever ...
	 * @param s celltext
	 * @return double on success
	 * @throws ScriptException
	 */
	public double eval (final String s) throws ScriptException {
		return Double.parseDouble(engine.eval(s).toString());
	} 
	
	/**
	 * Resolve References
	 */
	public void resolveReferences(CellData cell, Map<String,CellData> csvMap) throws CircularReferenceException {
		
		//split on expressions, factors, parents
		List<String> elements = Arrays.asList(cell.evaluatedFormula.trim().split("(?<=[-+*/()])|(?=[-+*/()])"));
		LOG.fine(cell.formatCellData());
		
		//formula assembly - we'll resolve first tier replacements
		//because of the randomized nature of putting cells to hashkeys
		//this is reasonably efficient - not as efficient as chasing a reference to the end and replacing the wholechain though
		StringBuilder formula = new StringBuilder();
		int refCount=0, parentRef=0; //these are for internal use
		
		//check to see if any elements are self referencing.
		for (String s : elements) {
			if (s.toUpperCase().equals(cell.s_idx)) {
				String msg = String.format("Circular Reference Exception: %nGetting Cell:%s%n%s5n", s, cell.formatCellData());
				LOG.severe(msg);
	        	throw new CircularReferenceException(msg);
			}
			//check to see if element is in the map
			if ( csvMap.containsKey(s) ) {
				int r = csvMap.get(s).evalRefStackCount; //get the referenced stackcoutn
				parentRef = Math.max(r,parentRef); //imperfect but pick largest stack
				refCount++;
				LOG.info(String.format("Getting Cell: %s%n",s));
				//this is the substitution
				formula.append("(").append(csvMap.get(s).evaluatedFormula).append(")");
			} else formula.append(s); //or just put the non key back in place
		}
		
		//update cell data
		cell.evaluatedFormula=formula.toString();
		cell.evalRefCount += refCount; //count what we replaced this round
		if (refCount>0) {
			cell.evalRefStackCount+=(Math.max(parentRef,1));
		}
		
	}
}