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
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public abstract class AlgoAnglePointsND extends AlgoAngle implements
		DrawInformationAlgo {

	protected GeoPointND An, Bn, Cn; // input
	protected GeoAngle angle; // output

	protected AlgoAnglePolygon algoAnglePoly;

	transient protected double bx, by, vx, vy, wx, wy;

	public AlgoAnglePointsND(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		this(cons, A, B, C, orientation);
		angle.setLabel(label);
	}

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

		this.An = A;
		this.Bn = B;
		this.Cn = C;
	}

	/**
	 * used as a helper algo (for AlgoAnglePolygon)
	 * 
	 * @param cons
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
	 * @param B
	 * @param C
	 */
	public void setABC(GeoPointND A, GeoPointND B, GeoPointND C) {
		this.An = A;
		this.Bn = B;
		this.Cn = C;
	}

	public AlgoAnglePointsND(GeoPointND A, GeoPointND B, GeoPointND C,
			Construction cons) {
		super(cons);
		this.cons = cons;
		this.An = A;
		this.Bn = B;
		this.Cn = C;

	}

	public AlgoAnglePointsND(GeoPointND A, GeoPointND B, GeoPointND C) {
		super(A.toGeoElement().cons, false);
		this.An = A;
		this.Bn = B;
		this.Cn = C;
	}

	protected AlgoAnglePointsND(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	void setAlgoAnglePolygon(AlgoAnglePolygon algo) {
		algoAnglePoly = algo;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) An;
		input[1] = (GeoElement) Bn;
		input[2] = (GeoElement) Cn;

		setOutputLength(1);
		setOutput(0, angle);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void remove() {
		if (removed)
			return;
		if (algoAnglePoly != null)
			algoAnglePoly.remove();
		else
			super.remove();
	}

	@Override
	public int getConstructionIndex() {
		if (algoAnglePoly != null) {
			return algoAnglePoly.getConstructionIndex();
		}
		return super.getConstructionIndex();
	}

	public GeoAngle getAngle() {
		return angle;
	}

	public GeoPointND getA() {
		return An;
	}

	public GeoPointND getB() {
		return Bn;
	}

	public GeoPointND getC() {
		return Cn;
	}

	@Override
	public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		if (algoAnglePoly != null) {
			return getLoc().getPlain("AngleBetweenABCofD", An.getLabel(tpl),
					Bn.getLabel(tpl), Cn.getLabel(tpl),
					algoAnglePoly.getPolygon().getNameDescription());
		}
		return getLoc().getPlain("AngleBetweenABC", An.getLabel(tpl),
				Bn.getLabel(tpl), Cn.getLabel(tpl));
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		Coords v = drawable.getCoordsInView(Bn);
		if (!drawable.inView(v)) {
			return false;
		}

		m[0] = v.get()[0];
		m[1] = v.get()[1];

		Coords ptCoords = drawable.getCoordsInView(An);
		if (!drawable.inView(ptCoords)) {
			return false;
		}
		Coords coords2 = drawable.getCoordsInView(Cn);
		if (!drawable.inView(coords2)) {
			return false;
		}

		// first vec
		firstVec[0] = ptCoords.getX() - m[0];
		firstVec[1] = ptCoords.getY() - m[1];

		double vertexScreen[] = new double[2];
		vertexScreen[0] = m[0];
		vertexScreen[1] = m[1];

		double firstVecScreen[] = new double[2];
		firstVecScreen[0] = ptCoords.getX();
		firstVecScreen[1] = ptCoords.getY();

		double secondVecScreen[] = new double[2];
		secondVecScreen[0] = coords2.getX();
		secondVecScreen[1] = coords2.getY();

		drawable.toScreenCoords(vertexScreen);
		drawable.toScreenCoords(firstVecScreen);
		drawable.toScreenCoords(secondVecScreen);

		firstVecScreen[0] -= vertexScreen[0];
		firstVecScreen[1] -= vertexScreen[1];
		secondVecScreen[0] -= vertexScreen[0];
		secondVecScreen[1] -= vertexScreen[1];

		drawable.setMaxRadius(0.5 * Math.sqrt(Math.min(firstVecScreen[0]
				* firstVecScreen[0] + firstVecScreen[1] * firstVecScreen[1],
				secondVecScreen[0] * secondVecScreen[0] + secondVecScreen[1]
						* secondVecScreen[1])));
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

	// TODO Consider locusequability

}
