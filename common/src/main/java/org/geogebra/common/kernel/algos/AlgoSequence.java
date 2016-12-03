/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Algorithm for the Sequence[ expression of var, var, from-value, to-value,
 * step ] command.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoSequence extends AlgoElement {

	private enum SequenceType {
		SIMPLE, RANGE, FULL
	}

	private GeoElementND expression; // input expression dependent on var
	private GeoNumeric var; // input: local variable
	private GeoNumberValue var_from, var_to, var_step;
	private GeoElement var_from_geo, var_to_geo, var_step_geo;
	private GeoList list; // output

	private SequenceType type;

	private double last_from = Double.MIN_VALUE, last_to = Double.MIN_VALUE,
			last_step = Double.MIN_VALUE;
	private boolean expIsFunctionOrCurve, isEmpty;
	private AlgoElement expressionParentAlgo;

	// we need to check that some Object[] reference didn't cause infinite
	// update cycle
	private boolean updateRunning = false;

	/**
	 * Creates a new algorithm to create a sequence of objects that form a list.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the list
	 * @param expression
	 *            expression
	 * @param var
	 *            variable
	 * @param var_from
	 *            lower bound
	 * @param var_to
	 *            upper bound
	 * @param var_step
	 *            step
	 */
	public AlgoSequence(Construction cons, String label,
			GeoElementND expression,
			GeoNumeric var, GeoNumberValue var_from, GeoNumberValue var_to,
			GeoNumberValue var_step) {

		this(cons, expression, var, var_from, var_to, var_step);
		list.setLabel(label);
	}

	/**
	 * Creates a new algorithm to create a sequence of objects that form a list.
	 * 
	 * @param cons
	 *            construction
	 * @param expression
	 *            expression
	 * @param var
	 *            variable
	 * @param var_from
	 *            lower bound
	 * @param var_to
	 *            upper bound
	 * @param var_step
	 *            step
	 */
	public AlgoSequence(Construction cons, GeoElementND expression,
			GeoNumeric var, GeoNumberValue var_from, GeoNumberValue var_to,
			GeoNumberValue var_step) {
		super(cons);

		this.expression = expression;
		this.var = var;
		this.var_from = var_from;
		var_from_geo = var_from.toGeoElement();
		this.var_to = var_to;
		var_to_geo = var_to.toGeoElement();
		this.var_step = var_step;
		if (var_step != null)
			var_step_geo = var_step.toGeoElement();

		expressionParentAlgo = expression.getParentAlgorithm();
		expIsFunctionOrCurve = expression instanceof CasEvaluableFunction;
		type = SequenceType.FULL;
		// Application.debug("expression: " + expression);
		// Application.debug("  parent algo: " +
		// expression.getParentAlgorithm());
		// // Application.debug("  parent algo input is var?: " +
		// (expression.getParentAlgorithm().getInput()[0] == var));
		// Application.debug("  variable: " + var);
		// Application.debug("  expIsGeoFunction: " + expIsGeoFunction);

		list = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * Creates simple sequence start..upTo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param from
	 *            lower bound
	 * @param upTo
	 *            upper bound
	 */
	public AlgoSequence(Construction cons, String label, GeoNumberValue from,
			GeoNumberValue upTo) {
		super(cons);
		type = SequenceType.RANGE;
		var_from = from;
		var_from_geo = var_from.toGeoElement();
		var_to = upTo;
		var_to_geo = var_to.toGeoElement();
		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	/**
	 * Creates simple sequence 1..upTo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param upTo
	 *            upper bound
	 */
	public AlgoSequence(Construction cons, String label, GeoNumberValue upTo) {
		super(cons);
		type = SequenceType.SIMPLE;
		var_from = new GeoNumeric(cons, 1);
		var_from_geo = (GeoElement) var_from;

		var_to = upTo;
		var_to_geo = var_to.toGeoElement();
		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Sequence;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		switch (type) {
		case SIMPLE:
			input = new GeoElement[1];
			input[0] = var_to_geo;
			list.setTypeStringForXML(StringUtil
					.toLowerCase(var_to_geo.getGeoClassType().xmlName));
			break;
		case RANGE:
			input = new GeoElement[2];
			input[0] = var_from_geo;
			input[1] = var_to_geo;
			list.setTypeStringForXML(StringUtil.toLowerCase(var_to_geo
					.getGeoClassType().xmlName));
			break;
		default:
			// make sure that x(Element[list,1]) will work even if the output
			// list's length is zero
			list.setTypeStringForXML(expression.getXMLtypeString());
			int len = var_step == null ? 4 : 5;
			input = new GeoElement[len];
			input[0] = expression.toGeoElement();
			input[1] = var;
			input[2] = var_from_geo;
			input[3] = var_to_geo;
			if (len == 5) {
				input[4] = var_step_geo;
			}
			break;

		}

		setOutputLength(1);
		setOutput(0, list);

		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns contents of input array excluding var (var is not input object,
	 * but must be in input array because of GetCommandDescription method). see
	 * ticket #72 2010-05-13 null pointer error fixed
	 * 
	 * @author Zbynek Konecny
	 * @version 2010-05-13
	 */
	@Override
	public GeoElement[] getInputForUpdateSetPropagation() {
		if (!type.equals(SequenceType.FULL)) {
			return input;
		}
		// if expression and var are the same, skip both
		int skip = expression == var ? 2 : 1;
		GeoElement[] realInput = new GeoElement[input.length - skip];
		if (skip == 1) {
			realInput[0] = expression.toGeoElement();
		}
		realInput[2 - skip] = var_from_geo;
		realInput[3 - skip] = var_to_geo;
		if (input.length == 5) {
			realInput[4 - skip] = var_step_geo;
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

		switch (type) {
		case SIMPLE:
			computeSimple();
			return;
		case RANGE:
			computeRange();
			return;

		}

		if (updateRunning) {
			return;
		}

		updateRunning = true;
		for (int i = 1; i < input.length; i++) {
			if (input[i] != var && !input[i].isDefined()) { // don't check the
															// var itself (maybe
															// undefined at last
															// loop)
				list.setUndefined();
				updateRunning = false;
				return;
			}
		}
		list.setDefined(true);

		// create sequence for expression(var) by changing var according to the
		// given range
		double from = var_from.getDouble();
		double to = var_to.getDouble();
		double step = var_step == null ? 1 : var_step.getDouble();

		isEmpty = !((to - from) * step > -Kernel.MIN_PRECISION);

		// an update may be necessary because another variable in expression
		// has changed. However, the range (from, to, step) may not have
		// changed:
		// in this case it is much more efficient not to create all objects
		// for the list again, but just to set their new values
		boolean setValuesOnly = (from == last_from && to == last_to && step == last_step);

		// setValues does not work for functions
		setValuesOnly = setValuesOnly && !expIsFunctionOrCurve;

		// avoid label creation, might happen e.g. in
		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// update list
		if (setValuesOnly)
			updateListItems(from, to, step);
		else
			createNewList(from, to, step);

		// revert label creation setting
		cons.setSuppressLabelCreation(oldSuppressLabels);
		updateRunning = false;
	}

	// use doubles
	// ef Sequence[9007199254000027, 9007199254000187]
	private void computeRange() {
		double from = Math.round(var_from.getDouble());
		double to = Math.round(var_to.getDouble());

		if (from > MyMath.LARGEST_INTEGER || from < -MyMath.LARGEST_INTEGER
				|| to > MyMath.LARGEST_INTEGER
				|| to < -MyMath.LARGEST_INTEGER) {
			list.setUndefined();
			return;
		}

		list.clear();

		// also see Operation.java case Sequence:
		if (from < to) {

			// increasing list
			for (double k = from; k <= to; k++) {
				list.addNumber(k, null);
			}

		} else {

			// decreasing list
			for (double k = from; k >= to; k--) {
				list.addNumber(k, null);
			}

		}
	}

	private void computeSimple() {
		int to = (int) Math.round(var_to.getDouble());

		if (last_to < to) {
			for (int k = (int) last_to; k < to; k++) {
				if (k >= 0) {
					list.addNumber(k + 1, null);
				}
			}
		}
		if (last_to > to) {
			for (int k = (int) last_to; k > to; k--) {
				if (k >= 1) {
					GeoElement ge = list.get(k - 1);
					ge.remove();
					list.remove(k - 1);
				}
			}
		}
		last_to = to;

	}

	private void createNewList(double from, double to, double step) {
		// clear list if defined
		int i = 0;
		int oldListSize = list.size();
		list.clear();

		if (!isEmpty) {
			// needed capacity
			if (Double.isInfinite((to - from) / step)) {
				list.setUndefined();
				return;
			}
			int n = (int) Math.ceil((to - from) / step) + 1;
			list.ensureCapacity(n);

			// create the sequence
			double currentVal = from;
			while ((step > 0 && currentVal <= to + Kernel.MIN_PRECISION)
					|| (step < 0 && currentVal >= to - Kernel.MIN_PRECISION)) {

				// check we haven't run out of memory
				if (kernel.getApplication().freeMemoryIsCritical()) {
					long mem = kernel.getApplication().freeMemory();
					list.clearCache();
					kernel.initUndoInfo(); // clear all undo info
					Log.debug("AlgoSequence aborted: free memory reached "
							+ mem);
					return;
				}

				// set local var value
				updateLocalVar(currentVal);
				addElement(i);
				currentVal += step;
				if (Kernel.isInteger(currentVal)) {
					currentVal = Math.round(currentVal);
				}
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
		last_from = from;
		last_to = to;
		last_step = step;
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
			if (listElement.isGeoList()) {
				((GeoList) listElement).replaceChildrenByValues(var);
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
				((ReplaceChildrenByValues) algoCopy)
						.replaceChildrenByValues(var);
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
				CasEvaluableFunction f = (CasEvaluableFunction) listElement;
				f.replaceChildrenByValues(var);
			}
		}

		return listElement;
	}

	private void updateListItems(double from, double to, double step) {
		if (isEmpty || list.size() == 0) {
			return;
		}

		double currentVal = from;
		int i = 0;

		while ((step > 0 && currentVal <= to + Kernel.MIN_PRECISION)
				|| (step < 0 && currentVal >= to - Kernel.MIN_PRECISION)) {
			GeoElement listElement = list.get(i);

			// check we haven't run out of memory
			if (kernel.getApplication().freeMemoryIsCritical()) {
				long mem = kernel.getApplication().freeMemory();
				list.clearCache();
				kernel.initUndoInfo(); // clear all undo info
				Log.debug("AlgoSequence aborted: free memory reached " + mem);
				return;
			}

			// set local var value
			updateLocalVar(currentVal);

			// copy expression value to listElement
			// if it's undefined, just copy the undefined property
			if (expression.isDefined()) {
				listElement.set(expression);
				if (listElement.isGeoList()) {
					((GeoList) listElement).replaceChildrenByValues(var);
				}
			} else {
				listElement.setUndefined();
			}
			copyDrawAlgo(listElement);
			listElement.update();

			currentVal += step;
			if (Kernel.isInteger(currentVal)) {
				currentVal = Math.round(currentVal);
			}
			i++;
		}
	}

	/**
	 * Sets value of the local loop variable of the sequence and updates all
	 * it's dependencies until we reach the sequence algo.
	 */
	private void updateLocalVar(double varVal) {
		// set local variable to given value
		var.setValue(varVal);

		// update var's algorithms until we reach expression
		if (expressionParentAlgo != null) {
			// update all dependent algorithms of the local variable var
			this.setStopUpdateCascade(true);

			// needed for eg Sequence[If[liste1(i) < a
			boolean oldLabelStatus = cons.isSuppressLabelsActive();
			kernel.getConstruction().setSuppressLabelCreation(true);

			var.getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			kernel.getConstruction().setSuppressLabelCreation(oldLabelStatus);

			this.setStopUpdateCascade(false);
			expressionParentAlgo.update();
		}
	}

	
}
