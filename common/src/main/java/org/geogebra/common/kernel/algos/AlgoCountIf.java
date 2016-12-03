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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 *
 * @author Michael
 */
public class AlgoCountIf extends AlgoElement {

	private GeoFunction boolFun; // input
	private GeoList list;
	private GeoNumeric result; // output

	/**
	 * Algorithm for handling of a CountIf construct
	 */
	public AlgoCountIf(Construction cons, String label, GeoFunction boolFun,
			GeoList list) {
		super(cons);
		this.boolFun = boolFun;
		this.list = list;

		// create output GeoElement of same type as ifGeo
		result = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.CountIf;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = boolFun;
		input[1] = list;

		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {
		try {

			int count = 0;
			// <Zbynek Konecny 2010-04-15>
			/*
			 * If val is not numeric, we use the underlying Expression of the
			 * function and plug the list element as variable. Deep copy is
			 * needed so that we can plug the value repeatedly.
			 */
			FunctionVariable var = boolFun.getFunction().getFunctionVariable();

			for (int i = 0; i < list.size(); i++) {
				GeoElement val = list.get(i);
				if (val.isGeoNumeric()) {
					if (boolFun.evaluateBoolean(((GeoNumeric) val).getValue()))
						count++;
				} else {
					ExpressionNode ex = (ExpressionNode) boolFun.getFunction()
							.getExpression().deepCopy(kernel);
					ex = ex.replace(var,
							val.evaluate(StringTemplate.defaultTemplate))
							.wrap();
					if (((MyBoolean) ex
							.evaluate(StringTemplate.defaultTemplate))
							.getBoolean())
						count++;
				}
			}
			// </Zbynek>
			result.setValue(count);

		} catch (Exception e) {
			result.setUndefined();
		}
	}

	
}
