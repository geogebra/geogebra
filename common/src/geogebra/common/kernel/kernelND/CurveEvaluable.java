package geogebra.common.kernel.kernelND;


/**
 * Interface for geos that can be evaluate as a curve.
 * evaluateCurve() may compute 2D or 3D points.
 * @author mathieu
 *
 */
public interface CurveEvaluable {

	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return minimum parameter value for this path
	 */
	public double getMinParameter();
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return maximum parameter value for this path
	 */
	public double getMaxParameter();
	

	/**
	 * create new double[] to store point coords
	 * @return new double[] with correct dimension
	 */
	public double[] newDoubleArray();
	
	/**
	 * Evaluates the curve for given parameter value
	 * @param t parameter value
	 * @param out array to store the result
	 */
	void evaluateCurve(double t, double [] out);
	
	
	/**
	 * @param a start parameter
	 * @param b end parameter
	 * @return an interval within [a, b] where the curve is defined.
	 * 
	 */
	public double[] getDefinedInterval(double a, double b);

}
