package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;

public class AlgoInverseBinomialMinimumTrials extends AlgoElement {
	public static final int MAX_TRIALS = 10000;
	private final GeoNumberValue cp;
	private final GeoNumberValue p;
	private final GeoNumberValue numberOfTrials;
	private final GeoNumeric num;

	public AlgoInverseBinomialMinimumTrials(String label, Construction c, GeoNumberValue cp,
			GeoNumberValue p, GeoNumberValue numberOfTrials) {
		super(c);
		this.cp = cp;
		this.p = p;
		this.numberOfTrials = numberOfTrials;
		num = new GeoNumeric(cons);
		setInputOutput();
		compute();
		setDependencies();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = cp.toGeoElement(cons);
		input[1] = p.toGeoElement(cons);
		input[2] = numberOfTrials.toGeoElement(cons);
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
		return isProbabilityOutOfRange(cp) || isProbabilityOutOfRange(p)
				|| isInvalidTrials(numberOfTrials.getDouble());
	}

	private boolean isInvalidTrials(double value) {
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

	public GeoElement getResult() {
		return num;
	}
}
