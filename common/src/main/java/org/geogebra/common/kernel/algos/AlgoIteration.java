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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

/**
 * Iteration[ f(x), x0, n ]
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIteration extends AlgoElement {

	private GeoFunction f; // input
	private GeoNumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
	private GeoElement result; // output

	public AlgoIteration(Construction cons, String label, GeoFunction f,
			GeoNumberValue startValue, GeoNumberValue n) {
		super(cons);
		this.f = f;
		this.startValue = startValue;
		startValueGeo = startValue.toGeoElement();
		this.n = n;
		nGeo = n.toGeoElement();

		result = new GeoNumeric(cons);
		isSimple = true;
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	private GeoElement expression; // input expression dependent on var
	private GeoElement[] vars; // input: local variable
	private int varCount;
	private GeoList[] over;
	private boolean expIsFunctionOrCurve, isSimple, isEmpty;
	private AlgoElement expressionParentAlgo;

	public AlgoIteration(Construction cons, String label,
			GeoElement expression, GeoElement[] vars, GeoList[] over,
			GeoNumberValue n) {
		super(cons);
		this.expression = expression;
		this.vars = vars;
		this.over = over;
		this.n = n;
		this.nGeo = n.toGeoElement();
		isSimple = false;

		varCount = vars.length;

		expressionParentAlgo = expression.getParentAlgorithm();
		expIsFunctionOrCurve = expression instanceof CasEvaluableFunction;

		result = expression.copy();
		setInputOutput(); // for AlgoElement

		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.Iteration;
	}

	@Override
	protected void setInputOutput() {
		if (isSimple) {
			input = new GeoElement[3];
			input[0] = f;
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

		}// done by AlgoElement
		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies();
	}

	public GeoElement getResult() {
		return result;
	}

	public final void computeSimple() {
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
			val = f.evaluate(val);
		}
		((GeoNumeric) result).setValue(val);
	}

	boolean updateRunning = false;

	@Override
	public final void compute() {
		if (isSimple) {
			computeSimple();
			return;
		}
		App.debug(nGeo.isLabelSet() + "label");
		if (updateRunning)
			return;
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
	public GeoElement[] getInputForUpdateSetPropagation() {
		if (isSimple) {
			return super.getInputForUpdateSetPropagation();
		}
		GeoElement[] realInput = new GeoElement[3];
		realInput[0] = expression;
		realInput[1] = over[0];
		realInput[2] = nGeo;

		return realInput;
	}

	// TODO Consider locusequability

	private void updateListItems() {
		if (isEmpty)
			return;

		int listSize = (int) Math.round(n.getDouble());


		if (listSize < over[0].size()) {
			result.set(over[0].get(listSize));
			return;
		}
		for (int j = 1; j < varCount; j++) {
			vars[j].set(over[0].get(over[0].size() - varCount + j - 1));
		}

		GeoElement listElement = over[0].get(over[0].size() - 1).copyInternal(
				cons);
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
			if (listElement instanceof GeoNumeric
					&& listElement.getDrawAlgorithm() instanceof DrawInformationAlgo) {
				listElement.setDrawAlgorithm(((DrawInformationAlgo) expression
						.getDrawAlgorithm()).copy());
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
