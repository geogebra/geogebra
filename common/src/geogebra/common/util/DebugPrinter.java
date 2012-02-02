package geogebra.common.util;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;



public abstract class DebugPrinter {
	
	//in compiled js mode we no need exceptions
	public static boolean DEBUG_IN_PRODUCTION = false;
	
	public abstract void print(String s,String info, int level);
	public abstract void getMemoryInfo(StringBuilder sb);
	
	//for compiled just use console.log
	public abstract void print(String s);
	
	private static Set<String> reportedImpls = new TreeSet<String>();
	
	public void debug(String s,boolean showMemory,boolean showTime,int level){
	String ss = s == null ? "<null>" : s;
	String callerMethodName = null;
	String callerClassName = null;
	if (!DEBUG_IN_PRODUCTION) {
		try{
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
	
		// String calleeMethod = elements[0].getMethodName();
		callerMethodName = elements[3].getMethodName();
		callerClassName = elements[3].getClassName();
		}catch(Throwable t){
			//do nothing here; we are probably running Web in Opera
		}
		if(callerMethodName==null){
			print(ss,"",0);
			return;
		}
		if(s.toLowerCase().equals("implementation needed")){
			if(reportedImpls.contains(callerClassName+callerMethodName))
				return;
			reportedImpls.add(callerClassName+callerMethodName);
		}
		StringBuilder sb = new StringBuilder("*** Message from ");
		sb.append("[");
		sb.append(callerClassName);
		sb.append(".");
		sb.append(callerMethodName);
		sb.append("]");
	
		if ( showTime) {
			getTimeInfo(sb);
		}
	
		if (showMemory) {
			getMemoryInfo(sb);
	
		}
		
		print(ss, sb.toString(), level);
	} else {
		print(s);
	}
	
	}
	@SuppressWarnings("deprecation")
    public void getTimeInfo(StringBuilder sb) {
		Date calendar = new Date();
		int min = calendar.getMinutes();
		String minS = (min < 10) ? "0" + min : "" + min;
		int sec = calendar.getSeconds();
		String secS = (sec < 10) ? "0" + sec : "" + sec;
		sb.append(" at ");
		sb.append(calendar.getHours());
		sb.append(":");
		sb.append(minS);
		sb.append(":");
		sb.append(secS);
	    
    }
}
