package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.MyMath;

public class DrawVectorModel {
	private final double[] coordsA = new double[2];
	private final double[] coordsB = new double[2];
	private final double[] coordsV = new double[2];
	private int lineThickness;
	private GBasicStroke objStroke;

	void update(int lineThickness, GBasicStroke objStroke) {
		this.lineThickness = lineThickness;
		this.objStroke = objStroke;
	}

	void setStartCoords(double x, double y) {
		coordsA[0] = x;
		coordsA[1] = y;
	}

	double getStartX() {
		return coordsA[0];
	}

	double getStartY() {
		return coordsA[1];
	}

	double getEndX() {
		return coordsB[0];
	}

	double getEndY() {
		return coordsB[1];
	}

	double getPositionVectorX() {
		return coordsV[0];

	}

	double getPositionVectorY() {
		return coordsV[1];
	}

	double[] getStartCoords() {
		return coordsA;
	}

	double[] getEndCoords() {
		return coordsB;
	}

	double[] getPositionVectorCoords() {
		return coordsV;
	}

	double getLineThickness() {
		return lineThickness;
	}

	double length() {
		return MyMath.length(this.coordsV[0], this.coordsV[1]);
	}

	void normalize() {
		coordsV[0] = coordsB[0] - coordsA[0];
		coordsV[1] = coordsB[1] - coordsA[1];
	}

	void scalePositionVector(double factor) {
		double length = length();
		this.coordsV[0] = (this.coordsV[0] * factor) / length;
		this.coordsV[1] = (this.coordsV[1] * factor) / length;

	}

	GBasicStroke getStroke() {
		return objStroke;
	}

	void update() {
		normalize();

		double factor = DrawVector.getFactor(lineThickness);
		double length = length();

		if (length < factor) {
			factor = length;
		}

		if (length > 0.0) {
			scalePositionVector(factor);
		}

	}

	void updateLabelPosition(Drawable drawable) {
		drawable.xLabel = (int) (xMiddle() + (coordsV[1] / 4.0));
		drawable.yLabel = (int) (yMiddle() - (coordsV[0] / 4.0));
	}

	private double yMiddle() {
		return (coordsA[1] + coordsB[1]) / 2.0;
	}

	private double xMiddle() {
		return (coordsA[0] + coordsB[0]) / 2.0;
	}

	void calculateEndCoords() {
		coordsB[0] = coordsA[0] + coordsV[0];
		coordsB[1] = coordsA[1] + coordsV[1];
	}

	void setEndCoords(double x, double y) {
		coordsB[0] = x;
		coordsB[1] = y;
	}

	void setVectorCoords(double x, double y) {
		coordsV[0] = x;
		coordsV[1] = y;
	}

	boolean isStartOnScreen(EuclidianView view) {
		return view.toScreenCoords(coordsA);
	}

	boolean isEndOnScreen(EuclidianView view) {
		return view.toScreenCoords(coordsB);
	}
}
