package com.windfall.testapp.processors.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.windfall.testapp.processors.CellProcessor;

public class CellProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testEvalCellData() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testEvalString() {
		CellProcessor cp = new CellProcessor();
		Map<String,Double> testData = new HashMap<>();
		testData.put("10+5", 15.0);
		testData.put("20-30+5", -5.0);
		testData.put("(100+4)/4*10+2",262.0);
		
		testData.entrySet().stream().forEach( t -> {
			String equationToSolve = t.getKey();
			System.out.printf(String.format("Equation: %s - Expected Value: %.2f%n", equationToSolve,t.getValue()));
			try {
			   double solved = cp.eval(equationToSolve);
			   String msg = String.format("%s = %.2f%n", equationToSolve, solved);
			   System.out.printf("\t%s%n",msg);
			   assertEquals(msg, t.getValue(), solved, 0);
			} catch (ScriptException e) {
				fail("Evaluation Failed:" + e.getMessage());
			}
			
		});
	}

//	@Test
//	public void testResolveReferences() {
//		fail("Not yet implemented");
//	}

}
