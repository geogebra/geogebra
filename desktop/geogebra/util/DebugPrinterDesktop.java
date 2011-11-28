package geogebra.util;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import geogebra.common.util.DebugPrinter;

public class DebugPrinterDesktop implements DebugPrinter{
	public void getTimeInfo(StringBuilder sb){
	Calendar calendar = new GregorianCalendar();
	int min = calendar.get(Calendar.MINUTE);
	String minS = (min < 10) ? "0" + min : "" + min;
	int sec = calendar.get(Calendar.SECOND);
	String secS = (sec < 10) ? "0" + sec : "" + sec;

	sb.append(" at ");
	sb.append(calendar.get(Calendar.HOUR));
	sb.append(":");
	sb.append(minS);
	sb.append(":");
	sb.append(secS);
	}

	public void print(String s,String info, int level) {
PrintStream debug = System.out;
		
		if (level > 0) debug = System.err;

		// multi line message
		if (s.indexOf("\n") > -1) {
			debug.println(info);
			debug.println(s);
			debug.println("*** END Message.");
		}
		// one line message
		else {
			debug.println(info);
			debug.print("\t");
			debug.println(s);
		}

		
	}

	public void getMemoryInfo(StringBuilder sb) {
		System.gc(); System.gc(); System.gc(); System.gc();

	    long usedK = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 ;

		sb.append("\n free memory: ");
		sb.append(Runtime.getRuntime().freeMemory());
		sb.append(" total memory: ");
		sb.append(Runtime.getRuntime().totalMemory());
		sb.append(" max memory: ");
		sb.append(Runtime.getRuntime().maxMemory());
		sb.append("\n used memory (total-free): ");
		sb.append(usedK + "K");			
	}
}
