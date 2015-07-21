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
	private int listCount;
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

		listCount = over.length;
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
			input = new GeoElement[2 + listCount + varCount];
			input[0] = expression;
			for (int i = 0; i < listCount; i++) {
				input[2 * i + 1] = vars[i];
				input[2 * i + 2] = over[i];
			}
			input[1 + listCount + varCount] = nGeo;

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
		if (iterations < 0) {
			updateRunning = false;
			result.setUndefined();
			return;
		}

		isEmpty = over[0].size() == 0;

		boolean setValuesOnly = false;
		setValuesOnly = setValuesOnly && !expIsFunctionOrCurve;

		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// update list
		updateListItems();

		// revert label creation setting
		cons.setSuppressLabelCreation(oldSuppressLabels);
		updateRunning = false;
	}
	// TODO Consider locusequability

	private void updateListItems() {
		if (isEmpty)
			return;

		int currentVal = 0;
		int listSize = (int) Math.round(n.getDouble());
		int i = 0;
		GeoElement listElement = over[0].get(0).copyInternal(cons);

		while (i < listSize) {
			App.debug(i + "level");


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
			} else
				listElement.setUndefined();
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
		for (int i = 0; i < listCount; i++) {
			vars[i].set(listElement);
		}
		if (varCount > listCount) {
			((GeoNumeric) vars[varCount - 1]).setValue(index + 1);
		}

		// update var's algorithms until we reach expression
		if (expressionParentAlgo != null) {
			// update all dependent algorithms of the local variable var
			this.setStopUpdateCascade(true);
			// for (int i = 0; i < listCount; i++)
			App.debug("TODO" + vars[0].getAlgoUpdateSet().getSize());//
			vars[0].getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			this.setStopUpdateCascade(false);
			expressionParentAlgo.update();
			listElement.set(expression);
		}

	}

}
