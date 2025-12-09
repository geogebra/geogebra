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
 * @author Michael
 */
public abstract class AlgoPolarPointND extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoLineND line; // input
	protected GeoPointND polar; // output

	/** Creates new AlgoPolarLine */
	public AlgoPolarPointND(Construction cons, String label, GeoConicND c,
			GeoLineND line) {
		super(cons);
		this.line = line;
		this.c = c;
		polar = newGeoPoint(cons);

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
	abstract protected GeoPointND newGeoPoint(Construction cons1);

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
		input[0] = (GeoElement) line;
		input[1] = c;

		setOnlyOutput(polar);
		setDependencies(); // done by AlgoElement
	}

	// Made public for LocusEqu
	public GeoPointND getPoint() {
		return polar;
	}

	// Made public for LocusEqu
	public GeoConicND getConic() {
		return c;
	}

	public GeoLineND getLine() {
		return line;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("PoleOfLineARelativeToB",
				"Pole of line %0 relative to %1", line.getLabel(tpl),
				c.getLabel(tpl));

	}

}
