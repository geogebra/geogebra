package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.nearlyOne;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.algos.AlgoSequenceRange;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoInversePoisson;
import org.geogebra.common.kernel.statistics.AlgoPoisson;
import org.geogebra.common.plugin.Operation;

public class PoissonDistribution extends CachingDiscreteDistribution {

	private final Construction cons;
	private final Kernel kernel;

	/**
	 *
	 * @param cons the construction.
	 */
	public PoissonDistribution(Construction cons) {
		this.cons = cons;
		kernel = cons.getKernel();
	}

	@Override
	protected DiscreteProbability createProbability(DistributionParameters parameters) {
		GeoNumberValue meanGeo = parameters.at(0);

		AlgoInversePoisson maxSequenceValue = new AlgoInversePoisson(cons,
				meanGeo, new GeoNumeric(cons, nearlyOne));
		cons.removeFromConstructionList(maxSequenceValue);
		GeoNumberValue maxDiscreteGeo = maxSequenceValue.getResult();

		AlgoSequenceRange algoSeq = new AlgoSequenceRange(cons, new GeoNumeric(cons, 0.0),
				maxDiscreteGeo, null);
		cons.removeFromAlgorithmList(algoSeq);
		GeoList values = (GeoList) algoSeq.getOutput(0);

		GeoNumeric k = new GeoNumeric(cons);
		AlgoListElement algo = new AlgoListElement(cons, values, k);
		cons.removeFromConstructionList(algo);

		AlgoPoisson poisson = new AlgoPoisson(cons, meanGeo,
				(GeoNumberValue) algo.getOutput(0),
				new GeoBoolean(cons, parameters.isCumulative));
		cons.removeFromConstructionList(poisson);

		ExpressionNode nPlusOne = new ExpressionNode(kernel, maxDiscreteGeo,
				Operation.PLUS, new MyDouble(kernel, 1.0));
		AlgoDependentNumber plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
		cons.removeFromConstructionList(plusOneAlgo);

		AlgoSequence algoSeq2 = new AlgoSequence(cons, poisson.getOutput(0), k,
				new GeoNumeric(cons, 1.0),
				(GeoNumberValue) plusOneAlgo.getOutput(0), null);
		cons.removeFromConstructionList(algoSeq2);

		GeoList probs = (GeoList) algoSeq2.getOutput(0);
		return new DiscreteProbability(values, probs);
	}
}
