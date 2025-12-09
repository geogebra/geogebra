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
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Center of a quadric/conic
 */
public abstract class AlgoCenterQuadricND extends AlgoElement {

	protected GeoQuadricND c; // input
	protected GeoPointND midpoint; // output

	/**
	 * @param cons
	 *            construction
	 * @param c
	 *            quadric
	 */
	public AlgoCenterQuadricND(Construction cons,
			GeoQuadricND c) {
		super(cons);
		this.c = c;
		midpoint = newGeoPoint(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * @param cons1
	 *            construction
	 * @return new GeoPoint
	 */
	abstract public GeoPointND newGeoPoint(Construction cons1);

	@Override
	public Commands getClassName() {
		return Commands.Center;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_MIDPOINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOnlyOutput(midpoint);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPointND getPoint() {
		return midpoint;
	}

	@Override
	public final void compute() {
		if (!c.isDefined()) {
			midpoint.setUndefined();
			return;
		}

		setCoords();
	}

	/**
	 * set midpoint coords
	 */
	abstract protected void setCoords();

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("CenterOfA", "Center of %0",
				c.getLabel(tpl));
	}

}
