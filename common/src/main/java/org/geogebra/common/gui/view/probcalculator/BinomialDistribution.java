package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoBinomialDist;

public class BinomialDistribution implements DiscreteDistribution {

	private final GeoNumeric k;
	private DiscreteProbability discreteProbability;
	private final Construction cons;
	private DistributionParameters oldParameters = null;
	private final GeoNumeric k2;

	/**
	 *
	 * @param cons The construction.
	 */
	public BinomialDistribution(Construction cons) {
		this.cons = cons;
		k = new GeoNumeric(cons);
		k2 = new GeoNumeric(cons);
	}

	@Override
	public DiscreteProbability create(DistributionParameters parameters) {
		if (parameters.equals(oldParameters)) {
			return discreteProbability;
		}

		GeoNumberValue nGeo = parameters.at(0);
		GeoNumberValue pGeo = parameters.at(1);
		oldParameters = parameters;

		GeoNumeric nPlusOneGeo = new GeoNumeric(cons, nGeo.getDouble() + 1);

		AlgoSequence algoSeq = new AlgoSequence(cons, k2, k2,
				new GeoNumeric(cons, 0.0), nGeo, null);
		GeoList values = (GeoList) algoSeq.getOutput(0);

		AlgoListElement algo = new AlgoListElement(cons, values,
				k);
		cons.removeFromConstructionList(algo);

		AlgoBinomialDist algo2 = new AlgoBinomialDist(cons, nGeo, pGeo,
				(GeoNumberValue) algo.getOutput(0),
				new GeoBoolean(cons, parameters.isCumulative));
		cons.removeFromConstructionList(algo2);

		AlgoSequence algoSeq2 = new AlgoSequence(cons, algo2.getOutput(0),
				k, new GeoNumeric(cons, 1.0), nPlusOneGeo, null);
		cons.removeFromConstructionList(algoSeq2);

		GeoList probs = (GeoList) algoSeq2.getOutput(0);
		this.discreteProbability = new DiscreteProbability(values, probs);
		return discreteProbability;
	}
}