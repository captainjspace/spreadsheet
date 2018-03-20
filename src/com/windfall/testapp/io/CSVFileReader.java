package com.windfall.testapp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

import com.windfall.testapp.exception.FieldCountMismatchException;
import com.windfall.testapp.exception.NotACSVException;
import com.windfall.testapp.models.CSVFileReaderOutputObjects;
import com.windfall.testapp.models.CSVMap;
import com.windfall.testapp.models.CellData;
import com.windfall.testapp.models.FileStats;
import com.windfall.testapp.processors.IndexToSpeadsheetLocationMapper;

public class CSVFileReader {

	private static final Logger LOG = Logger.getLogger(CSVFileReader.class.getName());
	
	private CSVMap csvMap = new CSVMap();
	private FileStats fs = new FileStats();
	private IndexToSpeadsheetLocationMapper cellMapper = new IndexToSpeadsheetLocationMapper();

	public long getFileSize(Path p) throws IOException{
		long size = -1;
		try (FileChannel fc = FileChannel.open(p)) {
			size = fc.size();
			fc.close();
		} catch (IOException e) {
			LOG.warning("Could not get file size - expect IOException on read");
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
	public CSVFileReaderOutputObjects csvToMap(Path path) throws Exception {
		
		Scanner scanner = null;
		String line;
		CellData cd;
		Charset charset = Charset.forName("UTF-8");
		fs.path=path.toString();
		fs.size = getFileSize(path);
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			
			//input consistency check
		    int rowFields;  
		    
		    //rows
		    while ((line = reader.readLine()) != null) {
		    	if (fs.rowCount==0) checkFirstLine(line);//CSV check
		    	
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
		        
		        /* check to see if we are on the first row to set field count */
		        if (rowFields>fs.maxFieldsInRow && fs.maxFieldsInRow==0) {
		        	//this should execute only on the first row
		        	fs.maxFieldsInRow=rowFields;
		        	fs.allRowsHaveSameFieldCount=true;
		        } else if ( rowFields!=fs.maxFieldsInRow && fs.maxFieldsInRow!=0) {  
		        	//throw mismatch , track largest
		        	fs.maxFieldsInRow = (rowFields>fs.maxFieldsInRow)?rowFields:fs.maxFieldsInRow; 
		        	fs.allRowsHaveSameFieldCount=(!fs.allRowsHaveSameFieldCount);
		        	//build error string
		        	String msg = String.format("FieldCountMismatchException: Row #%d contains %d fields", fs.rowCount, rowFields);
		        	throw new FieldCountMismatchException(msg);
		        }
		        
		        fs.cellCount+=rowFields; //increment cell count
		    }
		    LOG.info(fs.getStats());
		    reader.close();
		} catch (IOException x) {
		    LOG.severe(String.format("IOException: %n%s%n", x));
		    throw x;
		} finally {
			if (scanner != null) scanner.close();
		}

		return new CSVFileReaderOutputObjects(fs, csvMap);
	}

	/**
	 * Not a CSV error check
	 * @param line first line of file
	 * @throws NotACSVException
	 */
	private void checkFirstLine(String line) throws NotACSVException {
		if(line.indexOf(',')==-1) {
    		LOG.severe("This does not look like a CSV file - First line has not commas");
    		throw new NotACSVException(String.format("%nThis does not look like a csv file%n%s", fs.getStats()));
    	}
	}
}
