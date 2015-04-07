/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoPolarLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoPolarLineND extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoPointND P; // input
	protected GeoLineND polar; // output

	/** Creates new AlgoPolarLine */
	public AlgoPolarLineND(Construction cons, String label, GeoConicND c,
			GeoPointND P) {
		super(cons);
		this.P = P;
		this.c = c;
		polar = newGeoLine(cons);

		setInputOutput(); // for AlgoElement

		compute();
		polar.setLabel(label);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new geo line
	 */
	abstract protected GeoLineND newGeoLine(Construction cons);

	@Override
	public Commands getClassName() {
		return Commands.Polar;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POLAR_DIAMETER;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = c;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) polar);
		setDependencies(); // done by AlgoElement
	}

	// Made public for LocusEqu
	public GeoPointND getPoint() {
		return P;
	}

	// Made public for LocusEqu
	public GeoConicND getConic() {
		return c;
	}

	public GeoLineND getLine() {
		return polar;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("PolarLineOfARelativeToB", P.getLabel(tpl),
				c.getLabel(tpl));

	}

}
