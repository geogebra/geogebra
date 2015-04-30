package org.geogebra.common.kernel.kernelND;

/**
 * Interface to set/get coords mode (cartesian / polar / etc.)
 * 
 * @author mathieu
 *
 */
public interface HasCoordsMode {

	public int getMode();

	public void setMode(int coordCartesian);

}
