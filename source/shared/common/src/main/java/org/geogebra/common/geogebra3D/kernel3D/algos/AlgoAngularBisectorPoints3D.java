/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngularBisector.java
 *
 * Created on 26. October 2001
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author Markus
 */
public class AlgoAngularBisectorPoints3D extends AlgoElement3D {
	// input
	private GeoPointND A;
	private GeoPointND B;
	private GeoPointND C;
	// output
	private GeoLine3D bisector;

	private Coords o = new Coords(3);
	private Coords d = new Coords(3);
	private Coords v1 = new Coords(3);

	/**
	 * Creates new AlgoLineBisector
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 */
	public AlgoAngularBisectorPoints3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		this(cons, label, A, B, C, null);
	}

	/**
	 * Creates new AlgoLineBisector
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param orientation
	 *            direction
	 */
	protected AlgoAngularBisectorPoints3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		bisector = new GeoLine3D(cons);
		bisector.setStartPoint(B);
		setInput(orientation); // for AlgoElement
		setOutput(); // for AlgoElement

		// compute bisector of angle(A, B, C)
		compute();
		bisector.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AngularBisector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ANGULAR_BISECTOR;
	}

	// for AlgoElement
	/**
	 * set input
	 * 
	 * @param orientation
	 *            orientation
	 */
	protected void setInput(GeoDirectionND orientation) {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = (GeoElement) C;
	}

	/**
	 * set output
	 */
	private void setOutput() {
		setOnlyOutput(bisector);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoLine3D getLine() {
		return bisector;
	}

	// Made public for LocusEqu
	/**
	 * @return leg
	 */
	protected GeoPointND getA() {
		return A;
	}

	// Made public for LocusEqu
	/**
	 * @return vertex
	 */
	protected GeoPointND getB() {
		return B;
	}

	// Made public for LocusEqu
	/**
	 * @return leg
	 */
	protected GeoPointND getC() {
		return C;
	}

	@Override
	public final void compute() {
		boolean infiniteB = B.isInfinite();

		// set direction vector of bisector: (wx, wy)
		if (infiniteB) {
			// if B is at infinity then use it for direction
			// and midpoint(A,B) for start point
			o.setAdd3(A.getInhomCoordsInD3(), C.getInhomCoordsInD3())
					.mulInside3(0.5);
			d.set3(B.getCoordsInD3());
			bisector.setCoord(o, d);
		}
		// standard case: B is not at infinity
		else {
			o.set3(B.getInhomCoordsInD3());
			v1.setSub3(A.getInhomCoordsInD3(), o);
			v1.normalize();
			d.setSub3(C.getInhomCoordsInD3(), o);
			d.normalize();
			d.setAdd3(v1, d);
			setCoordFromFiniteB(o, d, v1);
		}
	}

	/**
	 * set bisector coords when B is finite
	 * 
	 * @param o
	 *            origin
	 * @param d
	 *            direction
	 * @param v1
	 *            direction BA
	 */
	protected void setCoordFromFiniteB(Coords o, Coords d, Coords v1) {
		bisector.setCoord(o, d);
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleBisectorOfABC", A.getLabel(tpl),
				B.getLabel(tpl), C.getLabel(tpl));

	}

	/*
	 * @Override public boolean isLocusEquable() { return true; }
	 * 
	 * public EquationElementInterface buildEquationElementForGeo(GeoElement
	 * geo, EquationScopeInterface scope) { return
	 * LocusEquation.eqnAngularBisectorPoints(geo, this, scope); }
	 */
}
