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
 * IterationList[ f(A), A, {A_lis_val}, n ]
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIterationList extends AlgoElement {

	private GeoFunction f; // input
	private GeoNumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
	private GeoList list; // output

	private GeoElement expression; // input expression dependent on var
	private GeoElement[] vars; // input: local variable
	private int varCount;
	private GeoList[] over;

	private boolean expIsFunctionOrCurve, isSimple, isEmpty;
	private AlgoElement expressionParentAlgo;

	// we need to check that some Object[] reference didn't cause infinite
	// update cycle
	private boolean updateRunning = false;
	private int iterationsOld = -1;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            list label
	 * @param f
	 *            function - first argument of IterationList
	 * @param startValue
	 *            start value for the function
	 * @param n
	 *            number of iterations
	 */
	public AlgoIterationList(Construction cons, String label, GeoFunction f,
			GeoNumberValue startValue, GeoNumberValue n) {
		super(cons);
		this.f = f;
		this.startValue = startValue;
		startValueGeo = startValue.toGeoElement();
		this.n = n;
		nGeo = n.toGeoElement();
		isSimple = true;

		list = new GeoList(cons);

		setInputOutput();
		compute();
		list.setLabel(label);
	}

	/**
	 * Creates a new algorithm to create a sequence of objects that form a list.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the list
	 * @param expression
	 *            expression first argument of IterationList
	 * @param vars
	 *            variables
	 * @param over
	 *            lists from which the variables should be taken
	 * @param n
	 *            number of iterations
	 */
	public AlgoIterationList(Construction cons, String label,
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

		list = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.IterationList;
	}

	@Override
	protected void setInputOutput() {
		if (isSimple) {
			input = new GeoElement[3];
			input[0] = f;
			input[1] = startValueGeo;
			input[2] = nGeo;

			super.setOutputLength(1);
			super.setOutput(0, list);
			setDependencies(); // done by AlgoElement
		} else {
			input = new GeoElement[3 + varCount];
			input[0] = expression;
			for (int i = 0; i < varCount; i++) {
				input[i + 1] = vars[i];

			}
			input[1 + varCount] = over[0];
			input[2 + varCount] = nGeo;

			setOutputLength(1);
			setOutput(0, list);

			list.setTypeStringForXML(expression.getXMLtypeString());

			setDependencies(); // done by AlgoElement
		}
	}

	/**
	 * Returns contents of input array excluding var (var is not input object,
	 * but must be in input array because of GetCommandDescription method).
	 */
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

	public GeoList getResult() {
		return list;
	}

	@Override
	public final void compute() {
		if (isSimple) {
			computeSimple();
			return;
		}

		if (updateRunning)
			return;
		updateRunning = true;

		for (int i = 2; i < input.length - 1; i += 2) {
			if (!input[i].isDefined()) {
				list.setUndefined();
				updateRunning = false;
				iterationsOld = -1;
				return;
			}
		}
		list.setDefined(true);

		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0 || varCount > over[0].size()) {
			list.setUndefined();
			updateRunning = false;
			iterationsOld = -1;
			return;
		}

		isEmpty = over[0].size() == 0;

		boolean setValuesOnly = iterations == iterationsOld;
		setValuesOnly = setValuesOnly && !expIsFunctionOrCurve;

		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// update list
		if (setValuesOnly) {
			updateListItems();
		} else {
			createNewList();
		}
		this.iterationsOld = iterations;

		// revert label creation setting
		cons.setSuppressLabelCreation(oldSuppressLabels);
		updateRunning = false;
	}

	private void createNewList() {
		int iterations = (int) Math.round(n.getDouble());
		int i = Math.min(over[0].size(), iterations);
		int oldListSize = list.size();
		list.clear();
		for (int j = 0; j < over[0].size()
 && j < iterations + 1; j++) {
			list.add(over[0].get(j).copyInternal(cons));
			if (j + 1 < varCount) {
				vars[j + 1].set(over[0].get(j));
			}
		}
		if (iterations + 1 <= over[0].size()) {
			return;
		}
		if (!isEmpty) {
			// needed capacity
			list.ensureCapacity(((int) Math.round(n.getDouble())) + 1);

			// create the sequence
			int listSize = ((int) Math.round(n.getDouble())) + 1;

			while (i < listSize) {
				// check we haven't run out of memory
				if (kernel.getApplication().freeMemoryIsCritical()) {
					long mem = kernel.getApplication().freeMemory();
					list.clearCache();
					kernel.initUndoInfo(); // clear all undo info
					App.debug("AlgoIterationList aborted: free memory reached "
							+ mem);
					return;
				}

				// set local var value
				updateLocalVar(i, list.get(i - 1));
				addElement(i);
				i++;
			}
		}

		// if the old list was longer than the new one
		// we need to set some cached elements to undefined
		for (int k = oldListSize - 1; k >= i; k--) {
			GeoElement oldElement = list.getCached(k);
			oldElement.setUndefined();
			oldElement.update();
		}
	}

	private void addElement(int i) {
		// only add new objects
		GeoElement listElement = null;
		int cacheListSize = list.getCacheSize();

		if (i < cacheListSize) {
			// we reuse existing list element from cache
			listElement = list.getCached(i);

			if (expIsFunctionOrCurve) {
				// for functions we always need a new element
				listElement.setParentAlgorithm(null);
				listElement.doRemove();

				// replace old list element by a new one
				listElement = createNewListElement();
			}
		} else {
			// create new list element
			listElement = createNewListElement();
		}

		// return early if it's the first element - we will add the start
		// position to this
		if (i == 0) {
			listElement.set(over[0].get(0));
			listElement.update();
			list.add(listElement);
			return;
		}

		// copy current expression value to listElement
		if (!expIsFunctionOrCurve) {
			listElement.set(expression);
			if (listElement.isGeoList()) {
				for (int j = 0; j < varCount; j++) {
					((GeoList) listElement).replaceChildrenByValues(vars[j]);
				}
			}
			AlgoElement drawAlgo = expression.getDrawAlgorithm();
			if (listElement instanceof GeoNumeric
					&& drawAlgo instanceof DrawInformationAlgo) {
				listElement.setDrawAlgorithm(((DrawInformationAlgo) drawAlgo)
						.copy());
				listElement.setEuclidianVisible(true);
			}
		}

		// set the value of our element
		listElement.update();
		list.add(listElement);
	}

	private GeoElement createNewListElement() {
		GeoElement listElement = expression.copyInternal(cons);
		listElement.setParentAlgorithm(this);
		listElement.setConstructionDefaults();
		listElement.setUseVisualDefaults(false);

		// functions and curves use the local variable var
		// so we have to replace var and all dependent objects of var
		// by their current values
		if (expIsFunctionOrCurve) {
			// GeoFunction
			if (listElement instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) listElement;
				for (int i = 0; i < varCount; i++)
					f.replaceChildrenByValues(vars[i]);
			}
		}

		return listElement;
	}

	private void updateListItems() {
		if (isEmpty)
			return;

		// int currentVal = 0;
		int listSize = (int) Math.round(n.getDouble());
		int i = over[0].size();
		for (int j = 0; j < over[0].size()
				&& j < (int) Math.round(n.getDouble()); j++) {
			list.get(j).set(over[0].get(j));
			if (j + 1 < vars.length) {
				vars[j + 1].set(over[0].get(j));
			}
		}
		if (over[0].size() >= (int) Math.round(n.getDouble())) {
			return;
		}

		while (i < listSize) {
			GeoElement listElement = list.get(i);

			// check we haven't run out of memory
			if (kernel.getApplication().freeMemoryIsCritical()) {
				long mem = kernel.getApplication().freeMemory();
				list.clearCache();
				kernel.initUndoInfo(); // clear all undo info
				App.debug("AlgoIterationList aborted: free memory reached "
						+ mem);
				return;
			}

			// set local var value
			// updateLocalVar(currentVal);
			updateLocalVar(i, listElement);

			// copy expression value to listElement
			// if it's undefined, just copy the undefined property
			if (expression.isDefined()) {
				listElement.set(expression);
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
	}

	/**
	 * Sets value of the local loop variable of the sequence and updates all
	 * it's dependencies until we reach the main algo.
	 */
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
			for (int i = 0; i < varCount; i++)
				vars[i].getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			this.setStopUpdateCascade(false);
			expressionParentAlgo.update();
		}

	}


	private void computeSimple() {
		list.setDefined(true);
		for (int i = 0; i < input.length; i++) {
			if (!input[i].isDefined()) {
				list.setUndefined();
				return;
			}
		}

		// number of iterations
		list.clear();
		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0) {
			list.setUndefined();
			return;
		}

		// perform iteration f(f(f(...(startValue))))
		// and fill list with all intermediate results
		double val = startValue.getDouble();
		setListElement(0, val);
		for (int i = 0; i < iterations; i++) {
			val = f.evaluate(val);
			setListElement(i + 1, val);
		}
	}

	private void setListElement(int index, double value) {
		GeoNumeric listElement;
		if (index < list.getCacheSize()) {
			// use existing list element
			listElement = (GeoNumeric) list.getCached(index);
		} else {
			// create a new list element
			listElement = new GeoNumeric(cons);
			listElement.setParentAlgorithm(this);
			listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);
		}

		list.add(listElement);
		listElement.setValue(value);
	}

	// TODO Consider locusequability

}
