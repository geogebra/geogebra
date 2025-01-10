package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoStats1D;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

public class AlgoSampleVariance extends AlgoStats1D {

	public AlgoSampleVariance(Construction cons,
			GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_SAMPLE_VARIANCE);
	}

	public AlgoSampleVariance(Construction cons, GeoList geoList,
			GeoList freq) {
		super(cons, geoList, freq, AlgoStats1D.STATS_SAMPLE_VARIANCE);
	}

	@Override
	public Commands getClassName() {
		return Commands.SampleVariance;
	}

}
