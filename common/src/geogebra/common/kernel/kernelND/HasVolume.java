package geogebra.common.kernel.kernelND;

/**
 * Interface for geos that have volume (spheres, prisms, etc.)
 * @author mathieu
 *
 */
public interface HasVolume {
	
	/**
	 * 
	 * @return volume
	 */
	public double getVolume();
	
	/**
	 * 
	 * @return true if the volume is finite
	 */
	public boolean hasFiniteVolume();

}
