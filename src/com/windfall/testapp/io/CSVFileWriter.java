package com.windfall.testapp.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class CSVFileWriter {
	
	private static final Logger logger = Logger.getLogger(CSVFileWriter.class.getName());
	private Charset charset = Charset.forName("UTF-8");

	private Path outPath;
	public CSVFileWriter(Path p) {
		this.outPath=p;	
	}

	public void write(String output) {
		logger.info(String.format("Writing to %s%n", outPath));
		try (BufferedWriter writer = Files.newBufferedWriter(outPath,charset)) {
			writer.write(output);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
	}
}
