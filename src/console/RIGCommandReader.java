package console;

import java.util.Scanner;
import java.util.logging.Logger;

import events.InitRigRequest;
import events.RigQueryRequest;
import events.RigUpdateRequest;
import logging.LogFactory;
import node.Node;
import reactor.RIGEventFactory;


public class RIGCommandReader implements Runnable{
	
	private Scanner sc;
	private volatile boolean getOut = false;
	private RIGEventFactory ev;
	private static Logger logger;
	private Node callingNode;
	
	public RIGCommandReader(Node callingNode) {
		logger = LogFactory.getLogger(RIGCommandReader.class.getName(), "console-reader.out");
		ev = RIGEventFactory.getInstance();
		sc = new Scanner(System.in);
		this.callingNode = callingNode;
	}
	
	
	@Override
	public void run() {
		
		System.out.print("RHIG>");
		while(!getOut) {
			String cmd = sc.nextLine();
			
			if(cmd.startsWith("exit")) {
				System.out.println("TURNING RIG OFF...GOODBYE!");
				sc.close();
				setGetOut(true);
			} else if(cmd.startsWith("init_rhig")) {
				
				String tokens[] = cmd.split(" ");
				
				if(tokens.length!=2 && tokens.length!=4) {
					System.out.println("INVALID SYNTAX. USE init_rhig [filesystem_name] [use_local(optional)(boolean) [local_dir(optional)]]");
					
				} else {
				
					String fsName = tokens[1];
					
					if(tokens.length == 2) {
						InitRigRequest ir = new InitRigRequest(fsName);
						//System.out.println("YOU ASKED FOR..."+cmd);
						ev.reactInternal(ir, callingNode);
						//System.out.print("\nRHIG>");
					} else if(tokens.length ==4) {
						boolean use_local = Boolean.valueOf(tokens[2]);
						String local_path = tokens[3];
						InitRigRequest ir = new InitRigRequest(fsName, use_local, local_path);
						ev.reactInternal(ir, callingNode);
					}
				}
				
			} else if(cmd.startsWith("query_rhig")) {
				
				String tokens[] = cmd.split(" ");
				
				//query_rhig roots-arizona plotID=20525,20526 sensor=lidar path=/s/chopin/b/grad/sapmitra/Desktop/rigTest/
				if(tokens.length < 2) 
					System.out.println("INVALID SYNTAX. USE query_rhig [fsName] [date=\"yyyy-MM-dd\"] [plotID=p1,p2,p3] [sensor=SENSOR_NAME] [path=PATH_TO_DOWNLOAD]");
				
				long start = System.currentTimeMillis();
				RigQueryRequest ir = new RigQueryRequest(tokens);
				//System.out.println("YOU ASKED FOR..."+cmd);
				ev.reactInternal(ir, callingNode);
				
				long ms = System.currentTimeMillis()-start;
				double tt = (double)ms/1000;
				System.out.println("Overall Complete in "+tt+" secs\n");
				
				//System.out.print("\nRHIG>");
			} else if(cmd.startsWith("update_rhig")) {
				
				String tokens[] = cmd.split(" ");
				//Example: update_rhig roots-arizona plotID=20525,20526 path=/s/chopin/b/grad/sapmitra/Desktop/rigTest/
				if(tokens.length < 2) 
					System.out.println("INVALID SYNTAX. USE update_rhig [fsName] [date=yyyy-MM-dd] [plotID=p1,p2,p3] [sensor=SENSOR_NAME] [path=PATH_TO_DOWNLOAD]");
				
				RigUpdateRequest ir = new RigUpdateRequest(tokens);
				//System.out.println("YOU ASKED FOR..."+cmd);
				ev.reactInternal(ir, callingNode);
				//System.out.print("\nRHIG>");
			} else {
				System.out.println("INVALID COMMAND...TRY AGAIN");
				//System.out.print("\nRHIG>");
			}
			if(!getOut)
				System.out.print("RHIG>");
			
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
	
	public static void main(String arg[]) {
		System.out.println(System.getenv("IRODS_USER"));
	}


}
