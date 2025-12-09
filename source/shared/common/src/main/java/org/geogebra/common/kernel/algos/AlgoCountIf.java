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
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param boolFun
	 *            filter function
	 * @param list
	 *            filtered list
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

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return object count
	 */
	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {
		try {

			int count = 0;
			/*
			 * If val is not numeric, we use the underlying Expression of the
			 * function and plug the list element as variable. Deep copy is
			 * needed so that we can plug the value repeatedly.
			 */
			FunctionVariable var = boolFun.getFunction().getFunctionVariable();

			for (int i = 0; i < list.size(); i++) {
				GeoElement val = list.get(i);
				if (val.isGeoNumeric()) {
					if (boolFun.evaluateBoolean(((GeoNumeric) val).getValue())) {
						count++;
					}
				} else {
					ExpressionNode ex = boolFun.getFunction()
							.getExpression().deepCopy(kernel);
					ex = ex.replace(var,
							val.evaluate(StringTemplate.defaultTemplate))
							.wrap();
					if (((MyBoolean) ex
							.evaluate(StringTemplate.defaultTemplate))
									.getBoolean()) {
						count++;
					}
				}
			}
			result.setValue(count);

		} catch (Exception e) {
			result.setUndefined();
		}
	}

}
