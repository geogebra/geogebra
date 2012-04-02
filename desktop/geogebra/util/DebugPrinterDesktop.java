package geogebra.util;

import geogebra.common.util.DebugPrinter;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Desktop related logging methods
 */
public class DebugPrinterDesktop extends DebugPrinter {
	
	/**
	 * @param sb the StringBuilder output to append the time
	 * Returns the current time in human readable format (for debugging)
	 */
	public void getTimeInfo(StringBuilder sb) {
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

	@Override
	public void print(String s, String info, int level) {
		PrintStream debug = System.out;

		if (level > 0)
			debug = System.err;

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

	/**
	 * @param sb the StringBuilder output to append the time
	 * Returns some memory related information (for debugging)
	 */
	public void getMemoryInfo(StringBuilder sb) {
		System.gc();
		System.gc();
		System.gc();
		System.gc();

		long usedK = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) / 1024;

		sb.append("\n free memory: ");
		sb.append(Runtime.getRuntime().freeMemory());
		sb.append(" total memory: ");
		sb.append(Runtime.getRuntime().totalMemory());
		sb.append(" max memory: ");
		sb.append(Runtime.getRuntime().maxMemory());
		sb.append("\n used memory (total-free): ");
		sb.append(usedK + "K");
	}

	@Override
	public void print(String s) {
		print(s, "debug:", 0);
	}

	private static Set<String> reportedImpls = new TreeSet<String>();

	@Override
	public void debug(String s, boolean showMemory, boolean showTime, int level) {
		String ss = s == null ? "<null>" : s;
		String callerMethodName = null;
		String callerClassName = null;
		if (!DEBUG_IN_PRODUCTION) {
			try {
				Throwable t = new Throwable();
				StackTraceElement[] elements = t.getStackTrace();

				// String calleeMethod = elements[0].getMethodName();
				callerMethodName = elements[3].getMethodName();
				callerClassName = elements[3].getClassName();
			} catch (Throwable t) {
				// do nothing here; we are probably running Web in Opera
			}
			if (callerMethodName == null) {
				print(ss, "", 0);
				return;
			}
			if (s.length() >= 21) {
				if (s.toLowerCase().substring(0, 21)
						.equals("implementation needed")) {
					if (reportedImpls.contains(callerClassName
							+ callerMethodName))
						return;
					reportedImpls.add(callerClassName + callerMethodName);
				}
			}
			StringBuilder sb = new StringBuilder("*** Message from ");
			sb.append("[");
			sb.append(callerClassName);
			sb.append(".");
			sb.append(callerMethodName);
			sb.append("]");

			if (showTime) {
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
}
