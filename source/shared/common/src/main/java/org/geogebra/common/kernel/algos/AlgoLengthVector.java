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
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.util.MyMath;

/**
 * Length of a vector or point.
 * 
 * @author Markus
 */
public class AlgoLengthVector extends AlgoElement {

	private GeoVec3D v; // input
	private GeoNumeric num; // output

	private double[] coords = new double[2];

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param v
	 *            vector
	 */
	public AlgoLengthVector(Construction cons, String label, GeoVec3D v) {
		super(cons);
		this.v = v;
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
		input[0] = v;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return vector length / point abs. value
	 */
	public GeoNumeric getLength() {
		return num;
	}

	GeoVec3D getv() {
		return v;
	}

	// calc length of vector v
	@Override
	public final void compute() {
		v.getInhomCoords(coords);
		num.setValue(MyMath.length(coords[0], coords[1]));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("LengthOfA", "Length of %0",
				v.getLabel(tpl));

	}

}
