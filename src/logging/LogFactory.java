package logging;

import java.io.File;
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
		String logDir = File.separator+"tmp"+File.separator+"rhigLogs"+File.separator;
		try {
			File dir = new File(logDir);
	         
	        if(!dir.exists())
	        	dir.mkdirs();
	         
			f = new FileHandler(logDir+logFileName);
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
