package com.windfall.testapp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.models.CSVFileParserOutput;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.FileStats;
import com.windfall.testapp.processors.IndexToSpeadsheetLocationMapper;

public class CSVFileParser {

	private static final Logger logger = Logger.getGlobal(); //.getLogger(CSVFileParser.class.getName());
	
	private CSVMap csvMap = new CSVMap();
	private FileStats fs = new FileStats();
	private IndexToSpeadsheetLocationMapper cellMapper = new IndexToSpeadsheetLocationMapper();

	public CSVFileParserOutput csvToMap(Path path) throws IOException, FieldCountMismatchException {
		
		Scanner scanner = null;
		String line;
		CellData cd;
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			
			//input consistency check
		    int maxFields=0;
		    int rowFields;  
		    
		    //rows
		    while ((line = reader.readLine()) != null) {
		    	rowFields=0;
		    	fs.rowCount+=1;
		        scanner = new Scanner(line);
		        scanner.useDelimiter(",");
		        
		        //cols
		        while (scanner.hasNext()) {
		        	rowFields+=1;
		        	long idx = fs.cellCount+rowFields;
		        	cd = cellMapper.getCellReference(new CellData(scanner.next(), idx, fs.rowCount, rowFields));
		        	csvMap.getCsvMap().put(cd.s_idx, cd);
		        }
		        
		        if (rowFields>maxFields && maxFields==0) {
		        	
		        	maxFields=rowFields;
		        	fs.allRowsHaveSameFieldCount=(!fs.allRowsHaveSameFieldCount);
		        	
		        } else if ( rowFields!=maxFields && maxFields!=0) {
		        	
		        	String msg = String.format("Check Data File: Row #%d contains %d fields", 
		        			fs.rowCount, rowFields);
		        	throw new FieldCountMismatchException(msg);
		        	
		        }
		        
		        fs.cellCount+=rowFields;
		    }
		    fs.maxFieldsInRow = maxFields;
		} catch (IOException x) {
		    logger.warning(String.format("IOException: %n%s%n", x));
		} finally {
			if (scanner != null) scanner.close();
			logger.info(String.format("File Stats:%n%s",fs.getStats()));
		}

		return new CSVFileParserOutput(fs, csvMap);
	}
}
