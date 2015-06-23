/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoParabolaPointLine.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoParabolaPointLine extends AlgoParabolaPointLineND implements
		SymbolicParametersBotanaAlgo {

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	public AlgoParabolaPointLine(Construction cons, String label, GeoPointND F,
			GeoLineND l) {
		super(cons, label, F, l);
	}

	public AlgoParabolaPointLine(Construction cons, GeoPointND F, GeoLineND l) {
		super(cons, F, l);
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons) {
		return new GeoConic(cons);
	}

	// compute parabola with focus F and line l
	@Override
	public final void compute() {
		parabola.setParabola((GeoPoint) F, (GeoLine) l);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnParabolaPointLine(geo, this, scope);
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint F = (GeoPoint) this.F;
		GeoLine l = (GeoLine) this.l;

		if (F != null && l != null) {
			Variable[] vF = F.getBotanaVars(F);
			Variable[] vl = l.getBotanaVars(l);

			if (botanaVars == null) {
				botanaVars = new Variable[10];
				// P
				botanaVars[0] = new Variable();
				botanaVars[1] = new Variable();
				// T
				botanaVars[2] = new Variable();
				botanaVars[3] = new Variable();
				// A
				botanaVars[4] = vl[0];
				botanaVars[5] = vl[1];
				// B
				botanaVars[6] = vl[2];
				botanaVars[7] = vl[3];
				// F
				botanaVars[8] = vF[0];
				botanaVars[9] = vF[1];
			}

			botanaPolynomials = new Polynomial[3];

			// |FP| = |PT|
			botanaPolynomials[0] = Polynomial.equidistant(vF[0], vF[1],
					botanaVars[0], botanaVars[1], botanaVars[2], botanaVars[3]);

			// A,T,B collinear
			botanaPolynomials[1] = Polynomial.collinear(vl[0], vl[1], vl[2],
					vl[3], botanaVars[2], botanaVars[3]);

			// PT orthogonal AB
			botanaPolynomials[2] = Polynomial.perpendicular(botanaVars[0],
					botanaVars[1], botanaVars[2], botanaVars[3], vl[0], vl[1],
					vl[2], vl[3]);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();

	}
}
