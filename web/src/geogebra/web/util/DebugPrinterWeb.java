package geogebra.web.util;

import geogebra.common.util.DebugPrinter;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Minimal debugging class for web
 */
public class DebugPrinterWeb extends DebugPrinter {

    @Override
    public native void print(String s, String info, int level) /*-{
		$wnd.console.log(info + "\t" + s);
    }-*/;

	@Override
    public void print(String s) {
	    print(s, "debug:", 0);
	    }

	@Override
    public void debug(String s, boolean showMemory, boolean showTime, int level) {
	    print(s); // TODO: Implement to handle missing parameters
	    
    }
}
