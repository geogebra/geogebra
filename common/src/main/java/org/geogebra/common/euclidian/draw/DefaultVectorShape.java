package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;

/**
 * Draws the vector with the default head
 */
public class DefaultVectorShape implements VectorShape {

	private final DrawVectorModel model;
	private final GLine2D line;

	/**
	 *
	 * @param model {@link DrawVectorModel}
	 */
	public DefaultVectorShape(DrawVectorModel model) {
		this.model = model;
		line = AwtFactory.getPrototype().newLine2D();
	}

	@Override
	public DrawVectorModel model() {
		return model;
	}

	@Override
	public GLine2D body() {
		double arrowBaseX = model.getEndX() - model.getPositionVectorX();
		double arrowBaseY = model.getEndY() - model.getPositionVectorY();
		line.setLine(model.getStartX(), model.getStartY(),
				arrowBaseX, arrowBaseY);
		return line;
	}

	@Override
	public GShape head() {
		GGeneralPath arrow = AwtFactory.getPrototype().newGeneralPath();
		double vX = model.getPositionVectorX() / 4.0;
		double vY = model.getPositionVectorY() / 4.0;

		double[] coordsF = new double[2];
		coordsF[0] = model.getEndX() - model.getPositionVectorX();
		coordsF[1] = model.getEndY() - model.getPositionVectorY();

		arrow.reset();
		arrow.moveTo(model.getEndX(), model.getEndY());
		arrow.lineTo(coordsF[0] - vY, coordsF[1] + vX);
		arrow.lineTo(coordsF[0] + vY, coordsF[1] - vX);
		arrow.closePath();
		return arrow;
	}
}
