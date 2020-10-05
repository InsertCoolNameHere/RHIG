package test;

import events.InitRigRequest;
import events.RigQueryRequest;
import events.RigUpdateRequest;
import node.RIGMonitor;
import reactor.RIGEventFactory;

public class TestInitRIG {

	public static void main(String[] args) {
		
		RIGEventFactory ef = RIGEventFactory.getInstance();
		RIGMonitor m = new RIGMonitor();
		
		InitRigRequest req = new InitRigRequest("roots-arizona-2018", true, "/s/chopin/e/proj/sustain/sapmitra/arizona/rhig_demo");
		
		ef.reactInternal(req, m);
		
		/*String rig_query = "query_rhig roots-arizona-2018 plotID=20525,20526 sensor=lidar,irt path=/s/chopin/e/proj/sustain/sapmitra/arizona/rhig_repo";
		
		RigQueryRequest rq = new RigQueryRequest(rig_query.split(" "));
		
		ef.reactInternal(rq, m);*/
		
		String rig_query = "update_rhig roots-arizona-2018 plotID=20525,20526 sensor=lidar,irt path=/s/chopin/e/proj/sustain/sapmitra/arizona/rhig_repo";
		
		RigUpdateRequest rq = new RigUpdateRequest(rig_query.split(" "));
		
		ef.reactInternal(rq, m);
		

	}

}
