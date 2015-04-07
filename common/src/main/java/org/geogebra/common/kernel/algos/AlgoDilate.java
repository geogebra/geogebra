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
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoDilate extends AlgoTransformation {

	protected GeoPointND S;
	private Dilateable out;
	private NumberValue r;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	private GeoElement rgeo;

	/**
	 * Creates new labeled enlarge geo
	 * 
	 * @param cons
	 * @param label
	 * @param A
	 * @param r
	 * @param S
	 */
	AlgoDilate(Construction cons, String label, GeoElement A, NumberValue r,
			GeoPointND S) {
		this(cons, A, r, S);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new unlabeled enlarge geo
	 * 
	 * @param cons
	 * @param A
	 * @param r
	 * @param S
	 */
	public AlgoDilate(Construction cons, GeoElement A, NumberValue r,
			GeoPointND S) {
		super(cons);
		this.r = r;
		this.S = S;

		inGeo = A;
		rgeo = r.toGeoElement();

		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Dilateable)
			out = (Dilateable) outGeo;

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
		if (S != null)
			input[2] = (GeoElement) S;

		setOutputLength(1);
		setOutput(0, outGeo);
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
		if (!(outGeo instanceof GeoList))
			out = (Dilateable) outGeo;
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

		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	/**
	 * set inGeo to outGeo
	 */
	protected void setOutGeo() {
		outGeo.set(inGeo);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		String sLabel = S == null ? cons.getOrigin().toValueString(tpl) : S
				.getLabel(tpl);
		return getLoc().getPlain("ADilatedByFactorBfromC", inGeo.getLabel(tpl),
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
		if (!(a instanceof GeoConicPart))
			super.transformLimitedPath(a, b);
		else
			super.transformLimitedConic(a, b);

	}

	@Override
	public double getAreaScaleFactor() {
		return r.getDouble() * r.getDouble();
	}

	// TODO Consider locusequability
}
