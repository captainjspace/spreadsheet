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
	
	public String formatCellData() {
	
		String fmt = "%n%s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n\t%-30s%-20s%n";
		String[] keys = {"Cell Data:","Index:","Row:", "Column:","Spreadsheet Location:","Text:","Evaluated Formula:",
				"Reference Resolve Attemped:","Successfully Calculated:", "Value:", "Total Ref Count:", "Highest Cell Stack Count:"};
		return String.format(fmt, 
				keys[0],
				keys[1],this.idx,
				keys[2],this.r,
				keys[3],this.c,
				keys[4],this.s_idx,
				keys[5],this.text,
				keys[6],this.evaluated_formula,
				keys[7],this.resolved,
				keys[8],this.calculated,
				keys[9],this.evaluated_value,
				keys[10],this.eval_ref_count,
				keys[11],this.eval_ref_stack_count
				);
	}
	@Override
	public String toString() {
		return "CellData [idx=" + idx + ", r=" + r + ", c=" + c + ", s_idx=" + s_idx + ", text=" + text
				+ ", evaluated_formula=" + evaluated_formula + ", evaluted_value=" + evaluted_value
				+ ", eval_ref_stack_count=" + eval_ref_stack_count + ", eval_ref_count=" + eval_ref_count
				+ ", resolved=" + resolved + ", calculated=" + calculated + "]";
	}
}
