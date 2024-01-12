/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDistanceLineLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 *
 * @author Markus
 */
public class AlgoDistanceLineLine extends AlgoElement {

	private GeoLine g; // input
	private GeoLine h; // input
	/** output: distance */
	protected GeoNumeric dist;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param g
	 *            line #1
	 * @param h
	 *            line #2
	 */
	public AlgoDistanceLineLine(Construction cons, String label, GeoLine g,
			GeoLine h) {
		super(cons);
		this.h = h;
		this.g = g;
		dist = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		dist.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DISTANCE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = h;
		input[1] = g;

		setOnlyOutput(dist);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoNumeric getDistance() {
		return dist;
	}

	// calc length of vector v
	@Override
	public void compute() {
		dist.setValue(g.distance(h));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DistanceOfAandB",
				"Distance between %0 and %1", g.getLabel(tpl),
				h.getLabel(tpl));
	}

}
