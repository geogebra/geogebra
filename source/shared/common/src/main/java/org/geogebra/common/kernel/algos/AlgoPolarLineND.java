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
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
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
	 * @param cons1
	 *            construction
	 * @return new geo line
	 */
	abstract protected GeoLineND newGeoLine(Construction cons1);

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

		setOnlyOutput(polar);
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
		return getLoc().getPlainDefault("PolarLineOfARelativeToB",
				"Polar line of %0 relative to %1", P.getLabel(tpl),
				c.getLabel(tpl));

	}

}
