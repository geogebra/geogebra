package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

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

	public abstract CASGenericInterface newGiac(CASparser p, CasParserTools t,
			Kernel k);
}
