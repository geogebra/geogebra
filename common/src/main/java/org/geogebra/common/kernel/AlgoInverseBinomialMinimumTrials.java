package org.geogebra.common.kernel;

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

		num.setValue(1);
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
