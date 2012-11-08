package geogebra.common.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

public abstract class CASFactory {
	private static CASFactory prototype;
	/**
	 * @return might return null. Use App.getCASFactory()
	 */
	public static CASFactory getPrototype() {
		return prototype;
	}
	
	public static void setPrototype(CASFactory factory) {
		prototype = factory;
	}
	public abstract CASmpreduce newMPReduce(App app,CASparser p,CasParserTools t,Kernel kernel);
}
