package irods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.irods.jargon.core.exception.JargonException;

import console.RIGCommandReader;
import logging.LogFactory;
import node.Node;
import query.Query;
import rigElements.RIGVertex;
import rigElements.RadixIntegrityGraph;
import rigFeature.Feature;
import utility.FSConfig;


public class RIGHandler {
	
	private Map<String,RadixIntegrityGraph> rig_map = new HashMap<String,RadixIntegrityGraph>();
	private static Logger logger;
	
	//private String localRigPaths = "/s/chopin/b/grad/sapmitra/Desktop/rig/rig.txt";
	private String localRepoPath = "/s/chopin/e/proj/sustain/sapmitra/arizona/rhig_demo";
	private boolean local_rig_fetch = false;
	
	public RIGHandler() {
		
		logger = LogFactory.getLogger(RIGCommandReader.class.getName(), "rig_handler.out");
		logger.setUseParentHandlers(false);
	}
	
	// SETTING THE LOCAL PATH TO A USER-DEFINED DIRECTORY
	public void setParams(String local_path) {
		local_rig_fetch = true;
		localRepoPath = local_path;
	}
	
	public void addRIG(String fsName) {
		
		if(rig_map.get(fsName) != null) {
			System.out.println("RIG ALREADY EXISTS FOR "+fsName);
		} else {
			//RadixIntegrityGraph rig = new RadixIntegrityGraph(featureList, "/iplant/home/radix_subterra", fsName);
			RadixIntegrityGraph rig = new RadixIntegrityGraph(FSConfig.getFeatures(fsName), IRODSManager.IRODS_BASE, fsName);
			rig_map.put(fsName, rig);
		}
		
	}
	
	// READS IN ROG_PATHS FROM IRODS SERVER OR FROM A LOCAL FILE
	public void updateFS_RIG(String fsName) throws IOException {
		
		IRODSManager subterra = new IRODSManager();
		long start = System.currentTimeMillis();
		String[] paths = null;
		try {
			if(!local_rig_fetch)
				paths = subterra.readAllRemoteFiles(fsName, localRepoPath);
			else {
				
				Path path = Paths.get(localRepoPath+File.separator+"rig_"+fsName+".txt");
				
				if(Files.exists(path)) {
					System.out.println("READING LOCAL RHIG");
					String content = new String(Files.readAllBytes(path));
					paths = content.split("\n");
				} else {
					paths = subterra.readAllRemoteFiles(fsName, localRepoPath);
				}
				
	            
			}
				
		} catch (JargonException e) {
			e.printStackTrace();
		}
		
		if(paths!=null && paths.length > 0) {
			//String pp = "";
			for(String path: paths) {
				addIRODSPendingPath(path, fsName);
				//pp+=path+"\n";
			}
			
			updateRIG(fsName);
			
			long time1 = System.currentTimeMillis() - start;
			double ttaken = (double)time1/(double)1000;
			System.out.println("RIKI: RIG UPDATE COMPLETE WITH "+paths.length+" PATHS IN "+ttaken+" secs");
		} else {
			logger.info("RIKI: NO PATH DUMP DETECTED...");
		}
		
	}
	
	public void addIRODSPendingPath(String filePath, String fsName) {
		if(rig_map.get(fsName) != null) {
			rig_map.get(fsName).addPath(filePath);
		}
		
	}
	
	public void updateRIG(String fsName) {
		if(rig_map.get(fsName) != null) {
			rig_map.get(fsName).updatePathsIntoRIG();
		}
		
	}

	public List<String> evaluateQuery(String fsName, Query query) {
		if(rig_map.get(fsName) != null) {
			return rig_map.get(fsName).evaluateQuery(query);
		}
		return null;
	}

	/**
	 * RETURNS A MAP TO EVALUATED PATHS TO THEIR CORRESPONDING VERTICES
	 * @author sapmitra
	 * @param fsName
	 * @param query
	 * @return
	 */
	public Map<String, RIGVertex<Feature, String>> evaluateQueryMap(String fsName, Query query) {
		if(rig_map.get(fsName) != null) {
			return rig_map.get(fsName).evaluateQueryMap(query);
		}
		return null;
	}

}
