package geogebra.kernel;

/**
 * Algos that contain information needed for object drawing
 * @author kondr
 *
 */
public interface AlgoDrawInformation {

	/**
	 * Make a placeholder for this algo containing all info necessary for drawing
	 * @return algo placeholder
	 */
	public AlgoDrawInformation copy();	


}
