package org.geogebra.common.kernel;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.statistics.AlgoDistribution;
import org.geogebra.common.util.DoubleUtil;

public class AlgoInverseBinomialMinimumTrials extends AlgoDistribution {
	public static final int MAX_TRIALS = 10000;

	public AlgoInverseBinomialMinimumTrials(String label, Construction c, GeoNumberValue cumulativePropability,
			GeoNumberValue probability, GeoNumberValue numberOfTrials) {
		super(c, label, cumulativePropability, probability, numberOfTrials, null);

	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = a.toGeoElement(cons);
		input[1] = b.toGeoElement(cons);
		input[2] = c.toGeoElement(cons);
		super.setOutputLength(1);
		setOutput(0, num);
	}

	@Override
	public void compute() {
		if (isInvalidArguments()) {
			num.setUndefined();
			return;
		}
		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double cumulativeProbability = a.getDouble();
			double probability = b.getDouble();
			int trials = Math.min((int) Math.round(c.getDouble()), MAX_TRIALS);
			int count = 0;
				try {
					for (int n = 0; n < 1000; n++) {
						BinomialDistribution dist =	getBinomialDistribution(n, probability);
						double x = dist.cumulativeProbability(trials);
					if (x > cumulativeProbability) {
						count++;
					}
				}
				num.setValue(count);

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

	private boolean isInvalidArguments() {
		return isProbabilityOutOfRange(a)
				|| isProbabilityOutOfRange(b)
				|| isInvalidTrials();
	}

	private boolean isInvalidTrials() {
		double value = c.getDouble();
		if (!DoubleUtil.isInteger(value)) {
			return true;
		}
		return value < 0 || value > MAX_TRIALS;
	}

	private boolean isProbabilityOutOfRange(GeoNumberValue probability) {
		double value = probability.getDouble();
		return value < 0 || value > 1;
	}


	@Override
	public GetCommand getClassName() {
		return Commands.InverseBinomialMinimumTrials;
	}
}
