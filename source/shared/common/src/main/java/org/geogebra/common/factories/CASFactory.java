package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

/**
 * Factory for CAS engine(s)
 */
public abstract class CASFactory {
	private static final Object lock = new Object();

	// MOB-1842
	// make sure we have a dummy CAS in place that always returns "?"
	// for non-CAS apps (eg Graphing, Geometry)
	private static volatile CASFactory prototype = new CASFactoryDummy();

	/**
	 * @return might return null. Use App.getCASFactory()
	 */
	public static CASFactory getPrototype() {
		return prototype;
	}

	public static boolean isInitialized() {
		return prototype != null && !(prototype instanceof CASFactoryDummy);
	}

	/**
	 * @param factory
	 *            prototype; needs to be set before we first call CAS
	 */
	public static void setPrototype(CASFactory factory) {
		synchronized (lock) {
			prototype = factory;
		}
	}

	/**
	 * @param parser
	 *            CAS parser
	 * @param kernel
	 *            kernel
	 * @return GIAC instance
	 */
	public abstract CASGenericInterface newGiac(CASparser parser,
			Kernel kernel);

	/**
	 * @return whether this will produce a working CAS
	 */
	public boolean isEnabled() {
		return true;
	}
}
