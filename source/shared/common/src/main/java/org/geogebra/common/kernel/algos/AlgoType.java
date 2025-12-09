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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Exposes conic / quadric type
 */
public class AlgoType extends AlgoElement {

	private final GeoQuadricND c; // input
	private final GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param c
	 *            quadric
	 */
	public AlgoType(Construction cons, GeoQuadricND c) {
		super(cons);
		this.c = c;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Type;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{c};
		setOnlyOutput(num);
		setDependencies();
	}

	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public final void compute() {
		if (c.isDefined()) {
			num.setValue(c.type);
		} else {
			num.setUndefined();
		}
	}

}
