package com.windfall.testapp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.windfall.testapp.Spreadsheet;
import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.exception.NotACSVException;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.processors.IndexToSpeadsheetLocationMapper;

public class CSVFileReader {

	private static final Logger LOG = Logger.getLogger(CSVFileReader.class.getName());
	
	private Map<String,CellData> csvMap = new HashMap<>();
	private IndexToSpeadsheetLocationMapper cellMapper = new IndexToSpeadsheetLocationMapper();

	public long getFileSize(Path p) throws IOException{
		long size = -1;
		try (FileChannel fc = FileChannel.open(p)) {
			size = fc.size();
			fc.close();
		} catch (IOException e) {
			LOG.severe("Could not get file size - expect IOException on read");
			throw e;
		}
		return size;
	}
	
	/**
	 * Maps csv file to HashMap of CellData for working
	 * @param path csv file path
	 * @return wrapper around FileStats and CSVMap
	 * @throws Exception subtypes - IOException, FieldCountMismatchException, NotACSVException
	 */
	public Map<String,CellData> csvToMap(Path path, Spreadsheet s) throws Exception {
		
		Scanner scanner = null;
		String line;
		CellData cd;
		Charset charset = Charset.forName("UTF-8");
		s.path=path.toString();
		s.size = getFileSize(path);
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			
			//input consistency check
		    int rowFields;  
		    
		    //rows
		    while ((line = reader.readLine()) != null) {
		    	if (s.m==0) checkFirstLine(line, s);//CSV check
		    	
		    	rowFields=0;
		    	s.m+=1;
		        scanner = new Scanner(line);
		        scanner.useDelimiter(",");
		        
		        //cols
		        while (scanner.hasNext()) {
		        	rowFields+=1;
		        	long idx = s.cellCount+rowFields;
		        	cd = new CellData(scanner.next(), idx, s.m, rowFields);
		        	cd.s_idx = cellMapper.getCellReference(cd.r,cd.c);
		        	csvMap.put(cd.s_idx, cd);
		        }
		        
		        /* check to see if we are on the first row to set field count */
		        if (rowFields>s.n && s.n==0) {
		        	//this should execute only on the first row
		        	s.n=rowFields;
		        	s.allRowsHaveSameFieldCount=true;
		        } else if ( rowFields!=s.n && s.n!=0) {  
		        	//throw mismatch , track largest
		        	s.n = Math.max(rowFields,s.n);
		        	s.allRowsHaveSameFieldCount=(!s.allRowsHaveSameFieldCount);
		        	//build error string
		        	String msg = String.format("FieldCountMismatchException: Row #%d contains %d fields", s.m, rowFields);
		        	LOG.severe(msg);
		        	throw new FieldCountMismatchException(msg);
		        }
		        s.cellCount+=rowFields; //increment cell count
		    }
		    LOG.fine(s.getFileStats());
		    reader.close();
		} catch (IOException x) {
		    LOG.severe(String.format("IOException: %n%s%n", x));
		    throw x;
		} finally {
			if (scanner != null) scanner.close();
		}
		
		return csvMap;
	}

	/**
	 * Not a CSV error check
	 * @param line first line of file
	 * @throws NotACSVException
	 */
	private void checkFirstLine(String line, Spreadsheet s) throws NotACSVException {
		if(line.indexOf(',')==-1) {
    		LOG.severe("This does not look like a CSV file - First line has not commas");
    		throw new NotACSVException(String.format("%nThis does not look like a csv file%n%s", s.getFileStats()));
    	}
	}
}
