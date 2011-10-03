package geogebra.kernel.statistics;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;

public class AlgoSampleVariance extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoSampleVariance(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SAMPLE_VARIANCE);
    }

    public String getClassName() {
        return "AlgoSampleVariance";
    }
}
