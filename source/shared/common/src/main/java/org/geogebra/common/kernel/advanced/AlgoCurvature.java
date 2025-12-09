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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 *          Calculate Curvature for function:
 */

public class AlgoCurvature extends AlgoElement {

	private GeoPointND A; // input
	private GeoFunction f;
	private GeoNumeric K; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoCurvature(Construction cons, String label, GeoPointND A,
			GeoFunction f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoCurvature(Construction cons, GeoPointND A, GeoFunction f) {
		super(cons);
		this.f = f;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Curvature;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A.toGeoElement();
		input[1] = f;

		setOnlyOutput(K);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return curvature
	 */
	public GeoNumeric getResult() {
		return K;
	}

	@Override
	public final void compute() {
		if (f.isDefined() && DoubleUtil.isZero(A.getInhomZ())) {
			K.setValue(f.evaluateCurvature(A.getInhomX()));
		} else {
			K.setUndefined();
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
	}

}