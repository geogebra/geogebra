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

package org.geogebra.ggbjdk.java.awt.geom;

/**
 * The <code>IllegalPathStateException</code> represents an
 * exception that is thrown if an operation is performed on a path
 * that is in an illegal state with respect to the particular
 * operation being performed, such as appending a path segment
 * to a {@link GeneralPath} without an initial moveto.
 *
 */
@SuppressWarnings("serial")
public class IllegalPathStateException extends RuntimeException {
	/**
	 * Constructs an <code>IllegalPathStateException</code> with no
	 * detail message.
	 *
	 * @since   1.2
	 */
	public IllegalPathStateException() {}

	/**
	 * Constructs an <code>IllegalPathStateException</code> with the
	 * specified detail message.
	 * @param   s   the detail message
	 * @since   1.2
	 */
	public IllegalPathStateException(String s) {
		super(s);
	}
}
