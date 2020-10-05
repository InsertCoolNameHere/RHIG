package events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.math3.analysis.function.Exp;

import query.Expression;
import query.Operation;
import query.Query;
import rigFeature.Feature;

public class RigUpdateRequest implements Event{
	
	private String fsName;
	private Query query;
	// DEFAULT PATH
	private String downloadPath = "/tmp/sapmitra/radix_download/";
	
	public String getFsName() {
		return fsName;
	}
	public void setFsName(String fsName) {
		this.fsName = fsName;
	}
	
	//query_rig [fsName] [date=yyyy-MM-dd] [plotID=p1,p2,p3] [sensor=SENSOR_NAME] [path=DOWNLOAD_PATH]
	//Example: query_rig roots-arizona plotID=20525,20526 path=/s/chopin/b/grad/sapmitra/Desktop/rigTest/
	public RigUpdateRequest(String[] tokens) {
		
		//String tokens[] = command.split(" ");
		this.fsName = tokens[1];
		
		Query q = new Query();
		
		if(tokens.length > 2) {
			List<Expression> date_expressions = new ArrayList<Expression>();
			List<Expression> plot_expressions = new ArrayList<Expression>();
			List<Expression> sensor_expressions = new ArrayList<Expression>();
			
			for(int i = 2; i < tokens.length; i++) {
				String arg = tokens[i];
				
				if(arg.startsWith("path")) {
					
					String p = arg.replace("path=", "");
					
					if(p.endsWith(File.separator))
						downloadPath = p;
					else
						downloadPath = p+File.separator;
					
				} else if(arg.startsWith("date")) {
					
					String dt = arg.replace("date=", "");
					
					String[] dateTokens = dt.split("-");
					
					String yr = dateTokens[0];
					
					if(!yr.contains("x")) {
						date_expressions.add(new Expression("==", new Feature(utility.Math.TEMPORAL_YEAR_FEATURE, Integer.valueOf(yr))));
					}
					
					
					String month = dateTokens[1];
					
					if(!month.contains("x")) {
						date_expressions.add(new Expression("==", new Feature(utility.Math.TEMPORAL_MONTH_FEATURE, Integer.valueOf(month))));
					}
					
					String day = dateTokens[2];
					
					if(!day.contains("x")) {
						date_expressions.add(new Expression("==", new Feature(utility.Math.TEMPORAL_DAY_FEATURE, Integer.valueOf(day))));
					}
					
					
				} else if(arg.startsWith("plotID")) {
					
					String[] plots = arg.replace("plotID=", "").split(",");
					
					for(String p : plots) {
						plot_expressions.add(new Expression("==", new Feature("plotID", Integer.valueOf(p))));
					}
					
				} else if(arg.startsWith("sensor")) {
					
					String[] sensors = arg.replace("sensor=", "").split(",");
					
					for(String s : sensors) {
						sensor_expressions.add(new Expression("==", new Feature("sensorType", s)));
					}
				} 
			}
			
			//List<Expression> fEx = date_expressions;
			List<List<Expression>> all_expressions = new ArrayList<List<Expression>>();
			
			if(plot_expressions.size() > 0 && sensor_expressions.size() > 0) {
				
				for(Expression p : plot_expressions) {
					
					for(Expression s : sensor_expressions) {
						List<Expression> ex_p= new ArrayList<Expression>(date_expressions);
						ex_p.add(s);
						ex_p.add(p);
						all_expressions.add(ex_p);
					}
				}
				
				
			} else if(plot_expressions.size() > 0) {
				
				for(Expression s : plot_expressions) {
					List<Expression> ex_p= new ArrayList<Expression>(date_expressions);
					ex_p.add(s);
					all_expressions.add(ex_p);
				}
				
			} else if(sensor_expressions.size() > 0) {
				
				for(Expression s : sensor_expressions) {
					List<Expression> ex_p= new ArrayList<Expression>(date_expressions);
					ex_p.add(s);
					all_expressions.add(ex_p);
				}
				
			} else {
				if(date_expressions.size() > 0) {
					all_expressions.add(date_expressions);
				}
			}
			
			if(all_expressions.size() > 0) {
				for(List<Expression> exps : all_expressions) {
					Operation o = new Operation(exps);
					q.addOperation(o);
				}
			}
		}
		query = q;
		
	}
	
	public static void main(String arg[]) {
		List<String> ss = null;
		for(String s : ss) {
			System.out.println(s);
		}
		
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPaths) {
		this.downloadPath = downloadPaths;
	}
	 
	
}
