package geogebra.common.kernel.geos;

import geogebra.common.kernel.Path;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Common interface for polygons and polylines
 * @author Zbynek
 */
public interface GeoPolyLineInterface {
	/**
	 * Returns true iff all vertices are labeled
	 * @return true iff all vertices are labeled
	 */
	public boolean isAllVertexLabelsSet() ;

	/**
	 * Returns true iff number of vertices is not volatile
	 * @return true iff number of vertices is not volatile
	 */
	public boolean isVertexCountFixed() ;
	
	/**
	 * Returns array of all vertices
	 * @return array of all vertices
	 */
	public GeoPointND[] getPoints(); 
	
	/**
	 * Returns i-th vertex
	 * @param i index
	 * @return i-th vertex
	 */
	public GeoPoint2 getPoint(int i);
	
	/**
	 * Converts polyline to cartesian curve
	 * @param outGeo curve to store result
	 */
	public void toGeoCurveCartesian(GeoCurveCartesian outGeo);
	/**
	 * @return boundary as Path
	 */
	public Path getBoundary();
	/**
	 * @return array of all vertices
	 */
	public GeoPointND[] getPointsND();


}
