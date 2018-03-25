package com.windfall.testapp.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class CSVFileWriter {
	
	private static final Logger LOG = Logger.getLogger(CSVFileWriter.class.getName());
	private static final String OUTPUT_DIR = "output/csv_output/";
	private static final String OUTPUT_FILE = "_output.csv_";
	
	private Charset charset = Charset.forName("UTF-8");
	private Path outPath;
	
	public CSVFileWriter(Path p) {
		init(p);
	}

	public void init(Path p) {
		String outputFile = p.getFileName().toString();
		int pos = outputFile.lastIndexOf(".");
		if (pos > 0)  outputFile = outputFile.substring(0, pos) + OUTPUT_FILE;
		this.outPath=Paths.get(OUTPUT_DIR + outputFile);
		try {
			Files.createDirectories(this.outPath.getParent());
		} catch (IOException e) {
			LOG.severe("Failed to Create Output Directory");
		}
	}
	
	public void write(String output) throws IOException {
		LOG.info(String.format("%nSUCCESS: Writing to %s%n", outPath));
		try (BufferedWriter writer = Files.newBufferedWriter(outPath,charset)) {
			writer.write(output);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			throw e;
		}
	}
}
