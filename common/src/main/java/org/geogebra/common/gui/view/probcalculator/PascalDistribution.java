package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.nearlyOne;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoInversePascal;
import org.geogebra.common.kernel.statistics.AlgoPascal;
import org.geogebra.common.plugin.Operation;

public class PascalDistribution implements DiscreteDistribution {

	private final Kernel kernel;
	private final GeoNumeric k;
	private final GeoNumeric k2;
	private DiscreteProbability discreteProbability;
	private final Construction cons;
	private DistributionParameters oldParameters = null;

	/**
	 *
	 * @param cons The construction.
	 */
	public PascalDistribution(Construction cons) {
		k = new GeoNumeric(cons);
		k2 = new GeoNumeric(cons);
		this.cons = cons;
		kernel = cons.getKernel();
	}

	@Override
	public DiscreteProbability create(DistributionParameters parameters) {
		if (parameters.equals(oldParameters)) {
			return discreteProbability;
		}

		GeoNumberValue nGeo = parameters.at(0);
		GeoNumberValue pGeo = parameters.at(1);
		oldParameters = parameters;

		AlgoInversePascal n2 = new AlgoInversePascal(cons, nGeo, pGeo,
				new GeoNumeric(cons, nearlyOne));
		cons.removeFromConstructionList(n2);
		GeoElementND n2Geo = n2.getOutput(0);

		AlgoSequence algoSeq = new AlgoSequence(cons, k, k, new GeoNumeric(cons, 0.0),
				(GeoNumberValue) n2Geo, null);
		cons.removeFromAlgorithmList(algoSeq);
		GeoList values = (GeoList) algoSeq.getOutput(0);

		AlgoListElement algo = new AlgoListElement(cons, values, k2);
		cons.removeFromConstructionList(algo);

		AlgoPascal algoPascal = new AlgoPascal(cons, nGeo, pGeo,
				(GeoNumberValue) algo.getOutput(0),
				new GeoBoolean(cons, parameters.isCumulative));
		cons.removeFromConstructionList(algoPascal);

		ExpressionNode nPlusOne = new ExpressionNode(kernel, n2Geo, Operation.PLUS,
				new MyDouble(kernel, 1.0));
		AlgoDependentNumber plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
		cons.removeFromConstructionList(plusOneAlgo);

		AlgoSequence algoSeq2 = new AlgoSequence(cons, algoPascal.getOutput(0), k2,
				new GeoNumeric(cons, 1.0),
				(GeoNumberValue) plusOneAlgo.getOutput(0), null);
		cons.removeFromConstructionList(algoSeq2);

		GeoList probs = (GeoList) algoSeq2.getOutput(0);
		this.discreteProbability = new DiscreteProbability(values, probs);
		return discreteProbability;
	}
}
