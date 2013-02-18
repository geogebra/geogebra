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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoAnglePoints extends AlgoElement implements
		DrawInformationAlgo, AngleAlgo {

	private GeoPointND An, Bn, Cn; // input
	private GeoAngle angle; // output

	/** standard normal vector */
	private static final Coords STANDARD_VN = new Coords(0, 0, 1, 0);

	private AlgoAnglePolygon algoAnglePoly;

	transient private double bx, by, vx, vy, wx, wy;

	public AlgoAnglePoints(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, A, B, C);
		angle.setLabel(label);
	}

	AlgoAnglePoints(Construction cons, AlgoAnglePolygon algoAnglePoly,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		this(cons, A, B, C);
		this.algoAnglePoly = algoAnglePoly;
	}

	@Override
	public Commands getClassName() {
		return Commands.Angle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ANGLE;
	}

	public AlgoAnglePoints(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		super(cons);
		this.An = A;
		this.Bn = B;
		this.Cn = C;
		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	protected GeoAngle newGeoAngle(Construction cons) {
		return new GeoAngle(cons);
	}

	public AlgoAnglePoints(GeoPointND A, GeoPointND B, GeoPointND C,
			Construction cons) {
		super(cons);
		this.cons = cons;
		this.An = A;
		this.Bn = B;
		this.Cn = C;

	}

	public AlgoAnglePoints(GeoPointND A, GeoPointND B, GeoPointND C) {
		super(A.toGeoElement().cons, false);
		this.An = A;
		this.Bn = B;
		this.Cn = C;
	}

	public AlgoAnglePoints copy() {
		return new AlgoAnglePoints(An.copy(),
				Bn.copy(), Cn.copy());
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
		if(removed)
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

	public Coords getVn() {
		return STANDARD_VN;
	}

	// calc angle between vectors A-B and C-B
	// angle in range [0, pi]
	@Override
	public void compute() {

		GeoPoint A = (GeoPoint) An;
		GeoPoint B = (GeoPoint) Bn;
		GeoPoint C = (GeoPoint) Cn;

		if (!A.isFinite() || !B.isFinite() || !C.isFinite()) {
			angle.setUndefined(); // undefined
			return;
		}

		// get vectors v=BA and w=BC
		bx = B.inhomX;
		by = B.inhomY;
		vx = A.inhomX - bx;
		vy = A.inhomY - by;
		wx = C.inhomX - bx;
		wy = C.inhomY - by;

		if (Kernel.isZero(vx) && Kernel.isZero(vy)
				|| Kernel.isZero(wx) && Kernel.isZero(wy)) {
			angle.setUndefined();
			return;
		}

		// |v| * |w| * sin(alpha) = det(v, w)
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// => tan(alpha) = det(v, w) / v . w
		double det = vx * wy - vy * wx;
		double prod = vx * wx + vy * wy;
		double value = Math.atan2(det, prod);

		angle.setValue(value);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		if (algoAnglePoly != null) {
			return loc.getPlain("AngleBetweenABCofD", An.getLabel(tpl), Bn
					.getLabel(tpl), Cn.getLabel(tpl), algoAnglePoly.getPolygon()
					.getNameDescription());
		}
		return loc.getPlain("AngleBetweenABC", An.getLabel(tpl),
				Bn.getLabel(tpl), Cn.getLabel(tpl));
	}

	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
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

		drawable.setMaxRadius(0.5 * Math.sqrt(Math.min(
				firstVecScreen[0] * firstVecScreen[0] + firstVecScreen[1]
						* firstVecScreen[1], secondVecScreen[0]
						* secondVecScreen[0] + secondVecScreen[1]
						* secondVecScreen[1])));
		return true;

	}

	// TODO Consider locusequability
}
