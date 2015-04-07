package org.geogebra.common.kernel;

/**
 * Interface for objects that may need updating their descendants on EV change
 * (zoom etc.)
 * 
 * use to register for updates: cons.registerEuclidianViewCE(this);
 */
public interface EuclidianViewCE {

	/**
	 * 
	 * @return true if all dependent objects of this one shall be updated
	 *         (updateCascade) after all euclidianViewUpdates were executed.
	 */
	public boolean euclidianViewUpdate();

}
