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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoDilate extends AlgoTransformation {

	private GeoPoint S;
	private Dilateable out;
	private NumberValue r;
	private GeoElement inGeo, outGeo, rgeo;

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
			GeoPoint S) {
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
			GeoPoint S) {
		super(cons);
		this.r = r;
		this.S = S;

		inGeo = A;
		rgeo = r.toGeoElement();
		if (A instanceof GeoPolygon || A instanceof GeoPolyLine
				|| A.isLimitedPath()) {
			outGeo = inGeo.copyInternal(cons);
			out = (Dilateable) outGeo;
		} else if (!A.isGeoList()) {
			// create output object
			outGeo = inGeo.copy();
			out = (Dilateable) outGeo;
		} else
			outGeo = new GeoList(cons);
		setInputOutput();
		compute();

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDilate;
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
			input[2] = S;

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
		outGeo.set(inGeo);
		if (S == null) {
			// Application.debug(cons.getOrigin());
			out.dilate(r, cons.getOrigin());
		} else
			out.dilate(r, S);
		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		String sLabel = S == null ? cons.getOrigin().toValueString(tpl) : S
				.getLabel(tpl);
		return app.getPlain("ADilatedByFactorBfromC", inGeo.getLabel(tpl),
				rgeo.getLabel(tpl), sLabel);

	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart))
			super.transformLimitedPath(a, b);
		else
			super.transformLimitedConic(a, b);

	}

	// TODO Consider locusequability
}
