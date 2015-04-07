package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoStats1D;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

public class AlgoSampleVariance extends AlgoStats1D {

	public AlgoSampleVariance(Construction cons, String label, GeoList geoList) {
		super(cons, label, geoList, AlgoStats1D.STATS_SAMPLE_VARIANCE);
	}

	public AlgoSampleVariance(Construction cons, String label, GeoList geoList,
			GeoList freq) {
		super(cons, label, geoList, freq, AlgoStats1D.STATS_SAMPLE_VARIANCE);
	}

	@Override
	public Commands getClassName() {
		return Commands.SampleVariance;
	}

}
