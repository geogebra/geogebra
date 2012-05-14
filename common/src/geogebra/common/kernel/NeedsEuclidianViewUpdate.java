package geogebra.common.kernel;
/**
 * Interface for objects that may need updting their descendants on EV change
 * (zoom etc.)
 */
public interface NeedsEuclidianViewUpdate {
	
	/**
	 * 
	 * @return true if all dependent objects of this one shall be updated (updateCascade)
	 *  after all euclidianViewUpdates were executed.
	 */
	public boolean euclidianViewUpdate();

}
