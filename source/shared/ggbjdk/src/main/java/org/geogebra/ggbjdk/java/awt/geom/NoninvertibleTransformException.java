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
 * The <code>NoninvertibleTransformException</code> class represents
 * an exception that is thrown if an operation is performed requiring
 * the inverse of an {@link AffineTransform} object but the
 * <code>AffineTransform</code> is in a non-invertible state.
 */
@SuppressWarnings("serial")
public class NoninvertibleTransformException extends java.lang.Exception {
	/**
	 * Constructs an instance of
	 * <code>NoninvertibleTransformException</code>
	 * with the specified detail message.
	 * @param   s     the detail message
	 * @since   1.2
	 */
	public NoninvertibleTransformException(String s) {
		super(s);
	}
}
