package com.windfall.testapp;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingConfig {
	
	private final static String LOGFILE="./log/Spreadsheet.log";
	
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s %2$s %5$s %6$s%n");
	}
	
	
	/**
	 * add handlers
	 */
	public void init() {
		try {
			Logger logger = Logger.getAnonymousLogger();
			// LOG this level to the log
			logger.setLevel(Level.ALL);

			final ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.INFO);
			consoleHandler.setFormatter(new SimpleFormatter());

			final FileHandler fileHandler = new FileHandler(LOGFILE,true);
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new SimpleFormatter());

			final Logger app = Logger.getLogger("");
			app.removeHandler(app.getHandlers()[0]); //remove default handler
			app.setLevel(Level.ALL);
			app.addHandler(fileHandler);
			app.addHandler(consoleHandler);
			Logger.getGlobal().info(String.format("%nLog File: %s", LOGFILE));

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}