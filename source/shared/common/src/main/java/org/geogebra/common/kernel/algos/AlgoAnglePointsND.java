/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAnglePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * @author Markus
 */
public abstract class AlgoAnglePointsND extends AlgoAngle
		implements DrawInformationAlgo {
	/** first leg */
	protected GeoPointND leg1N;
	/** vertex */
	protected GeoPointND vertexN;
	/** second leg */
	protected GeoPointND leg2N;
	/** output angle */
	protected GeoAngle angle;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param orientation
	 *            orientation for 3D or null
	 */
	public AlgoAnglePointsND(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation) {
		super(cons);
		setInput(A, B, C, orientation);
		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	/**
	 * set input
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @param orientation
	 *            orientation (can be null)
	 */
	protected void setInput(GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation) {

		this.leg1N = A;
		this.vertexN = B;
		this.leg2N = C;
	}

	/**
	 * used as a helper algo (for AlgoAnglePolygon)
	 * 
	 * @param cons
	 *            construction
	 */
	protected AlgoAnglePointsND(Construction cons) {
		super(cons);
		angle = new GeoAngle(cons); // not setting the angle interval
		angle.setDrawable(true);
	}

	/**
	 * set the points
	 * 
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 */
	public final void setABC(GeoPointND A, GeoPointND B, GeoPointND C) {
		this.leg1N = A;
		this.vertexN = B;
		this.leg2N = C;
	}

	/**
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param cons
	 *            construction
	 */
	public AlgoAnglePointsND(GeoPointND A, GeoPointND B, GeoPointND C,
			Construction cons) {
		super(cons);
		this.cons = cons;
		setABC(A, B, C);

	}

	/**
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 */
	public AlgoAnglePointsND(GeoPointND A, GeoPointND B, GeoPointND C) {
		super(A.toGeoElement().cons, false);
		setABC(A, B, C);
	}

	/**
	 * For copy constructor
	 * 
	 * @param c
	 *            construction
	 * @param addToConstructionList
	 *            whether to add to cons
	 */
	protected AlgoAnglePointsND(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) leg1N;
		input[1] = (GeoElement) vertexN;
		input[2] = (GeoElement) leg2N;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	/**
	 * @return first leg
	 */
	public GeoPointND getA() {
		return leg1N;
	}

	/**
	 * @return vertex
	 */
	public GeoPointND getB() {
		return vertexN;
	}

	/**
	 * @return second leg
	 */
	public GeoPointND getC() {
		return leg2N;
	}

	@Override
	public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		// if (algoAnglePoly != null) {
		// return getLoc().getPlainDefault("AngleBetweenABCofD",
		// "Angle between %0, %1, %2 of %3", leg1N.getLabel(tpl),
		// vertexN.getLabel(tpl), leg2N.getLabel(tpl),
		// algoAnglePoly.getPolygon().getNameDescription());
		// }
		return getLoc().getPlainDefault("AngleBetweenABC",
				"Angle between %0, %1, %2", leg1N.getLabel(tpl),
				vertexN.getLabel(tpl), leg2N.getLabel(tpl));
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		Coords v = drawable.getCoordsInView(vertexN);
		if (!drawable.inView(v)) {
			return false;
		}

		m[0] = v.get()[0];
		m[1] = v.get()[1];

		Coords ptCoords = drawable.getCoordsInView(leg1N);
		if (!drawable.inView(ptCoords)) {
			return false;
		}
		Coords coords2 = drawable.getCoordsInView(leg2N);
		if (!drawable.inView(coords2)) {
			return false;
		}

		// first vec
		firstVec[0] = ptCoords.getX() - m[0];
		firstVec[1] = ptCoords.getY() - m[1];

		double[] vertexScreen = new double[2];
		vertexScreen[0] = m[0];
		vertexScreen[1] = m[1];

		double[] firstVecScreen = new double[2];
		firstVecScreen[0] = ptCoords.getX();
		firstVecScreen[1] = ptCoords.getY();

		double[] secondVecScreen = new double[2];
		secondVecScreen[0] = coords2.getX();
		secondVecScreen[1] = coords2.getY();

		drawable.toScreenCoords(vertexScreen);
		drawable.toScreenCoords(firstVecScreen);
		drawable.toScreenCoords(secondVecScreen);

		firstVecScreen[0] -= vertexScreen[0];
		firstVecScreen[1] -= vertexScreen[1];
		secondVecScreen[0] -= vertexScreen[0];
		secondVecScreen[1] -= vertexScreen[1];

		drawable.setMaxRadius(0.5 * Math.sqrt(Math.min(
				firstVecScreen[0] * firstVecScreen[0]
						+ firstVecScreen[1] * firstVecScreen[1],
				secondVecScreen[0] * secondVecScreen[0]
						+ secondVecScreen[1] * secondVecScreen[1])));
		return true;

	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {
		Coords center = getB().getInhomCoordsInD3();
		drawCoords[0] = center;
		drawCoords[1] = getA().getInhomCoordsInD3().sub(center);
		drawCoords[2] = getC().getInhomCoordsInD3().sub(center);

		return true;
	}

}
