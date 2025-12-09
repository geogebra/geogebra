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
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Take objects from the middle of a list
 * 
 * @author Michael Borcherds
 */

public class AlgoKeepIf extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private GeoFunction boolFun; // input
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param boolFun
	 *            boolean filter
	 * @param inputList
	 *            list
	 */
	public AlgoKeepIf(Construction cons, String label, GeoFunction boolFun,
			GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		this.boolFun = boolFun;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.KeepIf;
	}

	@Override
	protected void setInputOutput() {
		outputList.setTypeStringForXML(inputList.getTypeStringForXML());
		input = new GeoElement[2];
		input[0] = boolFun;
		input[1] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();

		if (!inputList.isDefined() || !boolFun.isBooleanFunction()) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (size == 0) {
			return;
		}
		/*
		 * If val is not numeric, we use the underlying Expression of the
		 * function and plug the list element as variable. Deep copy is needed
		 * so that we can plug the value repeatedly.
		 */
		FunctionVariable var = boolFun.getFunction().getFunctionVariable();
		try {
			for (int i = 0; i < size; i++) {
				GeoElement geo = inputList.get(i);
				if (geo.isGeoNumeric()) {
					if (boolFun
							.evaluateBoolean(((GeoNumeric) geo).getValue())) {
						outputList.add(geo.copyInternal(cons));
					}
				} else {
					ExpressionNode ex = boolFun.getFunction().getExpression()
							.deepCopy(kernel);
					ex = ex.replace(var,
							geo.evaluate(StringTemplate.defaultTemplate))
							.wrap();
					if (((MyBoolean) ex
							.evaluate(StringTemplate.defaultTemplate))
									.getBoolean()) {
						outputList.add(geo.copyInternal(cons));
					}
				}

			}
		} catch (MyError e) {
			// eg KeepIf[x<3,{1,2,(4,4)}]
			Log.debug(e);
			outputList.setUndefined();
		}
	}

}
