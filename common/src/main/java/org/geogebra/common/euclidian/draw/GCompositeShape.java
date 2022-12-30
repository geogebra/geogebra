package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;

public class GCompositeShape {

	public static GArea toArea(GShape shape) {
		return AwtFactory.getPrototype().newArea(shape);
	}

	public static GShape union(GArea composite, GShape... shapes) {
		for (GShape shape : shapes) {
			if (shape != null) {
				composite.add(toArea(shape));
			}
		}
		return composite;
	}
}
