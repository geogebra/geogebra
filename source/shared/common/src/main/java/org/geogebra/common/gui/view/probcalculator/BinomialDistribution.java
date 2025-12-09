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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.algos.AlgoSequenceRange;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoBinomialDist;

public class BinomialDistribution extends CachingDiscreteDistribution {

	private final Construction cons;

	/**
	 *
	 * @param cons The construction.
	 */
	public BinomialDistribution(Construction cons) {
		this.cons = cons;
	}

	@Override
	protected DiscreteProbability createProbability(DistributionParameters parameters) {
		GeoNumberValue nGeo = parameters.at(0);
		GeoNumberValue pGeo = parameters.at(1);

		GeoNumeric nPlusOneGeo = new GeoNumeric(cons, nGeo.getDouble() + 1);

		AlgoSequenceRange algoSeq = new AlgoSequenceRange(cons,
				new GeoNumeric(cons, 0.0), nGeo, null);
		GeoList values = (GeoList) algoSeq.getOutput(0);

		GeoNumeric k = new GeoNumeric(cons);
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
		return new DiscreteProbability(values, probs);
	}
}