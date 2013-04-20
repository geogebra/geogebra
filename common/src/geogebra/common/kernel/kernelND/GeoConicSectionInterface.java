package geogebra.common.kernel.kernelND;

/**
 * Interface for conic sections (3D geos)
 * @author mathieu
 *
 */
public interface GeoConicSectionInterface {

	
	/**
	 * @param index index of the hole
	 * @return start parameter
	 */
	public double getParameterStart(int index);
	
	/**
	 * @param index index of the hole
	 * @return end parameter - start parameter
	 */
	public double getParameterExtent(int index);
	
	
	
}
