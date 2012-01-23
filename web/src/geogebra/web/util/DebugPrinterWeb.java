package geogebra.web.util;

import geogebra.common.util.DebugPrinter;

public class DebugPrinterWeb extends DebugPrinter{

	@Override
    public void print(String s, String info, int level) {
		System.out.println(info);
		System.out.print("\t");
		System.out.println(s);
    }

	@Override
    public void getMemoryInfo(StringBuilder sb) {
	    // we don't have access to this
    }

}
