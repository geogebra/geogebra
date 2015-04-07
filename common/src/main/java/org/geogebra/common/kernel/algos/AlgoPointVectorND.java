/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

public abstract class AlgoPointVectorND extends AlgoElement {

	protected GeoPointND P; // input
	protected GeoVectorND v; // input
	protected GeoPointND Q; // output

	public AlgoPointVectorND(Construction cons, String label, GeoPointND P,
			GeoVectorND v) {
		super(cons);
		this.P = P;
		this.v = v;
		Q = newGeoPoint(cons);

		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();
		Q.setLabel(label);
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return new point
	 */
	abstract protected GeoPointND newGeoPoint(Construction cons1);

	@Override
	public Commands getClassName() {
		return Commands.Point;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_VECTOR_FROM_POINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) v;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) Q);
		setDependencies(); // done by AlgoElement
	}

	public GeoPointND getQ() {
		return Q;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlain("PointAplusB", input[0].getLabel(tpl),
				input[1].getLabel(tpl));

	}

	// TODO Consider locusequability
}
