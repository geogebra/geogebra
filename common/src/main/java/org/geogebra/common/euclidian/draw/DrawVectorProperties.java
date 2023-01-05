package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.util.MyMath;

public class DrawVectorProperties {
	private final double[] coordsA;
	private final double[] coordsB;
	private final double[] coordsV;
	private final int lineThickness;
	private final GBasicStroke objStroke;

	public DrawVectorProperties(double[] coordsA, double[] coordsB, double[] coordsV,
			int lineThickness, GBasicStroke objStroke) {
		this.coordsA = coordsA;
		this.coordsB = coordsB;
		this.coordsV = coordsV;
		this.lineThickness = lineThickness;
		this.objStroke = objStroke;
	}

	public double getStartX() {
		return coordsA[0];
	}

	public double getStartY() {
		return coordsA[1];
	}
	public double getEndX() {
		return coordsB[0];
	}

	public double getEndY() {
		return coordsB[1];
	}

	public double getNormalVectorX() {
		return coordsV[0];

	}

	public double getNormalVectorY() {
		return coordsV[1];
	}

	public double[] getStartCoords() {
		return coordsA;
	}

	public double[] getEndCoords() {
		return coordsB;
	}

	public double[] getNormalVectorCoords() {
		return coordsV;
	}

	public double getLineThickness() {
		return lineThickness;
	}

	public double length() {
		return MyMath.length(this.coordsV[0], this.coordsV[1]);
	}

	public void normalize() {
		coordsV[0] = coordsB[0] - coordsA[0];
		coordsV[1] = coordsB[1] - coordsA[1];
	}

	public void scaleNormalVector(double factor) {
		double length = length();
		this.coordsV[0] = (this.coordsV[0] * factor) / length;
		this.coordsV[1] = (this.coordsV[1] * factor) / length;

	}

	public GBasicStroke getStroke() {
		return objStroke;
	}
}
