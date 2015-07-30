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
 * Take first n objects from a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoLastFunction extends AlgoElement {

	private GeoFunction function;
	private GeoNumeric result;

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
		if(!function.isDefined()){
			return;
		}
		ExpressionNode exp = this.function.getFunctionExpression().unwrap()
				.wrap();
		if(exp.getOperation() != Operation.DATA){
			return;
		}
		ExpressionValue ev = ((MyNumberPair) exp.getRight()).getY();
		if (!(ev instanceof ListValue) || ((ListValue) ev).size() < 1) {
			return;
		}

		result.setValue(((ListValue) ev).getListElement(
				((ListValue) ev).size() - 1).evaluateDouble());
		
	}

	public GeoNumeric getResult() {
		return result;
	}

}
