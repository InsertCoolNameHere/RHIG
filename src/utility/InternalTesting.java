package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.irods.jargon.core.exception.JargonException;

import irods.IRODSManager;
import irods.VerifyPathsNDirectories;


public class InternalTesting {
	public static String IRODS_BASE = "/iplant/home/radix_subterra/";
	public static String MY_BASE = "/tmp/sapmitra/";
	
	public static void main1(String arg[]) throws IOException {
		
		// READ RIG PATHS
		
		IRODSManager subterra = new IRODSManager();
		String[] paths = null;
		try {
			paths = subterra.readAllRemoteFiles("roots-arizona", "tmp");
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TOTAL PATHS:" +paths.length);
		
		int cnt = 0;
		if(paths!=null && paths.length > 0) {
			//String pp = "";
			for(String path: paths) {
				String myPath = path.replaceAll(IRODS_BASE, MY_BASE);
				
				String[] tokens = myPath.split("\\$\\$");
				String actPath = tokens[0];
				long checksum = Long.valueOf(tokens[1]);
				long localChecksum = Math.calculateChecksum(actPath);
				
				if(checksum != localChecksum) {
					System.out.println("ERROR: "+actPath+" "+checksum+">>"+localChecksum);
					cnt++;
				} else {
					System.out.println("PASS");
				}
			}
		}
		
		System.out.println(cnt);
	}
	
	
	public static void decompress(String in, File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(in))){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
    }
	
	public static void main(String arg[]) {
		
		removeSomeFiles("/tmp/sapmitra/localDump/roots-arizona/", 20);
	}
	
	public static void removeSomeFiles(String basePath, double percent) {
		
		File fBase = new File(basePath);
		List<String> listFileTree = VerifyPathsNDirectories.listFileTree(fBase);
		
		int toRemove = (int)(listFileTree.size()*percent/100);
		
		List<Integer> randomNums = getRandomNums(toRemove, listFileTree.size());
		for(int i: randomNums) {
			String p = listFileTree.get(i).split("\\$\\$")[0];
			File fp = new File(p);
			fp.delete();
		}
		
	}
	
	private static List<Integer> getRandomNums(int numSamples, int maxLine){
		Random rand = new Random();
		List<Integer> lineNums = new ArrayList<>(numSamples);
		while (lineNums.size() < numSamples) {
			int toAdd = rand.nextInt(maxLine);
			if (!lineNums.contains(toAdd))
				lineNums.add(toAdd);
		}
		return lineNums;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
