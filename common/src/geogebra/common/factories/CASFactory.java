package geogebra.common.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;

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
	public abstract CASGenericInterface newMPReduce(CASparser p,CasParserTools t,Kernel kernel);

	public abstract CASGenericInterface newGiac(CASparser p, CasParserTools t, Kernel k);
}
