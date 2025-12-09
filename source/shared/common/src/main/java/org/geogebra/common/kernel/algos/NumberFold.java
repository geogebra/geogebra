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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Helper for Sum/Product when numbers are involved
 */
public class NumberFold implements FoldComputer {

	private GeoNumeric result;
	private double x;

	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.result = new GeoNumeric(cons);
	}

	@Override
	public void add(GeoElement p, Operation op) {
		if (op == Operation.MULTIPLY) {
			x *= p.evaluateDouble();
		} else {
			x += p.evaluateDouble();
		}
	}

	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		x = geoElement.evaluateDouble();
	}

	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement instanceof GeoNumberValue;
	}

	@Override
	public void finish() {
		result.setValue(x);

	}

}
