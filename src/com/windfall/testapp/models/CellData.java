package com.windfall.testapp.models;
/**
 * Pojo for cell properties
 * @author joshualandman
 *
 */
public class CellData {
	
	public long idx;
	public int r,c, evaluated_value, eval_ref_stack_count, eval_ref_count; //index, row, col
	public String s_idx, text, evaluated_formula; //spreadsheet location
	public double evaluted_value;
	public boolean resolved, calculated;
	
	public CellData(String s, long idx, int row, int col) {
		this.idx=idx;
		this.r=row;
		this.c=col;
		this.text=s;
		this.evaluated_formula=s;
		this.resolved=false;
		this.calculated=false;
	}
	
	@Override
	public String toString() {
		return "CellData [idx=" + idx + ", r=" + r + ", c=" + c + ", s_idx=" + s_idx + ", text=" + text
				+ ", evaluated_formula=" + evaluated_formula + ", evaluted_value=" + evaluted_value
				+ ", eval_ref_stack_count=" + eval_ref_stack_count + ", eval_ref_count=" + eval_ref_count
				+ ", resolved=" + resolved + ", calculated=" + calculated + "]";
	}
}
