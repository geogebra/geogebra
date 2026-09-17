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

import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;

/**
 * The <code>Stroke</code> interface allows a
 * graphics object to obtain a {@link Shape} that is the
 * decorated outline, or stylistic representation of the outline,
 * of the specified <code>Shape</code>.
 * Stroking a <code>Shape</code> is like tracing its outline with a
 * marking pen of the appropriate size and shape.
 * The area where the pen would place ink is the area enclosed by the
 * outline <code>Shape</code>.
 * <p>
 * The methods of the <code>Graphics2D</code> interface that use the
 * outline <code>Shape</code> returned by a <code>Stroke</code> object
 * include <code>draw</code> and any other methods that are
 * implemented in terms of that method, such as
 * <code>drawLine</code>, <code>drawRect</code>,
 * <code>drawRoundRect</code>, <code>drawOval</code>,
 * <code>drawArc</code>, <code>drawPolyline</code>,
 * and <code>drawPolygon</code>.
 * <p>
 * The objects of the classes implementing <code>Stroke</code>
 * must be read-only because <code>Graphics2D</code> does not
 * clone these objects either when they are set as an attribute
 * with the <code>setStroke</code> method or when the
 * <code>Graphics2D</code> object is itself cloned.
 * If a <code>Stroke</code> object is modified after it is set in
 * the <code>Graphics2D</code> context then the behavior
 * of subsequent rendering would be undefined.
 * @see DefaultBasicStroke
 */
public interface Stroke {
	/**
	 * Returns an outline <code>Shape</code> which encloses the area that
	 * should be painted when the <code>Shape</code> is stroked according
	 * to the rules defined by the
	 * object implementing the <code>Stroke</code> interface.
	 * @param p a <code>Shape</code> to be stroked
	 * @return the stroked outline <code>Shape</code>.
	 */
	Shape createStrokedShape(Shape p);
}
