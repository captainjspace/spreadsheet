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

	private static final Logger LOG = Logger.getLogger(CSVFileParser.class.getName());
	
	private CSVMap csvMap = new CSVMap();
	private FileStats fs = new FileStats();
	private IndexToSpeadsheetLocationMapper cellMapper = new IndexToSpeadsheetLocationMapper();

	public CSVFileParserOutput csvToMap(Path path) throws IOException, FieldCountMismatchException {
		
		Scanner scanner = null;
		String line;
		CellData cd;
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			fs.path=path.toString();
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
		        	fs.maxFieldsInRow = maxFields;
		        	String msg = String.format("FieldCountMismatchException: Row #%d contains %d fields", 
		        			fs.rowCount, rowFields);
		        	throw new FieldCountMismatchException(msg);
		        	
		        }
		        
		        fs.cellCount+=rowFields;
		    }
		    fs.maxFieldsInRow = maxFields;
		    LOG.info(fs.getStats());
		    reader.close();
		} catch (IOException x) {
		    LOG.severe(String.format("IOException: %n%s%n", x));
		    throw x;
		} finally {
			if (scanner != null) scanner.close();
		}

		return new CSVFileParserOutput(fs, csvMap);
	}
}
