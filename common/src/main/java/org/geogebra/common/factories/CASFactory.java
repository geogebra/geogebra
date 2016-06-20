package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

/**
 * Factory for CAS engine(s)
 */
public abstract class CASFactory {
	private static CASFactory prototype;

	/**
	 * @return might return null. Use App.getCASFactory()
	 */
	public static CASFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param factory
	 *            prototype; needs to be set before we first call CAS
	 */
	public static void setPrototype(CASFactory factory) {
		prototype = factory;
	}

	/**
	 * @param parser
	 *            CAS parser
	 * @param tools
	 *            helper for output processing
	 * @param kernel
	 *            kernel
	 * @return GIAC instance
	 */
	public abstract CASGenericInterface newGiac(CASparser parser,
			CasParserTools tools,
			Kernel kernel);

	/**
	 * @return whether this will produce a working CAS
	 */
	public boolean isEnabled() {
		return true;
	}
}
