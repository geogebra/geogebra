package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;

public class ArrowVectorShape implements VectorShape {
	private final GLine2D line;
	private final DrawVectorModel model;

	/**
	 *
	 * @param model {@link DrawVectorModel}
	 */
	public ArrowVectorShape(DrawVectorModel model) {
		this.model = model;
		line = AwtFactory.getPrototype().newLine2D();
	}

	@Override
	public DrawVectorModel model() {
		return model;
	}

	@Override
	public GLine2D body() {
		line.setLine(model.getStartX(), model.getStartY(),
				model.getEndX(), model.getEndY());
		return line;
	}

	@Override
	public GShape head() {
		RotatedArrow rotatedArrow = new RotatedArrow(line, model.getLineThickness(),
				model.getStroke());
		return rotatedArrow.get();
	}

	@Override
	public GLine2D clipLine(int width, int height) {
		return body();
	}
}
