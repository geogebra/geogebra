package geogebra.common.adapters;

public interface LegendreGaussIntegrator {
	double integrate(RealRootAdapter func, double xfrom, double xto) throws Exception;
}
