package node;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.irods.jargon.core.exception.JargonException;

import console.RIGCommandReader;
import events.InitRigRequest;
import events.RigQueryRequest;
import events.RigUpdateRequest;
import irods.IRODSManager;
import irods.RIGHandler;
import irods.VerifyPathsNDirectories;
import events.Event;
import logging.LogFactory;
import reactor.RIGEventFactory;
import rigElements.RIGVertex;
import rigElements.RadixIntegrityGraph;
import rigFeature.Feature;
import rigFeature.FeatureType;
import utility.Pair;


// THIS FILE STARTS THE PROGRAM
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
		
		//logger.info("Messaging Node received SOMETHING");
		if(InitRigRequest.class.toString().equals(e.getClass().toString())) {
			
			// RIG STARTUP AND INITIALIZATION
			InitRigRequest regReq = (InitRigRequest)e;
			
			String fsName = regReq.getFsName();
			// CREATING RIG OBJECT
			rigHandler.addRIG(fsName);
			
			if(regReq.isUse_local()) {
				rigHandler.setParams(regReq.getLocal_dir());
			}
			try {
				rigHandler.updateFS_RIG(fsName);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		} else if(e instanceof RigQueryRequest) {
			
			long start = System.currentTimeMillis();
			
			// BLOCK DOWNLOAD REQUEST
			RigQueryRequest qReq = (RigQueryRequest)e;
			
			String fsName = qReq.getFsName();
			
			List<String> evaluatedPaths = null;
			
			VerifyPathsNDirectories downloader = new VerifyPathsNDirectories(qReq.getDownloadPath(), fsName);
			
			evaluatedPaths = rigHandler.evaluateQuery(fsName, qReq.getQuery());
			
			if(evaluatedPaths == null || evaluatedPaths.size() == 0)
				return;
			
			System.out.println("EVAL PATHS SIZE: "+evaluatedPaths.size());
			for(String p : evaluatedPaths) {
				
				System.out.println(p);
			}
			System.out.println("==============================");
			
			System.out.println("PATH EVALUATION: "+(System.currentTimeMillis() - start));
			
			if(!qReq.downloadOn) {
				return;
			}
			
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
						t = downloader.downloadAndValidateDirectories(p);
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
			
			System.out.println("DOWNLOAD TIME: "+ downloader.totalDownloadTime);
			System.out.println("VALIDATION TIME: "+ downloader.totalValidationTime);
			
		}  else if(e instanceof RigUpdateRequest) {
			
			RigUpdateRequest qReq = (RigUpdateRequest)e;
			
			handleLocalUpdate(qReq);
			
		}
	}


	// UPDATING CONTENTS OF AN EXISTING REPOSITORY
	private void handleLocalUpdate(RigUpdateRequest qReq) {
		
		long start = System.currentTimeMillis();
		
		String fsName = qReq.getFsName();
		
		VerifyPathsNDirectories downloader = new VerifyPathsNDirectories(qReq.getDownloadPath(), fsName);
		
		Map<String, RIGVertex<Feature, String>> evaluatedPaths = rigHandler.evaluateQueryMap(fsName, qReq.getQuery());
		
		if(evaluatedPaths == null || evaluatedPaths.size() == 0)
			return;
		
		System.out.println("QUERY EVALUATED PATHS:");
		for(String p : evaluatedPaths.keySet()) {
			
			System.out.println(p);
		}
		System.out.println("==============================");
		
		List<String> ultimatePaths = downloader.vetEvaluatedPaths(evaluatedPaths, qReq.getDownloadPath());
		
		System.out.println("PATHS VETTIED IN : "+(System.currentTimeMillis() - start)+"ms");
		System.out.println("NEED TO PERFORM "+ultimatePaths.size()+" DOWNLOADS :\n"+ultimatePaths);
		
		
		long start1 = System.currentTimeMillis();
		
		// ACTUAL DOWNLOADING
		int don = 1;
		for(String p : ultimatePaths) {
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
					t = downloader.downloadAndValidateDirectories(p);
					//im.readRemoteDirectory(qReq.getDownloadPath(), actPath);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
			
			if(!t) {
				System.out.println("Error Validating "+p);
			} else {
				if(don%10 == 0)
					System.out.println(don);
				//System.out.println("Successfully Downloaded & Validated "+p);
			}
			don++;
		}
		
		System.out.println("TOTAL PATHS DOWNLOADED: " + ultimatePaths.size() +" IN "+(System.currentTimeMillis() - start1));
		System.out.println("DOWNLOAD: "+downloader.totalDownloadTime+" VALIDATE "+downloader.totalValidationTime);
		System.out.println("YOUR CONTENTS ARE UP TO DATE.");
		System.out.println("TOTAL TIME: "+ (System.currentTimeMillis() - start));
		
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
