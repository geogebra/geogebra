package geogebra.common.kernel;

import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Interface for GeoElements that have a start point (GeoText, GeoVector)
 */
public interface Locateable extends GeoElementND{
	/**
	 * @param p start point 
	 * @throws CircularDefinitionException in case the start point depends on this object
	 */
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException;
	/**
	 * Unregisters start point
	 * @param p start point to remove
	 */
	public void removeStartPoint(GeoPointND p);	
	/**
	 * Returns (first) start point
	 * @return start point
	 */
	public GeoPointND getStartPoint();
		
	/**
	 * @param p start point
	 * @param number index (GeoImage has three startPoints (i.e. corners)) 
	 * @throws CircularDefinitionException in case the start point depends on this object
	 */
	public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException;
	
	/**
	 * @return array of all start points
	 */
	public GeoPointND [] getStartPoints();
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 * @param p start point
	 * @param number index
	 */
	public void initStartPoint(GeoPointND p, int number);
	
	/**
	 * @return true iff the location is absolute
	 */
	public boolean hasAbsoluteLocation();
	
	/**
	 * @return true iff object is always fixed
	 */
	public boolean isAlwaysFixed();
	
	/**
	 * Use this method to tell the locateable that its
	 * startpoint will be set soon. (This is needed
	 * during XML parsing, as startpoints are processed
	 * at the end of a construction, @see geogebra.io.MyXMLHandler)
	 */
	public void setWaitForStartPoint();
	public void updateLocation();

}
