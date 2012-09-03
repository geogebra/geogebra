/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.DrawInformationAlgo;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

/**
 * Algorithm for the Zip[ expression of var, var, list, var, list, ... ]
 * command.
 * 
 * @author Zbynek Konecny
 */
public class AlgoZip extends AlgoElement {

	private GeoElement expression; // input expression dependent on var
	private GeoElement[] vars; // input: local variable
	private int varCount;
	private GeoList[] over;
	private GeoList list; // output

	private int last_length = 0;
	private boolean expIsFunctionOrCurve, isEmpty;
	private AlgoElement expressionParentAlgo;

	// we need to check that some Object[] reference didn't cause infinite
	// update cycle
	private boolean updateRunning = false;

	/**
	 * Creates a new algorithm to create a sequence of objects that form a list.
	 * @param cons construction
	 * 
	 * @param label label for the list
	 * @param expression expression (first argument of zip
	 * @param vars variables
	 * @param over lists from which the variables should be taken
	 */
	public AlgoZip(Construction cons, String label, GeoElement expression,
			GeoElement[] vars, GeoList[] over) {

		this(cons, expression, vars, over);
		list.setLabel(label);
	}

	/**
	 * Creates a new algorithm to create a sequence of objects that form a list.
	 * @param cons construction
	 * @param expression expression (first argument of zip
	 * @param vars variables
	 * @param over lists from which the variables should be taken
	 */
	AlgoZip(Construction cons, GeoElement expression, GeoElement[] vars,
			GeoList[] over) {
		super(cons);

		this.expression = expression;
		this.vars = vars;
		this.over = over;
		varCount = vars.length;

		expressionParentAlgo = expression.getParentAlgorithm();
		expIsFunctionOrCurve = expression.isGeoFunction()
				|| expression.isGeoCurveCartesian();

		list = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoZip;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1 + 2 * varCount];
		input[0] = expression;
		for (int i = 0; i < varCount; i++) {
			input[2 * i + 1] = vars[i];
			input[2 * i + 2] = over[i];
		}
		setOutputLength(1);
		setOutput(0, list);

		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns contents of input array excluding var (var is not input object,
	 * but must be in input array because of GetCommandDescription method).
	 */
	@Override
	public
	GeoElement[] getInputForUpdateSetPropagation() {
		GeoElement[] realInput = new GeoElement[varCount + 1];
		realInput[0] = expression;
		for (int i = 0; i < varCount; i++) {
			realInput[i + 1] = over[i];
		}
		return realInput;
	}

	/**
	 * Returns list of all contained elements.
	 * 
	 * @return list of elements
	 */
	GeoList getList() {
		return list;
	}

	@Override
	public final void compute() {
		if (updateRunning)
			return;
		updateRunning = true;
		for (int i = 1; i < input.length; i++) {
			if (!input[i].isDefined()) {
				list.setUndefined();
				updateRunning = false;
				return;
			}
		}
		list.setDefined(true);

		// create sequence for expression(var) by changing var according to the
		// given range

		isEmpty = minOverSize() == 0;

		// an update may be necessary because another variable in expression
		// has changed. However, the range (from, to, step) may not have
		// changed:
		// in this case it is much more efficient not to create all objects
		// for the list again, but just to set their new values
		boolean setValuesOnly = (minOverSize() == last_length);

		// setValues does not work for functions
		setValuesOnly = setValuesOnly && !expIsFunctionOrCurve;

		// avoid label creation, might happen e.g. in
		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// update list
		if (setValuesOnly)
			updateListItems();
		else
			createNewList();

		// revert label creation setting
		cons.setSuppressLabelCreation(oldSuppressLabels);
		updateRunning = false;
	}

	private void createNewList() {
		// clear list if defined
		int i = 0;
		int oldListSize = list.size();
		list.clear();
		if (!isEmpty) {
			// needed capacity
			int n = minOverSize();
			list.ensureCapacity(n);

			// create the sequence
			int currentVal = 0;

			while (currentVal < minOverSize()) {
				// check we haven't run out of memory
				if (app.freeMemoryIsCritical()) {
					long mem = app.freeMemory();
					list.clearCache();
					kernel.initUndoInfo(); // clear all undo info
					App
							.debug("AlgoZip aborted: free memory reached "
									+ mem);
					return;
				}

				// set local var value
				updateLocalVar(currentVal);

				addElement(i);

				currentVal += 1;
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

		// remember current values
		last_length = minOverSize();
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

		// copy current expression value to listElement
		if (!expIsFunctionOrCurve) {
			listElement.set(expression);
			AlgoElement drawAlgo = expression.getDrawAlgorithm();
			if (listElement instanceof GeoNumeric
					&& drawAlgo instanceof DrawInformationAlgo) {
				App.debug(expression.getDrawAlgorithm().getClass()
						.getName());
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
			if (listElement.isGeoFunction()) {
				GeoFunction f = (GeoFunction) listElement;
				for (int i = 0; i < varCount; i++)
					f.replaceChildrenByValues(vars[i]);
			}
			// GeoCurve
			else if (listElement.isGeoCurveCartesian()) {
				GeoCurveCartesian curve = (GeoCurveCartesian) listElement;
				for (int i = 0; i < varCount; i++)
					curve.replaceChildrenByValues(vars[i]);
			}
		}

		return listElement;
	}

	private void updateListItems() {
		if (isEmpty)
			return;

		int currentVal = 0;

		while (currentVal < minOverSize()) {
			GeoElement listElement = list.get(currentVal);

			// check we haven't run out of memory
			if (app.freeMemoryIsCritical()) {
				long mem = app.freeMemory();
				list.clearCache();
				kernel.initUndoInfo(); // clear all undo info
				App.debug("AlgoZip aborted: free memory reached "
						+ mem);
				return;
			}

			// set local var value
			updateLocalVar(currentVal);

			// copy expression value to listElement
			// if it's undefined, just copy the undefined property
			if (expression.isDefined())
				listElement.set(expression);
			else
				listElement.setUndefined();
			if (listElement instanceof GeoNumeric
					&& listElement.getDrawAlgorithm() instanceof DrawInformationAlgo) {
				listElement.setDrawAlgorithm(((DrawInformationAlgo) expression
						.getDrawAlgorithm()).copy());
				listElement.setEuclidianVisible(true);
			}
			listElement.update();

			currentVal += 1;
		}
	}

	private int minOverSize() {
		int min = over[0].size();
		for (int i = 1; i < varCount; i++)
			if (over[i].size() < min)
				min = over[i].size();
		return min;
	}

	/**
	 * Sets value of the local loop variable of the sequence and updates all
	 * it's dependencies until we reach the sequence algo.
	 */
	private void updateLocalVar(int index) {
		// set local variable to given value
		for (int i = 0; i < varCount; i++)
			vars[i].set(over[i].get(index));

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

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability
}
