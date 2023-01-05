package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;

public class DefaultVectorShape implements VectorShape {

	private final DrawVectorProperties properties;

	public DefaultVectorShape(DrawVectorProperties properties) {
		this.properties = properties;
	}

	@Override
	public GShape body() {
		return null;
	}

	@Override
	public GShape head() {
		GGeneralPath arrow = AwtFactory.getPrototype().newGeneralPath();
		double vX = properties.getNormalVectorX() / 4.0;
		double vY = properties.getNormalVectorY() / 4.0;


		double[] coordsF = new double[2];
		coordsF[0] = properties.getEndX() - properties.getNormalVectorX();
		coordsF[1] = properties.getEndY() - properties.getNormalVectorX();

		arrow.reset();
		arrow.moveTo(properties.getEndX(), properties.getEndY());
		arrow.lineTo(coordsF[0] - vY, coordsF[1] + vX);
		arrow.lineTo(coordsF[0] + vY, coordsF[1] - vX);
		arrow.closePath();
		return arrow;
	}
}
