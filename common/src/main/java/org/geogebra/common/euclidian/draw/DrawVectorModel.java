package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.util.MyMath;

public class DrawVectorModel {
	private final double[] coordsA = new double[2];
	private final double[] coordsB = new double[2];
	private final double[] coordsV = new double[2];
	private int lineThickness;
	private GBasicStroke objStroke;

	public void update(int lineThickness, GBasicStroke objStroke) {
		this.lineThickness = lineThickness;
		this.objStroke = objStroke;
	}

	public void setStartCoords(double x, double y) {
		coordsA[0] = x;
		coordsA[1] = y;
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

	public double getPositionVectorX() {
		return coordsV[0];

	}

	public double getPositionVectorY() {
		return coordsV[1];
	}

	public double[] getStartCoords() {
		return coordsA;
	}

	public double[] getEndCoords() {
		return coordsB;
	}

	public double[] getPositionVectorCoords() {
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

	public void scalePositionVector(double factor) {
		double length = length();
		this.coordsV[0] = (this.coordsV[0] * factor) / length;
		this.coordsV[1] = (this.coordsV[1] * factor) / length;

	}

	public GBasicStroke getStroke() {
		return objStroke;
	}

	public void update() {
		normalize();

		// calculate endpoint F at base of arrow

		double factor = DrawVector.getFactor(lineThickness);

		double length = length();

		// decrease arrowhead size if it's longer than the vector
		if (length < factor) {
			factor = length;
		}

		if (length > 0.0) {
			scalePositionVector(factor);
		}

	}

	public void updateLabelPosition(Drawable drawable) {
		// note that coordsV was normalized in setArrow()
		drawable.xLabel = (int) ((coordsA[0] + coordsB[0]) / 2.0 + coordsV[1]);
		drawable.yLabel = (int) ((coordsA[1] + coordsB[1]) / 2.0 - coordsV[0]);
	}

	public void calculateEndCoords() {
		coordsB[0] = coordsA[0] + coordsV[0];
		coordsB[1] = coordsA[1] + coordsV[1];
	}

	public void setEndCoords(double x, double y) {
		coordsB[0] = x;
		coordsB[1] = y;
	}

	public void setVectorCoords(double x, double y) {
		coordsV[0] = x;
		coordsV[1] = y;
	}
}
