/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

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
