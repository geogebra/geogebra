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

package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;

/**
 * Simple stack class for already evaluated values of the curve.
 *
 * @author Laszlo
 */
public class CurvePlotterStack {

	private final CurvePlotterStackItem[] items;
	private int top;

	/**
	 * Constructor
	 *
	 * @param length of the stack
	 * @param onScreen if first item on screen
	 * @param eval first evaluation
	 */
	public CurvePlotterStack(int length, boolean onScreen,
			double[] eval) {
		items = new CurvePlotterStackItem[length];
		for (int i = 0; i < length; i++) {
			items[i] = new CurvePlotterStackItem();
		}
		items[0].set(1, 0, onScreen, Cloner.clone(eval));
		top = 1;
	}

	/**
	 * Push an element to stack, consisting the following info:
	 *
	 * @param dyadic t of f(t)
	 * @param depth of the bisection.
	 * @param onScreen if the evaluated value on screen
	 * @param eval f(t)
	 */
	public void push(int dyadic, int depth, boolean onScreen, double[] eval) {
		items[top].set(dyadic, depth, onScreen, eval);
		top++;
	}

	/**
	 * Pops item from the top.
	 *
	 * @return top of the item.
	 */
	public CurvePlotterStackItem pop() {
		top--;
		return top >= 0 ? items[top] : null;
	}

	/**
	 *
	 * @return if the stack have items.
	 */
	public boolean hasItems() {
		return top != 0;
	}
}
