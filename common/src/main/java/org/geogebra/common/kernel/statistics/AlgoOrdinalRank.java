/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Ranks of a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 2010-05-27
 */

public class AlgoOrdinalRank extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	public AlgoOrdinalRank(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrdinalRank;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}

		GeoElement geo0 = inputList.get(0);

		TreeSet<OrderedPair> sortedSet;

		if (geo0 instanceof NumberValue) {
			sortedSet = new TreeSet<OrderedPair>(getComparator());

		} else {
			outputList.setUndefined();
			return;
		}

		// copy inputList into treeset
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				NumberValue num = (NumberValue) geo;
				OrderedPair pair = new OrderedPair(num.getDouble(), i);
				sortedSet.add(pair);
			} else {
				outputList.setUndefined();
				return;
			}
		}

		// assemble the ranks in an array
		Iterator<OrderedPair> iterator = sortedSet.iterator();
		double[] list = new double[size];
		int i = 1;
		while (iterator.hasNext()) {
			OrderedPair pair = iterator.next();
			list[pair.y] = i++;

		}

		// copy the ranks back into a list
		outputList.setDefined(true);
		outputList.clear();
		for (i = 0; i < size; i++) {
			outputList.add(new GeoNumeric(cons, list[i]));
		}

	}

	private class OrderedPair {
		public double x;
		public int y;

		public OrderedPair(double x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static Comparator<OrderedPair> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<OrderedPair>() {
				public int compare(OrderedPair a, OrderedPair b) {
					OrderedPair itemA = (OrderedPair) a;
					OrderedPair itemB = (OrderedPair) b;

					double compX = itemA.x - itemB.x;
					return compX < 0 ? -1 : +1;
				}
			};

		}

		return comparator;
	}

	private static Comparator<OrderedPair> comparator;

	// TODO Consider locusequability

}
