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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumeratorDenominator extends AlgoElement {

	private GeoNumeric f; // input
	private GeoNumeric g; // output
	private Commands type;

	public AlgoNumeratorDenominator(Construction cons, String label,
			GeoNumeric f, Commands type) {
		this(cons, f, type);
		g.setLabel(label);
	}

	public AlgoNumeratorDenominator(Construction cons, GeoNumeric f,
			Commands type) {
		super(cons);
		this.f = f;
		this.type = type;
		g = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return type;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { f };

		super.setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	public GeoElement getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}
		double[] frac = AlgoFractionText.DecimalToFraction(f.getDouble(),
				Kernel.STANDARD_PRECISION);
		if (frac.length < 2) {
			g.setUndefined();
			return;
		}
		g.setValue(frac[type == Commands.Numerator ? 0 : 1]);

	}

	// TODO Consider locusequability

}
