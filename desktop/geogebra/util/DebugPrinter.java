package geogebra.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implementation for the DebugPrinter abstract class for desktop
 */
public class DebugPrinter extends geogebra.common.util.DebugPrinter {
	private DebugPrinterDesktop dpd;

	/**
	 * Creates a new DebugPrinter instance
	 */
	public DebugPrinter() {
		dpd = new DebugPrinterDesktop();
	}
	
	@Override
	public void print(String s, String info, int level) {
		dpd.print(s, info, level);
	}
	
	@Override
	public void print(String s) {
		dpd.print(s);
	}

	@Override
	public void debug(String s, boolean showMemory, boolean showTime, int level) {
		dpd.debug(s, showMemory, showTime, level);
	}
	
}
