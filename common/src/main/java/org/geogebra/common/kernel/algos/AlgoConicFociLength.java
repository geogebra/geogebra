/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * 
 * @author Markus
 */
public abstract class AlgoConicFociLength extends AlgoConicFociLengthND
		implements SymbolicParametersBotanaAlgo {
	private BotanaEllipseHyperbolaLength botanaParams;
	public AlgoConicFociLength(
			// package private
			Construction cons, String label, GeoPointND A, GeoPointND B,
			GeoNumberValue a) {
		super(cons, label, A, B, a, null);
	}

	@Override
	protected void setOrientation(GeoDirectionND orientation) {
		// no need in 2D
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons1) {
		return new GeoConic(cons1);
	}

	@Override
	protected void setInput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = ageo;
	}

	@Override
	protected GeoPoint getA2d() {
		return (GeoPoint) A;
	}

	@Override
	protected GeoPoint getB2d() {
		return (GeoPoint) B;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (botanaParams == null) {
			botanaParams = new BotanaEllipseHyperbolaLength();
		}
		return botanaParams.getVars();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaParams == null) {
			botanaParams = new BotanaEllipseHyperbolaLength();
		}
		return botanaParams.getBotanaPolynomials(getFocus1(), getFocus2(), a);
	}
}
