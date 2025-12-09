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
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;

/**
 * Converts lines, conics to function
 * 
 * @author Zbynek
 * @deprecated instead of converting line to function with this algo please make
 *             sure the receiver can handle both functions and lines
 */
@Deprecated
public class AlgoFunctionableToFunction extends AlgoElement {

	private GeoFunction outputFunction;
	private GeoFunctionable functionable;

	/**
	 * @param construction
	 *            construction
	 * @param functionable
	 *            geo to be converted to a function
	 */
	public AlgoFunctionableToFunction(Construction construction,
			GeoFunctionable functionable) {
		super(construction);
		this.functionable = functionable;
		Function expr = functionable.getFunction();
		outputFunction = new GeoFunction(cons, expr);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { functionable.toGeoElement() };
		setOnlyOutput(outputFunction);
		setDependencies();
	}

	@Override
	public void compute() {
		ExpressionNode newExpression = functionable.getFunction()
				.getFunctionExpression();
		outputFunction.getFunction().setExpression(newExpression);
		outputFunction.getFunction().initFunction();
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return functionable.getLabel(tpl);
	}

	/**
	 * @return output
	 */
	public GeoFunction getFunction() {
		return outputFunction;
	}

}
