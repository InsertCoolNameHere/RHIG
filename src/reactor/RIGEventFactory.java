package reactor;

import events.Event;
import events.InitRigRequest;
import events.RigQueryRequest;
import node.Node;

public class RIGEventFactory {
	
	private static RIGEventFactory eventFactory;
	
	public static synchronized RIGEventFactory getInstance() {
		if(eventFactory == null) {
			eventFactory = new RIGEventFactory();
		}
		return eventFactory;
	}
	
	/* react to incoming message */
	
	
	
	public void reactInternal(Event ev, Node callingNode) {
		
		callingNode.onEvent(ev);
		
	}
	

}
