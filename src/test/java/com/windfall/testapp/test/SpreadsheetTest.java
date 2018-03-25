package com.windfall.testapp.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.windfall.testapp.Spreadsheet;


public class SpreadsheetTest {

	@Test
	public void testRun() {
		boolean passed=false;
		String reason = null;
		try {
			Spreadsheet s= new Spreadsheet();
			s.run();
			passed = true;
		} catch (Exception e) {
			reason = e.getMessage();
		}
		if (!passed) {
			fail(reason);
		} else {
			System.out.println("example test");
		}
	}

}
