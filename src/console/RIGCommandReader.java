package console;

import java.util.Scanner;
import java.util.logging.Logger;

import events.InitRigRequest;
import events.RigQueryRequest;
import logging.LogFactory;
import node.Node;
import reactor.RIGEventFactory;


public class RIGCommandReader implements Runnable{
	
	private Scanner sc;
	private volatile boolean getOut = false;
	private RIGEventFactory ev;
	private static Logger logger;
	private Node callingNode;
	
	/*public RIGCommandReader() {
		logger = LogFactory.getLogger(RIGCommandReader.class.getName(), "console-reader.out");
		ev = RIGEventFactory.getInstance();
		sc = new Scanner(System.in);
	}*/
	
	public RIGCommandReader(Node callingNode) {
		logger = LogFactory.getLogger(RIGCommandReader.class.getName(), "console-reader.out");
		ev = RIGEventFactory.getInstance();
		sc = new Scanner(System.in);
		this.callingNode = callingNode;
	}
	
	
	@Override
	public void run() {
		
		System.out.print("RIG>");
		while(!getOut) {
			String cmd = sc.nextLine();
			
			if(cmd.startsWith("exit")) {
				System.out.println("TURNING RIG OFF...GOODBYE!");
				sc.close();
				setGetOut(true);
			} else if(cmd.startsWith("init_rig")) {
				
				String tokens[] = cmd.split(" ");
				// TEST
				if(tokens.length!=2) 
					System.out.println("INVALID SYNTAX. USE init_rig [filesystem_name] ");
				
				String fsName = tokens[1];
				
				InitRigRequest ir = new InitRigRequest(fsName);
				//System.out.println("YOU ASKED FOR..."+cmd);
				ev.reactInternal(ir, callingNode);
				System.out.print("\nRIG>");
				
			} else if(cmd.startsWith("query_rig")) {
				
				String tokens[] = cmd.split(" ");
				
				if(tokens.length < 2) 
					System.out.println("INVALID SYNTAX. USE query_rig [fsName] [date=\"yyyy-MM-dd\"] [plotID=p1,p2,p3] [sensor=SENSOR_NAME] [path=PATH_TO_DOWNLOAD]");
				
				RigQueryRequest ir = new RigQueryRequest(cmd);
				//System.out.println("YOU ASKED FOR..."+cmd);
				ev.reactInternal(ir, callingNode);
				System.out.print("\nRIG>");
			} else {
				System.out.println("INVALID COMMAND...TRY AGAIN");
				System.out.print("\nRIG>");
			}
			
		}
		//logger.info("RIG CONSOLE READER EXITED");
		
	}

	public Scanner getSc() {
		return sc;
	}

	public void setSc(Scanner sc) {
		this.sc = sc;
	}

	public boolean isGetOut() {
		return getOut;
	}

	public void setGetOut(boolean getOut) {
		this.getOut = getOut;
	}

	public RIGEventFactory getEv() {
		return ev;
	}

	public void setEv(RIGEventFactory ev) {
		this.ev = ev;
	}


}
