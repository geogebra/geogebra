package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoHyperGeometric;

public class HyperGeometricProbability implements DiscreteDistribution {

	private GeoNumeric k;
	private GeoNumeric k2;
	private DiscreteProbability discreteProbability;
	private final Construction cons;
	private DistributionParameters oldParameters = null;

	/**
	 *
	 * @param cons The construction.
	 */
	public HyperGeometricProbability(Construction cons) {
		this.cons = cons;
	}

	@Override
	public DiscreteProbability create(DistributionParameters parameters) {
		if (parameters.equals(oldParameters)) {
			return discreteProbability;
		}

		GeoNumberValue pGeo = parameters.at(0);
		GeoNumberValue nGeo = parameters.at(1);
		GeoNumberValue sGeo = parameters.at(2);
		oldParameters = parameters;

		double p = pGeo.getDouble(); // population size
		double n = nGeo.getDouble(); // n
		double s = sGeo.getDouble(); // sample size

		// ================================================
		// interval bounds:
		// [ max(0, n + s - p) , min(n, s) ]
		// =================================================

		double lowBound = Math.max(0, n + s - p);
		double highBound = Math.min(n, s);

		GeoNumeric lowGeo = new GeoNumeric(cons, lowBound);
		GeoNumeric highGeo = new GeoNumeric(cons, highBound);

		k = new GeoNumeric(cons);
		k2 = new GeoNumeric(cons);
		AlgoSequence algoSeq = new AlgoSequence(cons, k, k, lowGeo, highGeo, null);
		cons.removeFromAlgorithmList(algoSeq);
		GeoList values = (GeoList) algoSeq.getOutput(0);

		AlgoListElement algo = new AlgoListElement(cons, values, k2);
		cons.removeFromConstructionList(algo);

		AlgoHyperGeometric hyperGeometric = new AlgoHyperGeometric(cons,
				pGeo, nGeo, sGeo, (GeoNumberValue) algo.getOutput(0),
				new GeoBoolean(cons, parameters.isCumulative));
		cons.removeFromConstructionList(hyperGeometric);

		double length = highBound - lowBound + 1;
		GeoNumeric lengthGeo = new GeoNumeric(cons, length);
		AlgoSequence algoSeq2 = new AlgoSequence(cons, hyperGeometric.getOutput(0), k2,
				new GeoNumeric(cons, 1.0), lengthGeo, null);
		cons.removeFromConstructionList(algoSeq2);
		GeoList probs = (GeoList) algoSeq2.getOutput(0);
		this.discreteProbability = new DiscreteProbability(values, probs);
		return discreteProbability;
	}

	public GeoList probs() {
		return discreteProbability.probabilities();
	}

	public GeoList values() {
		return discreteProbability.values();
	}
}
