package irods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;

import org.irods.jargon.core.exception.JargonException;

import rigElements.RIGVertex;
import rigElements.RadixIntegrityGraph;
import rigFeature.Feature;
import rigFeature.FeatureType;
import utility.FSConfig;
import utility.Pair;


public class VerifyPathsNDirectories {
	
	// ================CONFIG START=====================
	public String fsName = "";
	
	// BASE DIRECTORY OF IRODS
	public static String irodsBase = IRODSManager.IRODS_BASE;
	
	// THE DIRECTORY WHERE THE FILES GET DOWNLOADED
	public static String fsBase = "/s/chopin/b/grad/sapmitra/Documents/radix/";
	
	public String separator = File.separator;
	
	public IRODSManager irm = new IRODSManager();
	
	// COMPLETE FEATURE LIST
	public List<Pair<String, FeatureType>> featureList;

	public long totalDownloadTime=0;

	public long totalValidationTime=0;
	
	/**
	 * @param faBase
	 * @param faName 
	 */
	public VerifyPathsNDirectories(String faBase, String faName) {
		
		if(faBase.endsWith(File.separator))
			fsBase = faBase;
		else
			fsBase = faBase+File.separator;
		fsName = faName;
		featureList = populateFeatures(fsName);
	}

	// ================CONFIG START=====================
	
	
	public boolean validatePaths(String pathStr, boolean toDownload) throws IOException {
		
		String[] tokens = pathStr.split("\\$\\$");
		
		long checkSum = Long.valueOf(tokens[1]);
		String filePath = tokens[0];
		
		
		if(checkSum == downloadAndCalculateFileCRC(filePath, toDownload)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param fullIrodsDirPath - FULL IRODS PATH TO THE DIRECTORY WHOSE CRC WE HAVE
	 * @return
	 * @throws IOException
	 */
	public boolean downloadAndValidateDirectories(String fullIrodsDirPath) throws IOException {
		
		String tokens[] = fullIrodsDirPath.split("\\$\\$");
		long checkVal = Long.valueOf(tokens[1]);
		fullIrodsDirPath = tokens[0];
		
		// DOWNLOADING THE DIRECTORY FIRST
		try {
			long start = System.currentTimeMillis();
			irm.downloadRemoteDirectory(fsBase, fullIrodsDirPath);
			totalDownloadTime+= (System.currentTimeMillis() - start);
		} catch (JargonException e) {
			e.printStackTrace();
		}
		
		long start = System.currentTimeMillis();
		
		String irodsBaseForThisFS = irodsBase+IRODSManager.IRODS_SEPARATOR+fsName;
		String relativePath = fullIrodsDirPath.substring(irodsBaseForThisFS.length());
		String fullGalileoDirPath = fsBase+fsName+
				relativePath.replace(IRODSManager.IRODS_SEPARATOR, IRODSManager.GALILEO_SEPARATOR);
		
		int numFeatures = getNumFeatures(relativePath);
		List<Pair<String, FeatureType>> relFeatures = getRelevantFeatures(numFeatures);
		
		if(relFeatures == null || relFeatures.size() == 0) {
			// THIS IS THE SENSOR DIRECTORY
			// ITS HAS ONE FILE, GET ITS CRC, THAT'S ENOUGH
			String tok[] = relativePath.split(IRODSManager.IRODS_SEPARATOR);
			//int ln = tok.length;
			String fname = "";
			for(int i=1; i<= numFeatures; i++) {
				if(i==numFeatures) {
					fname+=tok[i]+".gblock";
					continue;
				}
				fname+=tok[i]+"-";
			}
			
			totalValidationTime += (System.currentTimeMillis() - start);
			// VALIDATING PATH WITHOUT ACTUALLY DOWNLOADING
			return validatePaths(fullIrodsDirPath+IRODSManager.IRODS_SEPARATOR+fname+"$$"+checkVal, false);
		}
		
		List<String> paths = listFileTree(new File(fullGalileoDirPath));
		
		RadixIntegrityGraph rig = new RadixIntegrityGraph(relFeatures, fullIrodsDirPath, fsName);


		for(String p : paths) {
			
			p = p.substring(fsBase.length());
			//System.out.println(p);
			
			rig.addPath(irodsBase+IRODSManager.IRODS_SEPARATOR+p.replace(IRODSManager.GALILEO_SEPARATOR, IRODSManager.IRODS_SEPARATOR));
		}
		
		rig.updatePathsIntoRIG();
		
		totalValidationTime += (System.currentTimeMillis() - start);
		if(rig.hrig.getRoot().hashValue == checkVal)
			return true;
		
		return false;
	}
	
	
	public int getNumFeatures(String relativePath) {
		
		if(relativePath == null || relativePath.trim().length() == 0)
			return 0;
		
		String temp = relativePath;
		int cnt = 0;
		
		while(true) {
			if(temp.contains(IRODSManager.IRODS_SEPARATOR)) {
				int indx = temp.indexOf(IRODSManager.IRODS_SEPARATOR);
				temp = temp.substring(indx+1);
				cnt++;
			} else {
				break;
			}
		}
		
		return cnt;
	}
	
	
	public static List<String> listFileTree(File dir) {
	    List<String> fileTree = new ArrayList<String>();
	    
	    if(dir==null||dir.listFiles()==null){
	        return fileTree;
	    }
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile() && entry.getName().endsWith(".gblock")) {
	        	
	        	try {
					long val = RadixIntegrityGraph.getChecksumFromFilepath(entry.getAbsolutePath());
					fileTree.add(entry.getAbsolutePath()+"$$"+val);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	
	        } else 
	        	fileTree.addAll(listFileTree(entry));
	    }
	    return fileTree;
	}
	 
	
	
	public long downloadAndCalculateFileCRC(String suffixPath, boolean toDownload) throws IOException {
		long crc = -1;
		String fullPath = new String(suffixPath);
		suffixPath = suffixPath.replace(IRODSManager.IRODS_BASE, "");
		
		if(toDownload) {
			try {
				long start = System.currentTimeMillis();
				irm.downloadRemoteFile(fsBase, fullPath);
				totalDownloadTime += (System.currentTimeMillis() - start);
			} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long start = System.currentTimeMillis();
		Adler32 a1 = new Adler32();
		a1.update(Files.readAllBytes(Paths.get(fsBase+suffixPath)));
		crc = a1.getValue();
		totalValidationTime += (System.currentTimeMillis() - start);
		
		return crc;
	}
	

	public static void main(String[] args) throws IOException {
		// SAMPLE RESPONSE
		/*[/iplant/home/radix_subterra/roots-arizona/20403/2018/9/28$$3373363902, 
		 /iplant/home/radix_subterra/roots-arizona/20404/2018/9/28$$1043362283, 
		 /iplant/home/radix_subterra/roots-arizona/20419/2018/9/28$$3522680225, 
		 /iplant/home/radix_subterra/roots-arizona/20420/2018/9/28$$2550387965]*/

		/*
		/iplant/home/radix_subterra/roots-arizona/20420/2018/9/28/irt/20420-2018-9-28-irt.gblock$$2550387965
		/iplant/home/radix_subterra/roots-arizona/20404/2018/9/28/irt/20404-2018-9-28-irt.gblock$$1043362283
		/iplant/home/radix_subterra/roots-arizona/20403/2018/9/28/irt/20403-2018-9-28-irt.gblock$$3373363902
		/iplant/home/radix_subterra/roots-arizona/20419/2018/9/28/irt/20419-2018-9-28-irt.gblock$$3522680225
		*/
		
		VerifyPathsNDirectories vpd = new VerifyPathsNDirectories("/s/chopin/b/grad/sapmitra/Documents/radix/testdir", "roots-arizona");
		//vpd.validateDirectories("/testdir/");
		
		
		List<String> downloadList = new ArrayList<String>();
		downloadList.add("/iplant/home/radix_subterra/roots-arizona/20420/2018/9/28/irt$$2550387965");
		
		//vpd.downloadPaths(downloadList);
	}
	
	/*public void downloadPaths(List<String> downloadList) throws IOException {
		for(String dl : downloadList) {
			boolean result = false;
			if(dl.contains("gblock")) {
				result = validatePaths(dl, true);
			} else {
				result = validateDirectories(dl);
			}
			
			if(!result)
				System.out.println("FAILURE DOWNLOADING "+dl);
			else
				System.out.println("SUCCESSFULLY DOWNLOADED "+dl);
		}
	}
	
	
	public List<Pair<String, FeatureType>> populateFeatures() {
		
		List<Pair<String, FeatureType>> featureList = new ArrayList<>();
		
		// FEATURELIST IS PROBABLY NOT USED AT ALL
		
		featureList.add(new Pair<>("plotID", FeatureType.INT));
		
		featureList.add(new Pair<>(utility.Math.TEMPORAL_YEAR_FEATURE, FeatureType.INT));
		featureList.add(new Pair<>(utility.Math.TEMPORAL_MONTH_FEATURE, FeatureType.INT));
		featureList.add(new Pair<>(utility.Math.TEMPORAL_DAY_FEATURE, FeatureType.INT));
		
		featureList.add(new Pair<>("sensorType", FeatureType.STRING));
		
		return featureList;
	}*/
	
	
	public List<Pair<String, FeatureType>> populateFeatures(String fsName) {
		String featureList = FSConfig.getFeatures(fsName);
		
		List<String> featureNames = null;
		List<Pair<String, FeatureType>> fList = new ArrayList<>();
		if (featureList != null) {
			
			featureNames = new ArrayList<String>();
			
			for (String nameType : featureList.split(",")) {
				String[] pair = nameType.split(":");
				featureNames.add(pair[0]);
				fList.add(new Pair<String, FeatureType>(pair[0], FeatureType.fromInt(Integer.parseInt(pair[1]))));
			}
			/* Cannot modify featurelist anymore */
			fList = Collections.unmodifiableList(fList);
			
		} else {
			return null;
		}
		
		return fList;
		
	}

	/**
	 * GET THE PARTIAL FEATURES LIST THAT THIS DIRECTORY CONTAINS
	 * @return 
	 */
	public List<Pair<String, FeatureType>> getRelevantFeatures(int offset) {
		
		return featureList.subList(offset, featureList.size());
		
	}

	/**
	 * FIND WHICH PATHS IN THE EVALUATED ACTUALLY NEEDS DOWNLOADING BASED ON WHAT IS ALREADY IN THE LOCAL DIRECTORY
	 * @author sapmitra
	 * @param evaluatedPaths
	 * @param downloadPath
	 */
	public List<String> vetEvaluatedPaths(Map<String, RIGVertex<Feature, String>> evaluatedPaths, String localDirPath) {
		
		// LIST OF EVALUATED PATHS THAT ALREADY EXIST AND DONT NEED RE-DOWNLOADING
		List<String> toRemove = new ArrayList<String>();
		// LIST OF PATHS THAT ARE HERE BUT THEIR CHECKSUM IS STALE
		List<String> updatedPaths = new ArrayList<String>();
		
		//String localDirPath = fsBase;
		
		List<String> localPaths = listFileTree(new File(localDirPath));
		
		List<Pair<String, FeatureType>> relFeatures = getRelevantFeatures(0);
		
		RadixIntegrityGraph localRig = new RadixIntegrityGraph(relFeatures, IRODSManager.IRODS_BASE+IRODSManager.IRODS_SEPARATOR+fsName, fsName);

		for(String p : localPaths) {
			
			p = p.substring(localDirPath.length());
			//System.out.println(p);
			
			localRig.addPath(irodsBase+IRODSManager.IRODS_SEPARATOR+p.replace(IRODSManager.GALILEO_SEPARATOR, IRODSManager.IRODS_SEPARATOR));
		}
		
		localRig.updatePathsIntoRIG();
		
		// FOR EACH evaluatedPaths, CHECK IT AGAINST THE localRIG
		for(String path: evaluatedPaths.keySet()) {
			
			String tokens[] = path.split("\\$\\$");
			
			String relPath = tokens[0];
			long checkSum = Long.valueOf(tokens[1]);
			
			relPath = relPath.replace(IRODSManager.IRODS_BASE+IRODSManager.IRODS_SEPARATOR+fsName,"");
			
			if(relPath.startsWith(IRODSManager.IRODS_SEPARATOR)) {
				relPath = relPath.substring(1);
			}
			
			String[] pathTokens = relPath.split(IRODSManager.IRODS_SEPARATOR);
			
			int ln = pathTokens.length;
			
			int i=0;
			
			RIGVertex<Feature, String> local_vert = localRig.hrig.getRoot();
			for(String token : pathTokens) {
				
				Pair<String, FeatureType> featureInfo = relFeatures.get(i);
				i++;
				
				Feature f = new Feature(featureInfo.a,token);
				if(featureInfo.b == FeatureType.INT) {
					f = new Feature(featureInfo.a,Integer.valueOf(token));
				} else if(featureInfo.b == FeatureType.FLOAT) {
					f = new Feature(featureInfo.a,Float.valueOf(token));
				} 
				
				// neighbor IS THE localRIG VERTEX
				local_vert = local_vert.getNeighbor(f);
				
				if(local_vert == null) {
					// THIS PATH DOES NOT EXIST IN LOCAL YET
					break;
				}
				
				// WE HAVE REACHED THE FINAL NODE REPRESENTED BY THE EVALUATED PATH
				if(i == ln) {
					if(local_vert.hashValue == checkSum) {
						// THE HASH OF THE LOCAL DIRECTORY MATCHES THE HASH OF THE RIG
						toRemove.add(path);
						break;
					} else {
						// THIS PATH IS TO BE REMOVED...WE NEED TO GO DOWN THE TREE AND FIND FINER PATHS TO DOWNLOAD
						toRemove.add(path);
						//updatedPaths.add(path);
						
						RIGVertex<Feature, String> online_vertex = evaluatedPaths.get(path);
						// COMPARE online_vertex and local_vert
						
						compareVertices(local_vert, online_vertex, updatedPaths);
						
						break;
					}
				}
			}
			
		}
		
		for(String k : toRemove) {
			evaluatedPaths.remove(k);
		}
		
		List<String> ultimatePaths = new ArrayList<String>(evaluatedPaths.keySet());
		ultimatePaths.addAll(updatedPaths);
		
		//System.out.println("EVALUATED PATHS:" + evaluatedPaths.keySet());
		//System.out.println("VETTED PATHS: " + ultimatePaths);
		System.out.println("PATHS ALREADY UP TO DATE: " + toRemove);
		
		return ultimatePaths;
		
	}
	
	/**
	 * COMPARES TWO SUB-TREES AND FINDS PATHS THAT MISMATCH BETWEEN THEM
	 * @author sapmitra
	 * @param local_vert THE LABEL OF THIS AND online_vert MATCHES, BUT THEIR HASH IS NOT. 
	 * 	WE GO DEEPER TO FIND THE DISCREPANCY
	 * @param online_vert
	 * @param newPaths
	 */
	private void compareVertices(RIGVertex<Feature, String> local_vert, RIGVertex<Feature, String> online_vert, List<String> newPaths) {
		
		Collection<RIGVertex<Feature, String>> onlineNeighbors = online_vert.getAllNeighbors();
		
		if(onlineNeighbors == null || onlineNeighbors.isEmpty()) {
			// WE HAVE REACHED THE END AND STILL NO MATCH
			newPaths.add(online_vert.path+"$$"+online_vert.hashValue);
			return;
		}
		
		for(RIGVertex<Feature, String> oNode : onlineNeighbors) {
			//System.out.println("TESTING");
			RIGVertex<Feature, String> lNode = local_vert.getNeighbor(oNode.getLabel());
			
			if(lNode == null) {
				//THIS EVALUATED PATH DOES NOT EXIST IN LOCAL
				// NO FURTHER TRAVERSAL DOWN THIS PATH IS NECESARY
				newPaths.add(oNode.path+"$$"+oNode.hashValue);
				continue;
			}
			
			if(lNode.hashValue == oNode.hashValue) {
				// NOTHING
				continue;
			} else {
				// THIS NODE IS AVAILABLE IN LOCAL, BUT ITS HASHVALUE MISMATCHES WITH THE ONLINE VERSION
				compareVertices(lNode, oNode, newPaths);
			}
			/*oNode.getValues()
			Feature f = new Feature(featureInfo.a,token);
			if(featureInfo.b == FeatureType.INT) {
				f = new Feature(featureInfo.a,Integer.valueOf(token));
			} else if(featureInfo.b == FeatureType.FLOAT) {
				f = new Feature(featureInfo.a,Float.valueOf(token));
			} 
			
			local_vert.getNeighbor(f);*/
		}
		
	}
	
	
}
