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
	private static final String OUTPUT_DIR = "/csv_output/";
	private Charset charset = Charset.forName("UTF-8");

	private Path outPath;
	public CSVFileWriter(Path p) {
		this.outPath=Paths.get(p.getParent().getParent() + OUTPUT_DIR + p.getFileName());
	}

	public void write(String output) {
		LOG.info(String.format("%nWriting to %s%n", outPath));
		
		try {
			Files.createDirectories(this.outPath.getParent());
		} catch (IOException e1) {
			LOG.severe("Failed to Create Output Directory");
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(outPath,charset)) {
			writer.write(output);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	}
}
