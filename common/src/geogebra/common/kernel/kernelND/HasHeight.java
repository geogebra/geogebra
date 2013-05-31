package geogebra.common.kernel.kernelND;

/**
 * Interface for geos that have a height (cones, pyramids, prisms, etc.)
 * @author mathieu
 *
 */
public interface HasHeight {

	/**
	 * 
	 * @return algebraic value for height (can be negative)
	 */
	public double getOrientedHeight();
}
