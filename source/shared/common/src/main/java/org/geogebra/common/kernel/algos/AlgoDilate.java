/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * @author Markus
 */
public class AlgoDilate extends AlgoTransformation {

	protected GeoPointND S;
	private Dilateable out;
	private NumberValue r;
	private GeoElement rgeo;

	/**
	 * Creates new labeled enlarge geo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            dilated geo
	 * @param r
	 *            coefficient
	 * @param S
	 *            dilation center
	 */
	AlgoDilate(Construction cons, String label, GeoElement A, GeoNumberValue r,
			GeoPointND S) {
		this(cons, A, r, S);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new unlabeled enlarge geo
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            dilated geo
	 * @param r
	 *            coefficient
	 * @param S
	 *            dilation center
	 */
	public AlgoDilate(Construction cons, GeoElement A, GeoNumberValue r,
			GeoPointND S) {
		super(cons);
		this.r = r;
		this.S = S;

		inGeo = A;
		rgeo = r.toGeoElement();

		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Dilateable) {
			out = (Dilateable) outGeo;
		}

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Dilate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DILATE_FROM_POINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[S == null ? 2 : 3];
		input[0] = inGeo;
		input[1] = rgeo;
		if (S != null) {
			input[2] = (GeoElement) S;
		}

		setOnlyOutput(outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting GeoElement
	 * 
	 * @return the resulting GeoElement
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList) && (outGeo instanceof Dilateable)) {
			out = (Dilateable) outGeo;
		}
	}

	// calc dilated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}

		setOutGeo();
		if (!out.isDefined()) {
			return;
		}

		out.dilate(r, getPointCoords());

		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		String sLabel = S == null ? cons.getOrigin().toValueString(tpl)
				: S.getLabel(tpl);
		return getLoc().getPlainDefault("ADilatedByFactorBfromC",
				"%0 dilated by factor %1 from %2", inGeo.getLabel(tpl),
				rgeo.getLabel(tpl), sLabel);

	}

	/**
	 * 
	 * @return point coords for dilate
	 */
	protected Coords getPointCoords() {
		if (S == null) {
			return Coords.O;
		}

		return S.getInhomCoords();
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart)) {
			super.transformLimitedPath(a, b);
		} else {
			super.transformLimitedConic(a, b);
		}

	}

	@Override
	public double getAreaScaleFactor() {
		return r.getDouble() * r.getDouble();
	}

}
