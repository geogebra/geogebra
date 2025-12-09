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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 */
public class AlgoDiameterLine extends AlgoDiameterLineND {

	private GeoVector v;

	/**
	 * Creates new 2D algo for Diameter
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @param g
	 *            parallel line
	 */
	public AlgoDiameterLine(Construction cons, String label, GeoConicND c,
			GeoLineND g) {
		super(cons, label, c, g);
	}

	@Override
	protected void createOutput(Construction cons1) {
		diameter = new GeoLine(cons1);
		v = new GeoVector(cons1);
	}

	// calc diameter line of v relative to c
	@Override
	public final void compute() {
		((GeoLine) g).getDirection(v);
		c.diameterLine(v, (GeoLine) diameter);
	}

}
