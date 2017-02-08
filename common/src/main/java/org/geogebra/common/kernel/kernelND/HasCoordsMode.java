package org.geogebra.common.kernel.kernelND;

/**
 * Interface to set/get coords mode (cartesian / polar / etc.)
 * 
 * @author mathieu
 *
 */
public interface HasCoordsMode {

	/**
	 * @return coords mode
	 */
	public int getMode();

	/**
	 * @param coordCartesian
	 *            coords mode, see Kernel.COORDS_POLAR etc
	 */
	public void setMode(int coordCartesian);

}
