package geogebra.web.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Web related logging methods
 */
public class DebugPrinter extends geogebra.common.util.DebugPrinter {

	private DebugPrinterWeb dpw;

	/**
	 * Creates a new DebugPrinter instance
	 */
	public DebugPrinter() {
		dpw = new DebugPrinterWeb();
	}

	@Override
    public void print(String s, String info, int level) {
		dpw.print(s, info, level);
	}
	
	@Override
    public void print(String s) {
		dpw.print(s);
	}

	@Override
    public void debug(String s, boolean showMemory, boolean showTime, int level) {
	    dpw.debug(s, showMemory, showTime, level); // TODO: Implement to handle missing parameters
    }

}
