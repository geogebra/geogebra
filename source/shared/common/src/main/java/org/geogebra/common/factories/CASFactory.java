/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
