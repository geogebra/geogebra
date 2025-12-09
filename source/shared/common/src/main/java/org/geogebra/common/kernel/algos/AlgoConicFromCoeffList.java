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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

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
		setOnlyOutput(conic);

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
		if (L.size() == 3) {
			conic.setCoeffs(getCoeff(0, 0),
					getCoeff(0, 1) + getCoeff(1, 0), getCoeff(1, 1),
					getCoeff(0, 2) + getCoeff(2, 0),
					getCoeff(1, 2) + getCoeff(2, 1), getCoeff(2, 2));
		} else if (L.size() == 6) {
			conic.setCoeffs(getCoeff(0), getCoeff(3), getCoeff(1), getCoeff(4),
					getCoeff(5), getCoeff(2));
		} else {
			conic.setUndefined();
		}
	}

	private double getCoeff(int i, int j) {
		return L.get(i).isGeoList()
				? ((GeoList) L.get(i)).get(j).evaluateDouble() : Double.NaN;
	}

	private double getCoeff(int i) {
		return L.get(i).evaluateDouble();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("ConicFromCoeffListA", "Conic from %0",
				L.getLabel(tpl));
	}

}
