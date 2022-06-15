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
import org.geogebra.common.kernel.algos.AlgoIterationList.Type;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Iteration[ f(x), x0, n ]
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIteration extends AlgoElement {

	private GeoFunction f; // input
	private GeoNumberValue startValue;
	private GeoNumberValue n;
	private GeoElement startValueGeo;
	private GeoElement nGeo;
	private GeoElement result; // output
	private GeoFunctionNVar fNVar;

	private GeoElement expression; // input expression dependent on var
	private GeoElement[] vars; // input: local variable
	private int varCount;
	private GeoList[] over;
	private boolean isEmpty;
	private AlgoElement expressionParentAlgo;
	AlgoIterationList.Type type;
	boolean updateRunning = false;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param startValue
	 *            initial value
	 * @param n
	 *            number of iterations
	 */
	public AlgoIteration(Construction cons, String label, GeoFunction f,
			GeoNumberValue startValue, GeoNumberValue n) {
		super(cons);
		this.f = f;
		this.startValue = startValue;
		startValueGeo = startValue.toGeoElement();
		this.n = n;
		nGeo = n.toGeoElement();

		result = new GeoNumeric(cons);
		type = Type.SIMPLE;
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function(last, last index)
	 * @param startValue
	 *            initial values
	 * @param n
	 *            number of iterations
	 */
	public AlgoIteration(Construction cons, String label, GeoFunctionNVar f,
			GeoList startValue, GeoNumberValue n) {
		super(cons);
		this.fNVar = f;
		// this.startValue = startValue;
		startValueGeo = startValue.toGeoElement();
		this.n = n;
		nGeo = n.toGeoElement();

		result = new GeoNumeric(cons);
		type = Type.DOUBLE;
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param expression
	 *            expression
	 * @param vars
	 *            variables
	 * @param over
	 *            initial values
	 * @param n
	 *            number of iterations
	 */
	public AlgoIteration(Construction cons, GeoElement expression,
			GeoElement[] vars, GeoList[] over, GeoNumberValue n) {
		super(cons);
		this.expression = expression;
		this.vars = vars;
		this.over = over;
		this.n = n;
		this.nGeo = n.toGeoElement();
		type = Type.DEFAULT;

		varCount = vars.length;

		expressionParentAlgo = expression.getParentAlgorithm();

		result = expression.copyInternal(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Iteration;
	}

	@Override
	protected void setInputOutput() {
		if (type == Type.SIMPLE) {
			input = new GeoElement[3];
			input[0] = f;
			input[1] = startValueGeo;
			input[2] = nGeo;

			// done by AlgoElement
		} else if (type == Type.DOUBLE) {
			input = new GeoElement[3];
			input[0] = fNVar;
			input[1] = startValueGeo;
			input[2] = nGeo;

			// done by AlgoElement
		} else {
			input = new GeoElement[3 + varCount];
			input[0] = expression;
			for (int i = 0; i < varCount; i++) {
				input[i + 1] = vars[i];

			}
			input[1 + varCount] = over[0];
			input[2 + varCount] = nGeo;

		} // done by AlgoElement
		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies();
	}

	/**
	 * @return iteration result
	 */
	public GeoElement getResult() {
		return result;
	}

	private final void computeSimple() {
		if (!f.isDefined() || !startValueGeo.isDefined() || !nGeo.isDefined()) {
			result.setUndefined();
			return;
		}

		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0) {
			result.setUndefined();
			return;
		}

		// perform iteration f(f(f(...(startValue))))
		double val = startValue.getDouble();
		for (int i = 0; i < iterations; i++) {
			val = f.value(val);
		}
		((GeoNumeric) result).setValue(val);
	}

	private final void computeDouble() {
		if (!fNVar.isDefined() || !startValueGeo.isDefined()
				|| !nGeo.isDefined() || ((GeoList) startValueGeo).size() != 2) {
			result.setUndefined();
			return;
		}

		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0) {
			result.setUndefined();
			return;
		}

		// perform iteration f(f(f(...(startValue))))
		double val = ((GeoList) startValueGeo).get(0).evaluateDouble();
		double offset = Math
				.round(((GeoList) startValueGeo).get(1).evaluateDouble());
		for (int i = 0; i < iterations; i++) {
			val = fNVar.evaluate(offset + i, val);
		}
		((GeoNumeric) result).setValue(val);
	}

	@Override
	public final void compute() {
		if (type == Type.SIMPLE) {
			computeSimple();
			return;
		}
		if (type == Type.DOUBLE) {
			computeDouble();
			return;
		}
		if (updateRunning) {
			return;
		}
		updateRunning = true;

		for (int i = 2; i < input.length - 1; i += 2) {
			if (!input[i].isDefined()) {
				result.setUndefined();
				updateRunning = false;
				return;
			}
		}
		// list.setDefined(true);

		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0 || varCount > over[0].size()) {
			updateRunning = false;
			result.setUndefined();
			return;
		}

		isEmpty = over[0].size() == 0;

		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// update list
		updateListItems();

		// revert label creation setting
		cons.setSuppressLabelCreation(oldSuppressLabels);
		updateRunning = false;
	}

	@Override
	public GeoElementND[] getInputForUpdateSetPropagation() {
		if (type != Type.DEFAULT) {
			return super.getInputForUpdateSetPropagation();
		}
		GeoElement[] realInput = new GeoElement[3];
		realInput[0] = expression;
		realInput[1] = over[0];
		realInput[2] = nGeo;

		return realInput;
	}

	private void updateListItems() {
		if (isEmpty) {
			return;
		}

		int listSize = (int) Math.round(n.getDouble());

		if (listSize < over[0].size()) {
			result.set(over[0].get(listSize));
			return;
		}
		for (int j = 1; j < varCount; j++) {
			vars[j].set(over[0].get(over[0].size() - varCount + j - 1));
		}

		GeoElement listElement = over[0].get(over[0].size() - 1)
				.copyInternal(cons);
		int i = over[0].size();
		while (i <= listSize) {
			// check we haven't run out of memory

			// set local var value
			// updateLocalVar(currentVal);
			updateLocalVar(i, listElement);

			// copy expression value to listElement
			// if it's undefined, just copy the undefined property
			if (expression.isDefined()) {
				//
				if (listElement.isGeoList()) {
					for (int j = 0; j < varCount; j++) {
						((GeoList) listElement)
								.replaceChildrenByValues(vars[j]);
					}
				}
			} else {
				listElement.setUndefined();
			}
			if (listElement instanceof GeoNumeric && listElement
					.getDrawAlgorithm() instanceof DrawInformationAlgo) {
				listElement.setDrawAlgorithm(
						((DrawInformationAlgo) expression.getDrawAlgorithm())
								.copy());
				listElement.setEuclidianVisible(true);
			}
			listElement.update();
			i++;
		}
		result.set(listElement);
	}

	private void updateLocalVar(int index, GeoElement listElement) {
		// set local variable to given value
		if (index == 0) {
			return;
		}
		for (int i = 0; i < varCount - 1; i++) {
			vars[i].set(vars[i + 1]);
		}
		vars[varCount - 1].set(listElement);
		// update var's algorithms until we reach expression
		if (expressionParentAlgo != null) {
			// update all dependent algorithms of the local variable var
			this.setStopUpdateCascade(true);
			for (int i = 0; i < varCount; i++) {
				vars[i].getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			}
			this.setStopUpdateCascade(false);
			expressionParentAlgo.update();
			listElement.set(expression);
		}
	}

}
