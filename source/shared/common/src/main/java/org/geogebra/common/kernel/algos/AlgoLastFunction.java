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
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;

/**
 * Take last element from a data function
 * 
 */

public class AlgoLastFunction extends AlgoElement {

	private GeoFunction function;
	private GeoNumeric result;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputFn
	 *            data function
	 */
	public AlgoLastFunction(Construction cons, String label,
			GeoFunction inputFn) {
		super(cons);
		this.function = inputFn;
		this.result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.Last;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = function;

		super.setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {
		result.setUndefined();
		if (!function.isDefined()) {
			return;
		}
		ExpressionNode exp = this.function.getFunctionExpression().unwrap()
				.wrap();
		if (exp.getOperation() != Operation.DATA) {
			return;
		}
		ExpressionValue ev = ((MyNumberPair) exp.getRight()).getY();
		if (!(ev instanceof ListValue) || ((ListValue) ev).size() < 1) {
			return;
		}

		result.setValue(((ListValue) ev)
				.get(((ListValue) ev).size() - 1).evaluateDouble());

	}

	/**
	 * @return last element of data function
	 */
	public GeoNumeric getResult() {
		return result;
	}

}
