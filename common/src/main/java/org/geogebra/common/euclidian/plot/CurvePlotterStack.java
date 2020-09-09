package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;

public class CurvePlotterStack {

	private final CurvePlotterStackItem[] items;
	private int top;

	public CurvePlotterStack(int length, boolean onScreen,
			double[] eval) {
		items = new CurvePlotterStackItem[length];
		for (int i = 0; i < length; i++) {
			items[i] = new CurvePlotterStackItem();
		}
		items[0].set(1, 0, onScreen, Cloner.clone(eval));
		top = 1;
	}

	public void push(int dyadic, int depth, boolean onScreen, double[] eval) {
		items[top].set(dyadic, depth, onScreen, eval);
		top++;
	}

	public CurvePlotterStackItem pop() {
		top--;
		return items[top];
	}

	public boolean hasItems() {
		return top != 0;
	}
}
