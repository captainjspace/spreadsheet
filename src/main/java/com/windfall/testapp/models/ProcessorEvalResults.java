package com.windfall.testapp.models;

/**
 * Wrapper for maintaining processor evaluation formula, value and state
 * @author joshualandman
 *
 */
public class ProcessorEvalResults {
	
	public boolean complete;
	public String startFormula;
	public String endFormula;
	public double evaluatedValue;
	
	
	public String dump() {
		String fmt = "%n%s%n\t%-20s%5s%n\t%-20s%-20s%n\t%-20s%-20s%n\t%-20s%s%n";
		String[] keys = {"ProcessorEvalResults", "Complete:","Start Formula:","EndFormula:","Evaluated Value:"};
		return String.format(fmt, 
				keys[0],
				keys[1],this.complete,
				keys[2],this.startFormula,
				keys[3],this.endFormula,
				keys[4],this.evaluatedValue);
	}
	
}
