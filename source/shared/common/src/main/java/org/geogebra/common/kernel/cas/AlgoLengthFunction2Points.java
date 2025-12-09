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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Calculate Function Length between the points A and B: integral from
 * A to B on T = sqrt(1+(f')^2)
 * @author Victor Franco Espino
 */

public class AlgoLengthFunction2Points extends AlgoUsingTempCASalgo {

	private GeoPointND A;
	private GeoPointND B; // input
	private GeoFunction f;
	private GeoNumeric length; // output
	private LengthFunction lengthFunction; // is T = sqrt(1+(f')^2)

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param A
	 *            start point
	 * @param B
	 *            end point
	 */
	public AlgoLengthFunction2Points(Construction cons, String label,
			GeoFunction f, GeoPointND A, GeoPointND B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.f = f;
		length = new GeoNumeric(cons);

		refreshCASResults();

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f;
		input[1] = A.toGeoElement();
		input[2] = B.toGeoElement();

		setOnlyOutput(length);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting length
	 */
	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		double a = A.getInhomCoordsInD3().getX();
		double b = B.getInhomCoordsInD3().getX();
		length.setValue(lengthFunction.integral(a, b));
	}

	// locusequability makes no sense here

	@Override
	public void refreshCASResults() {
		// First derivative of function f
		algoCAS = new AlgoDerivative(cons, f, null, null, true,
				new EvalInfo(false));
		cons.removeFromConstructionList(algoCAS);
		GeoFunction f1 = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();
		lengthFunction = new LengthFunction(f1, f);
	}
}