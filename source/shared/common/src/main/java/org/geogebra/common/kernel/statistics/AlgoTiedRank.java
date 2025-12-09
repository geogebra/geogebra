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

import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * List ranking using tie strategy.
 */

public class AlgoTiedRank extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;
	private NaturalRanking rankingAlgorithm;
	private double[] inputArray;
	private double[] outputArray;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            input data
	 */
	public AlgoTiedRank(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TiedRank;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list of ranks
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

		inputArray = new double[size];

		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				inputArray[i] = geo.evaluateDouble();
				if (Double.isNaN(inputArray[i])) {
					outputList.setUndefined();
					return;
				}
			} else {
				outputList.setUndefined();
				return;
			}
		}

		if (rankingAlgorithm == null) {
			rankingAlgorithm = new NaturalRanking(TiesStrategy.AVERAGE);
		}

		outputArray = rankingAlgorithm.rank(inputArray);

		// copy the ranks back into a list
		outputList.setDefined(true);
		outputList.clear();
		for (int i = 0; i < size; i++) {
			outputList.addNumber(outputArray[i], this);
		}
	}

}
