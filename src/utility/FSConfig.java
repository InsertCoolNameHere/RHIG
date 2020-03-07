package utility;

import java.util.HashMap;
import java.util.Map;

public class FSConfig {
	
	private static Map<String, String> featuresMap;
	
	static {
		featuresMap = new HashMap<String, String>();
		featuresMap.put("roots-arizona", "plotID:1,"+utility.Math.TEMPORAL_YEAR_FEATURE+":1,"+utility.Math.TEMPORAL_MONTH_FEATURE+":1,"+utility.Math.TEMPORAL_DAY_FEATURE+":1,sensorType:9");
		
	}
	
	public static String getFeatures(String fsName) {
		return featuresMap.get(fsName);
	}

}
