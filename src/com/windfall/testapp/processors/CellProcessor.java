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
			per.startFormula = cd.evaluated_formula;
			per.complete = false;
			per.evaluatedValue = eval(per.startFormula);
			per.complete = true;
			per.endFormula = per.startFormula;
		} catch (Exception e) {
			LOG.fine(String.format("Cell still has references:%n%-10s%-10s", per.startFormula, cd.s_idx));
		}
		
		return per;
	}
	
	/**
	 * Expression Eval
	 */
	public static double eval (final String s) {
		try {
			return new Object() {

				int pos = -1, ch;

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
			return -1000000;
		}
	}

	/**
	 * Resolve References
	 */
	public CellData resolveReferences(CellData cell, CSVMap csvMap) throws CircularReferenceException {
		List<String> elements = Arrays.asList(cell.evaluated_formula.trim().split("(?<=[-+*/()])|(?=[-+*/()])"));
		//check for operands only?
		//formula assembly
		LOG.fine(cell.formatCellData());
		StringBuilder formula = new StringBuilder();
		int refCount=0;
		int parentRef=0;
		for (String s : elements) {
			if (s.toUpperCase().equals(cell.s_idx)) {
				String msg = String.format("Circular Reference Exception: %nGetting Cell:%s%n%s5n", s, cell.formatCellData());
	        	throw new CircularReferenceException(msg);
			}
			if ( csvMap.getCsvMap().containsKey(s) ) {
				int r = csvMap.getCsvMap().get(s).eval_ref_stack_count;
				parentRef = (r>parentRef)? r :parentRef; //imperfect but pick largest stack
				refCount++;
				LOG.info(String.format("Getting Cell: %s%n",s));
				formula.append("(").append(csvMap.getCsvMap().get(s).evaluated_formula).append(")");
			} else formula.append(s);
		}
		cell.evaluated_formula=formula.toString();
		cell.eval_ref_count += refCount;
		
		if (refCount>0) cell.eval_ref_stack_count+=((parentRef>0)?parentRef:1);
		csvMap.getCsvMap().put(cell.s_idx, cell);
		return cell;
	}
}