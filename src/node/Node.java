package node;

import java.net.Socket;

import events.Event;


public interface Node {
	
	public void onEvent(Event e);
	
	public void onCommand(String s);

}
