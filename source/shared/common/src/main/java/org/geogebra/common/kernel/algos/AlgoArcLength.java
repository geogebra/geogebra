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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Length of an arc.
 * 
 * @author michael
 */
public class AlgoArcLength extends AlgoElement {

	private GeoConicPartND arc; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            cons
	 * @param label
	 *            label
	 * @param arc
	 *            partial conic
	 */
	public AlgoArcLength(Construction cons, String label, GeoConicPartND arc) {
		super(cons);
		this.arc = arc;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) arc;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return arc length
	 */
	public GeoNumeric getArcLength() {
		return num;
	}

	// calc length of vector v
	@Override
	public final void compute() {

		num.setValue(arc.getArcLength());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("ArcLengthOfA", "Arc length of %0",
				((GeoElement) arc).getLabel(tpl));

	}

}
