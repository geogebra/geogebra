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

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * factory for GL stuff
 * 
 * @author mathieu
 *
 */
public abstract class GLFactory {

	/**
	 * prototype to factor stuff
	 */
	private static volatile GLFactory prototype = null;

	private static final Object lock = new Object();

	/**
	 * @return factory singleton instance
	 */
	public static GLFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            factory prototype
	 */
	public static void setPrototypeIfNull(GLFactory p) {
		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * 
	 * @return new float buffer
	 */
	abstract public GLBuffer newBuffer();

	/**
	 * 
	 * @return new short buffer for indices
	 */
	abstract public GLBufferIndices newBufferIndices();

}
