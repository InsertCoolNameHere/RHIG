package node;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.irods.jargon.core.exception.JargonException;

import console.RIGCommandReader;
import events.InitRigRequest;
import events.RigQueryRequest;
import irods.IRODSManager;
import irods.RIGHandler;
import irods.VerifyPathsNDirectories;
import events.Event;
import logging.LogFactory;
import reactor.RIGEventFactory;



public class RIGMonitor implements Node {
	
	/* THIS IS NEEDED TO INFORM OTHER MESSAGING NODES ABOUT THE SERVERSOCKET OF THIS MESSAGING NODE*/
	private RIGEventFactory eventFactory;
	private static Logger logger;
	private RIGCommandReader consoleReader;
	private RIGHandler rigHandler;
	
	
	public RIGMonitor() {
		rigHandler = new RIGHandler();
		logger = LogFactory.getLogger(RIGMonitor.class.getName(), "monitor.out");
		logger.setUseParentHandlers(false);
	}
	
	

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub
		
		//logger.info("Messaging Node received SOMETHING");
		if(InitRigRequest.class.toString().equals(e.getClass().toString())) {
			InitRigRequest regReq = (InitRigRequest)e;
			
			String fsName = regReq.getFsName();
			// CREATING RIG OBJECT
			rigHandler.addRIG(fsName);
			try {
				rigHandler.updateFS_RIG(fsName);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		} else if(e instanceof RigQueryRequest) {
			RigQueryRequest qReq = (RigQueryRequest)e;
			
			String fsName = qReq.getFsName();
			
			List<String> evaluatedPaths = rigHandler.evaluateQuery(fsName, qReq.getQuery());
			
			if(evaluatedPaths == null || evaluatedPaths.size() == 0)
				return;
			
			
			for(String p : evaluatedPaths) {
				
				System.out.println(p);
			}
			System.out.println("==============================");
			
			VerifyPathsNDirectories downloader = new VerifyPathsNDirectories(qReq.getDownloadPath(), fsName);
			// ACTUAL DOWNLOADING
			for(String p : evaluatedPaths) {
				boolean t = false;
				if(p.contains(".gblock")) {
					try {
						t = downloader.validatePaths(p, true);
						
						
					} catch (IOException e1) {
						System.out.println("ERROR DOWNLOADING "+p);
						e1.printStackTrace();
					}
				} else {
					
					try {
						t = downloader.validateDirectories(p);
						//im.readRemoteDirectory(qReq.getDownloadPath(), actPath);
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}
				
				if(!t) {
					System.out.println("Error Validating "+p);
				} else {
					System.out.println("Successfully Downloaded & Validated "+p);
				}
			}
			
		}
	}
	
	
	public static void main(String arg[]) {
		/*if(arg.length != 2) {
			System.out.println("Invalid number of Arguments Entered. Program Exiting.");
			return;
		}*/
		
		RIGMonitor m = null;
		
		//m = new RIGMonitor(arg[0], arg[1]);
		m = new RIGMonitor();
	
		
		logger.info("RIG Monitor Node Initialized");
		m.startup();
		
	}
	
	
	public void startup() {
		
		/* Starting Up the Thread In charge of reading from Console */
		
		consoleReader = new RIGCommandReader(this);
		Thread consoleReaderThread = new Thread(consoleReader);
		
		/*=====================CONSOLE READER THREAD STARTED==================================*/
		
		consoleReaderThread.start();
		logger.info("Messaging Node Console Reader started");
		
		
		try {
			
			consoleReaderThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	


	

	public RIGEventFactory getEventFactory() {
		return eventFactory;
	}



	public void setEventFactory(RIGEventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}



	public static Logger getLogger() {
		return logger;
	}



	public static void setLogger(Logger logger) {
		RIGMonitor.logger = logger;
	}


	@Override
	public void onCommand(String s) {
		
		if(s.equals("print-shortest-path")) {
			//dijk.printAllShortestPaths();
		}
		
	}



	
	
}
