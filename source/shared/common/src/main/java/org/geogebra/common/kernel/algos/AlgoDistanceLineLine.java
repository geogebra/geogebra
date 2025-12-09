/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
