package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoStats1D;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

public class AlgoSampleStandardDeviation extends AlgoStats1D {

	public AlgoSampleStandardDeviation(Construction cons, String label,
			GeoList geoList) {
		super(cons, label, geoList, AlgoStats1D.STATS_SAMPLE_SD);
	}

	public AlgoSampleStandardDeviation(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_SAMPLE_SD);
	}

	public AlgoSampleStandardDeviation(Construction cons, String label,
			GeoList geoList, GeoList freq) {
		super(cons, label, geoList, freq, AlgoStats1D.STATS_SAMPLE_SD);
	}

	public AlgoSampleStandardDeviation(Construction cons, GeoList geoList,
			GeoList freq) {
		super(cons, geoList, freq, AlgoStats1D.STATS_SAMPLE_SD);
	}

	@Override
	public Commands getClassName() {
		return Commands.SampleSD;
	}
}
