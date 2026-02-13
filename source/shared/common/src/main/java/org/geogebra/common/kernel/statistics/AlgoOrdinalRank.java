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

	private static Comparator<OrderedPair> comparator;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            data
	 */
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

	/**
	 * @return element ranks
	 */
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
			sortedSet = new TreeSet<>(getComparator());

		} else {
			outputList.setUndefined();
			return;
		}

		// copy inputList into treeset
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				OrderedPair pair = new OrderedPair(geo.evaluateDouble(), i);
				sortedSet.add(pair);
				if (Double.isNaN(geo.evaluateDouble())) {
					outputList.setUndefined();
					return;
				}
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
			outputList.addNumber(list[i], null);
		}

	}

	private static final class OrderedPair {
		private final double x;
		private final int y;

		private OrderedPair(double x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * @return comparator
	 */
	private static Comparator<OrderedPair> getComparator() {
		if (comparator == null) {
			comparator = (a, b) -> {

				double compX = a.x - b.x;
				return compX < 0 ? -1 : +1;
			};

		}

		return comparator;
	}
}
