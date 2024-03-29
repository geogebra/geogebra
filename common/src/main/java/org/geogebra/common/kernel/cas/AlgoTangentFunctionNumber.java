/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTangents.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * 
 * @author Markus
 */
public class AlgoTangentFunctionNumber extends AlgoElement
		implements TangentAlgo {

	private GeoNumberValue n; // input
	private GeoElement ngeo;
	private GeoFunctionable f; // input
	private GeoLine tangent; // output

	private GeoPoint T;
	private final NoCASDerivativeCache cache;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param n
	 *            function parameter
	 * @param f
	 *            function
	 */
	public AlgoTangentFunctionNumber(Construction cons, String label,
			GeoNumberValue n, GeoFunctionable f) {
		super(cons);
		this.n = n;
		ngeo = n.toGeoElement();
		this.f = f;

		tangent = new GeoLine(cons);
		T = new GeoPoint(cons);
		tangent.setStartPoint(T);

		// derivative of f
		// now uses special non-CAS version of algo
		cache = new NoCASDerivativeCache(f);

		setInputOutput(); // for AlgoElement
		compute();
		tangent.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = ngeo;
		input[1] = f.toGeoElement();

		setOnlyOutput(tangent);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting tangent
	 */
	public GeoLine getTangent() {
		return tangent;
	}

	/**
	 * @return input function
	 */
	GeoFunctionable getFunction() {
		return f;
	}

	// calc tangent at x=a
	@Override
	public final void compute() {
		double a = n.getDouble();
		if (!f.isDefined() || Double.isInfinite(a)
				|| Double.isNaN(a)) {
			tangent.setUndefined();
			return;
		}

		// calc the tangent;
		double fa = f.value(a);
		double slope = cache.evaluateDerivative(a);
		tangent.setCoords(-slope, 1.0, a * slope - fa);
		T.setCoords(a, fa, 1.0);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("TangentToAatB", "Tangent to %0 at %1",
				f.getLabel(tpl),
				"x = " + ngeo.getLabel(tpl));
	}

	@Override
	public GeoPoint getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == f && line == tangent) {
			return T;
		}
		return null;
	}

}
