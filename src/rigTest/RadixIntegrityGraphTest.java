package rigTest;

import java.util.List;

import query.Expression;
import query.Operation;
import query.Query;
import rigElements.RadixIntegrityGraph;
import rigFeature.Feature;

public class RadixIntegrityGraphTest {

	
	public static void main(String arg[]) {
		
		RadixIntegrityGraph rig = new RadixIntegrityGraph("plotID:1,"+utility.Math.TEMPORAL_YEAR_FEATURE+":1,"+utility.Math.TEMPORAL_MONTH_FEATURE+":1,"+utility.Math.TEMPORAL_DAY_FEATURE+":1,sensorType:9", "/iplant/home/radix_subterra", "roots-arizona");
		rig.addPath("/iplant/home/radix_subterra/roots-arizona/20403/2018/9/28/irt/20403-2018-9-28-irt.gblock$$3373363902");
		rig.addPath("/iplant/home/radix_subterra/roots-arizona/20420/2018/9/28/irt/20420-2018-9-28-irt.gblock$$2550387965");
		rig.addPath("/iplant/home/radix_subterra/roots-arizona/20419/2018/9/28/irt/20419-2018-9-28-irt.gblock$$3522680225");
		rig.addPath("/iplant/home/radix_subterra/roots-arizona/20404/2018/9/28/irt/20404-2018-9-28-irt.gblock$$1043362283");
		
		
		rig.updatePathsIntoRIG();
		
		System.out.println("Hi "+rig.hrig.getRoot().hashValue); 
		
		
		Query query = buildQuery();
		
		List<String> evaluateQuery = rig.evaluateQuery(query);
		
		
		for(String p : evaluateQuery) {
			
			System.out.println(p);
		}
		
	}

	private static Query buildQuery() {
		Query q = new Query();
		q.addOperation(new Operation(new Expression(">", new Feature("plotID", 20405)),new Expression("==", new Feature(utility.Math.TEMPORAL_YEAR_FEATURE, 2018)),new Expression("==", new Feature("sensorType", "irt"))));
		//q.addOperation(new Operation(new Expression("==", new Feature("year", 2014)),new Expression("==", new Feature("month", 6))));
		return q;
	}
	
}
