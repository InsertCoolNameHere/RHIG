package irods;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.irods.jargon.core.exception.JargonException;

import console.RIGCommandReader;
import logging.LogFactory;
import node.Node;
import query.Query;
import rigElements.RadixIntegrityGraph;
import utility.FSConfig;


public class RIGHandler {
	
	private Map<String,RadixIntegrityGraph> rig_map = new HashMap<String,RadixIntegrityGraph>();
	private static Logger logger;
	
	public RIGHandler() {
		
		logger = LogFactory.getLogger(RIGCommandReader.class.getName(), "rig_handler.out");
		logger.setUseParentHandlers(false);
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
	
	
	
	public void updateFS_RIG(String fsName) throws IOException {
		
		IRODSManager subterra = new IRODSManager();
		
		String[] paths = null;
		try {
			paths = subterra.readAllRemoteFiles(fsName);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(paths!=null && paths.length > 0) {
			//String pp = "";
			for(String path: paths) {
				addIRODSPendingPath(path, fsName);
				//pp+=path+"\n";
			}
			
			updateRIG(fsName);
			System.out.println("RIKI: RIG UPDATE COMPLETE WITH "+paths.length+" PATHS");
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

}
