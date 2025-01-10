package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;

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
