package geogebra.kernel.statistics;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;

public class AlgoSampleStandardDeviation extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoSampleStandardDeviation(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SAMPLE_SD);
    }

	public AlgoSampleStandardDeviation(Construction cons, GeoList geoList) {
        super(cons,geoList,AlgoStats1D.STATS_SAMPLE_SD);
    }

    public String getClassName() {
        return "AlgoSampleStandardDeviation";
    }
}

