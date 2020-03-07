package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LogFactory {
	
	public static Logger getLogger(String className, String logFileName) {
		
		Logger logger = Logger.getLogger(className);
		logger.setUseParentHandlers(false);
		FileHandler f;
		try {
			f = new FileHandler("/s/chopin/b/grad/sapmitra/Documents/radix/rigLogs/"+logFileName);
			f.setFormatter(new SimpleFormatter());
			f.setLevel(Level.ALL);
			logger.addHandler(f);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return logger;
		
	}

}
