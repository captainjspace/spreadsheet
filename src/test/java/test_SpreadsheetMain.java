package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.windfall.testapp.Spreadsheet;

public class test_SpreadsheetMain {

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

	@Test
	public void testRun() {
		boolean passed=false;
		String reason = null;
		try {
			Spreadsheet s= new Spreadsheet();
			s.run();
		} catch (Exception e) {
			reason = e.getMessage();
		}
		if (reason != null) {
			fail(reason);
		} else {
			System.out.println("example test");
		}
	}

}
