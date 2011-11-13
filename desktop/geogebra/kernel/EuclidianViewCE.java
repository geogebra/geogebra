package geogebra.kernel;

public interface EuclidianViewCE {
	
	/**
	 * 
	 * @return true if all dependent objects of this one shall be updated (updateCascade)
	 *  after all euclidianViewUpdates were executed.
	 */
	public boolean euclidianViewUpdate();

}
