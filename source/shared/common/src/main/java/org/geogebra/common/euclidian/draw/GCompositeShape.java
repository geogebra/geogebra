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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GShape;

public class GCompositeShape {

	/**
	 *
	 * @param shape to make an area of.
	 * @return the new area contains the shape only.
	 */
	public static GArea toArea(GShape shape) {
		return AwtFactory.getPrototype().newArea(shape);
	}

	/**
	 * Creates the union of shapes to one.
	 * @param composite to add shapes to.
	 * @param shapes to union.
	 * @return the result, composite shape.
	 */
	public static GShape union(GArea composite, GShape... shapes) {
		for (GShape shape : shapes) {
			if (shape != null) {
				composite.add(toArea(shape));
			}
		}
		return composite;
	}
}
