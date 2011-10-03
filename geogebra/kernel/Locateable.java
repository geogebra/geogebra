package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;


/**
 * Interface for GeoElements that have a start point (GeoText, GeoVector)
 */
public interface Locateable {
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException;
	public void removeStartPoint(GeoPointND p);	
	public GeoPointND getStartPoint();
		
	// GeoImage has three startPoints (i.e. corners)
	public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException;
	public GeoPointND [] getStartPoints();
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPointND p, int number);
	
	public boolean hasAbsoluteLocation();
	
	public boolean isAlwaysFixed();
	
	/**
	 * Use this method to tell the locateable that its
	 * startpoint will be set soon. (This is needed
	 * during XML parsing, as startpoints are processed
	 * at the end of a construction, @see geogebra.io.MyXMLHandler)
	 */
	public void setWaitForStartPoint();
	
	public GeoElement toGeoElement();
}
