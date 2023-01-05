package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;

public class ArrowVectorShape implements VectorShape {
	private final GLine2D line;
	private final DrawVectorProperties properties;
	private boolean filled = false;

	public ArrowVectorShape(DrawVectorProperties properties) {
		this.properties = properties;
		line = AwtFactory.getPrototype().newLine2D();
	}

	@Override
	public GLine2D body() {
		line.setLine(properties.getStartX(), properties.getStartY(),
				properties.getEndX(), properties.getEndY());
		return line;
	}

	@Override
	public GShape head() {
		RotatedArrow rotatedArrow = new RotatedArrow(line, properties.getLineThickness(),
				properties.getStroke());
		rotatedArrow.setFilled(filled);
		return rotatedArrow.get();
	}

	@Override
	public GLine2D clippedBody() {
		return body();
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}
}
