/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * @since 4.0
 * @author Tam
 * @version 2012-03-20
 */
public class AlgoConicFromCoeffList extends AlgoElement {

	private GeoList L; // input A list of 6 coeffs
	private GeoConic conic; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param L
	 *            coefficients
	 */
	public AlgoConicFromCoeffList(Construction cons, String label, GeoList L) {
		super(cons);
		this.L = L;
		conic = new GeoConic(cons);

		setInputOutput(); // for AlgoElement
		compute();
		conic.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.Conic;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { L };

		setOutputLength(1);
		setOutput(0, conic);

		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output conic
	 */
	public GeoConic getConic() {
		return conic;
	}

	/**
	 * @return input coefficient list
	 */
	GeoList getCoeffList() {
		return L;
	}

	@Override
	public final void compute() {
		if (L.size() != 6) {
			conic.setUndefined();
		} else {
			conic.setCoeffs(((GeoNumeric) L.get(0)).getDouble(),
					((GeoNumeric) L.get(3)).getDouble(),
					((GeoNumeric) L.get(1)).getDouble(),
					((GeoNumeric) L.get(4)).getDouble(),
					((GeoNumeric) L.get(5)).getDouble(),
					((GeoNumeric) L.get(2)).getDouble());
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("ConicFromCoeffListA", L.getLabel(tpl));
	}

	// TODO Consider locusequability
}
