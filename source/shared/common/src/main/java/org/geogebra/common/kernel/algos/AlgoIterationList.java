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
import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

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
	private GeoFunctionNVar fNVar;
	private GeoNumberValue startValue;
	private GeoNumberValue n;
	private GeoList startValues;
	private GeoElement startValueGeo;
	private GeoElement nGeo;
	private GeoList list; // output

	private GeoElement expression; // input expression dependent on var
	private GeoElement[] vars; // input: local variable
	private int varCount;
	private GeoList[] over;

	private boolean expIsFunctionOrCurve;
	private boolean isEmpty;
	private AlgoElement expressionParentAlgo;

	enum IterationType {
		/** u(n+1)=f(u(n)) */
		SIMPLE,
		/** u(n+1)=f(u(n),n) */
		DOUBLE,
		/** general type: deeper dependency, arbitrary type */
		DEFAULT
	}

	private IterationType type = IterationType.DEFAULT;

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
		type = IterationType.SIMPLE;

		list = new GeoList(cons);

		setInputOutput();
		compute();
		list.setLabel(label);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param fNVar
	 *            f(n,u(n))
	 * @param startValues
	 *            {n0,u0}
	 * @param n
	 *            number of iterations
	 */
	public AlgoIterationList(Construction cons, String label,
			GeoFunctionNVar fNVar, GeoList startValues, GeoNumberValue n) {
		super(cons);
		this.fNVar = fNVar;
		this.startValueGeo = this.startValues = startValues;
		this.n = n;
		nGeo = n.toGeoElement();
		type = IterationType.DOUBLE;

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
	 * @param expression
	 *            expression first argument of IterationList
	 * @param vars
	 *            variables
	 * @param over
	 *            lists from which the variables should be taken
	 * @param n
	 *            number of iterations
	 */
	public AlgoIterationList(Construction cons, GeoElement expression,
			GeoElement[] vars, GeoList[] over, GeoNumberValue n) {
		super(cons);
		this.expression = expression;
		this.vars = vars;
		this.over = over;
		this.n = n;
		this.nGeo = n.toGeoElement();
		type = IterationType.DEFAULT;

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
		switch (type) {
		case SIMPLE:
			simpleDependency(f);
			break;
		case DOUBLE:
			simpleDependency(fNVar); // done by AlgoElement
			break;
		case DEFAULT:
		default:
			input = new GeoElement[3 + varCount];
			input[0] = expression;
			for (int i = 0; i < varCount; i++) {
				input[i + 1] = vars[i];

			}
			input[1 + varCount] = over[0];
			input[2 + varCount] = nGeo;

			setOnlyOutput(list);

			list.setTypeStringForXML(expression.getXMLtypeString());

			setDependencies(); // done by AlgoElement
			break;
		}

	}

	private void simpleDependency(GeoElement f2) {
		input = new GeoElement[3];
		input[0] = f2;
		input[1] = startValueGeo;
		input[2] = nGeo;

		setOnlyOutput(list);
		setDependencies(); // done by AlgoElement

	}

	/**
	 * Returns contents of input array excluding var (var is not input object,
	 * but must be in input array because of GetCommandDescription method).
	 */
	@Override
	public GeoElementND[] getInputForUpdateSetPropagation() {
		switch (type) {
		case SIMPLE:
		case DOUBLE:
			return super.getInputForUpdateSetPropagation();
		case DEFAULT:
		default:
			GeoElement[] realInput = new GeoElement[3];
			realInput[0] = expression;
			realInput[1] = over[0];
			realInput[2] = nGeo;

			return realInput;
		}
	}

	/**
	 * @return resulting list
	 */
	public GeoList getResult() {
		return list;
	}

	@Override
	public final void compute() {

		switch (type) {
		case SIMPLE:
			computeSimple();
			return;
		case DOUBLE:
			computeDouble();
			return;
		case DEFAULT:
		default:
			// done below
			break;
		}

		if (updateRunning) {
			return;
		}
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
		for (int j = 0; j < over[0].size() && j < iterations + 1; j++) {
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
					Log.debug("AlgoIterationList aborted: free memory reached "
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
			copyDrawAlgo(listElement);
		}

		// set the value of our element
		listElement.update();
		list.add(listElement);
	}

	private void copyDrawAlgo(GeoElement listElement) {
		AlgoElement drawAlgo = expression.getDrawAlgorithm();
		if (listElement instanceof GeoNumeric
				&& drawAlgo instanceof DrawInformationAlgo) {
			DrawInformationAlgo algoCopy = ((DrawInformationAlgo) drawAlgo)
					.copy();
			if (algoCopy instanceof ReplaceChildrenByValues) {
				for (int j = 0; j < varCount; j++) {
					((ReplaceChildrenByValues) algoCopy)
							.replaceChildrenByValues(vars[j]);
				}
			}
			listElement.setDrawAlgorithm(algoCopy);
			listElement.setEuclidianVisible(true);
		}

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
				CasEvaluableFunction fun = (CasEvaluableFunction) listElement;
				for (int i = 0; i < varCount; i++) {
					fun.replaceChildrenByValues(vars[i]);
				}
			}
		}

		return listElement;
	}

	private void updateListItems() {
		if (isEmpty) {
			return;
		}

		// int currentVal = 0;
		int listSize = (int) Math.round(n.getDouble()) + 1;
		int i = over[0].size();
		for (int j = 0; j < over[0].size() && j < listSize; j++) {
			list.get(j).set(over[0].get(j));
			if (j + 1 < vars.length) {
				vars[j + 1].set(over[0].get(j));
			}
		}
		if (over[0].size() >= listSize) {
			return;
		}

		while (i < listSize) {
			GeoElement listElement = list.get(i);

			// check we haven't run out of memory
			if (kernel.getApplication().freeMemoryIsCritical()) {
				long mem = kernel.getApplication().freeMemory();
				list.clearCache();
				kernel.initUndoInfo(); // clear all undo info
				Log.debug("AlgoIterationList aborted: free memory reached "
						+ mem);
				return;
			}

			// set local var value
			// updateLocalVar(currentVal);
			updateLocalVar(i, list.get(i - 1));
			Log.debug(expression + "");
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
			} else {
				listElement.setUndefined();
			}
			copyDrawAlgo(listElement);
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
			for (int i = 0; i < varCount; i++) {
				vars[i].getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			}
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
			val = f.value(val);
			setListElement(i + 1, val);
		}
	}

	private void computeDouble() {
		list.setDefined(true);
		for (int i = 0; i < input.length; i++) {
			if (!input[i].isDefined()) {
				list.setUndefined();
				return;
			}
		}

		// number of iterations
		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0) {
			list.setUndefined();
			return;
		}

		// check if we have 2 start values: integer and double
		if (startValues.size() != 2) {
			list.setUndefined();
			return;
		}
		GeoElement startValue1 = startValues.get(0);
		if (!(startValue1 instanceof GeoNumberValue)) {
			list.setUndefined();
			return;
		}
		double nUdouble = ((GeoNumberValue) startValue1).getDouble();
		int nU = (int) Math.round(nUdouble);
		if (!DoubleUtil.isEqual(nU, nUdouble)) {
			list.setUndefined();
			return;
		}
		GeoElement startValue2 = startValues.get(1);
		if (!(startValue2 instanceof GeoNumberValue)) {
			list.setUndefined();
			return;
		}
		double u = ((GeoNumberValue) startValue2).getDouble();

		// perform iterations u(n+1)=f(n,u(n))
		list.clear();
		setListElement(0, u);
		for (int i = 0; i < iterations; i++) {
			u = fNVar.evaluate(nU, u);
			setListElement(i + 1, u);
			nU++;
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

}
