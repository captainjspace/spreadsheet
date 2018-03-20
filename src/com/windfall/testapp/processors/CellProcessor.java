package com.windfall.testapp.processors;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


import com.windfall.testapp.exception.CircularReferenceException;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.ProcessorEvalResults;

/**
 * CellProcessor - handles expression evaluation, reference resolution
 */
public class CellProcessor {

	private static final Logger LOG = Logger.getLogger(CellProcessor.class.getName());
	
	public static ProcessorEvalResults eval (CellData cd) {
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
	 * Expression Eval - I pulled this code from somewhere and modified it 2 years ago
	 * I last used in in ParseUtils in my dmpro project to calculate die rolls.
	 * for this project i've try/catch it - so that a failed eval can lead to another resolve pass
	 */
	public static double eval (final String s) {
		try {
			return new Object() {

				int pos = -1, ch;

				//increment position
				void nextChar() {
					ch =  ( ++pos < s.length() )  ? s.charAt(pos) : -1; //end
				}

				//eat the next character
				boolean eat(int charToEat) {
					while (ch == ' ') nextChar();
					if ( ch == charToEat ) {
						nextChar();
						return true; //got what I was looking and current position in string is good 
					}
					return false; // move to next test
				}

				//parse op
				double parse() {
					nextChar();
					double x = parseExpression(); 
					//if you get back here before the end of the string - something is wrong.
					if (pos < s.length()) throw new RuntimeException ("Unexpected error - did not get to end of string" + (char)ch);
					return x;
				}

				// Grammar:
				// expression = term | expression `+` term | expression `-` term
				// term = factor | term `*` factor | term `/` factor
				// factor = `+` factor | `-` factor | `(` expression `)`
				//        | number | functionName factor | factor `^` factor

				//expressions are + -
				double parseExpression() {
					double x = parseTerm();
					for (;;) {
						if ( eat('+') ) x += parseTerm();
						else if ( eat('-') ) x -= parseTerm();
						else return x;
					}
				}

				//terms will either be / * or a deeper factor
				double parseTerm() {
					double x = parseFactor();
					for (;;) {
						if ( eat('/')) x /= parseFactor();
						else if ( eat('*')) x *= parseFactor();
						else return x;
					}
				}

				double parseFactor() {
					//eventually this happens...
					if (eat('+')) return parseFactor(); // unary plus
					if (eat('-')) return -parseFactor(); // unary minus

					double x;
					int startPos = this.pos;

					if (eat('(')) { // double back for parentheses
						x = parseExpression();
						eat(')');
					} else if ((ch >= '0' && ch <= '9') || ch == '.') { 
						// while in numbers just keep going...
						while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
						//not in a number any number so parse what we have.
						x = Double.parseDouble(s.substring(startPos, this.pos));
					} else {
						throw new RuntimeException("Unexpected error " + (char)ch);
					}
					return x;
				}
			}.parse(); 
		} catch (Exception e) {
			LOG.info(String.format("Expression Error: %s%n, s"));
			return -999999999; //marker 
		}
	}

	/**
	 * Resolve References
	 */
	public CellData resolveReferences(CellData cell, CSVMap csvMap) throws CircularReferenceException {
		
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
			if ( csvMap.getCsvMap().containsKey(s) ) {
				int r = csvMap.getCsvMap().get(s).evalRefStackCount; //get the referenced stackcoutn
				parentRef = (r>parentRef)? r :parentRef; //imperfect but pick largest stack
				refCount++;
				LOG.info(String.format("Getting Cell: %s%n",s));
				//this is the substitution
				formula.append("(").append(csvMap.getCsvMap().get(s).evaluatedFormula).append(")");
			} else formula.append(s); //or just put the non key back in place
		}
		
		//update cell data
		cell.evaluatedFormula=formula.toString();
		cell.evalRefCount += refCount; //count what we replaced this round
		if (refCount>0) cell.evalRefStackCount+=((parentRef>0)?parentRef:1);
		
		/* REPLACE THE CELL IN THE MAP */
		csvMap.getCsvMap().put(cell.s_idx, cell); 
		return cell; //for caller.
	}
}