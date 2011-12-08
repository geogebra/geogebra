package geogebra.common.kernel.geos;
//IMPORTANT: don't change or delete this during the refactoring,
//as this is an interface intentionally 

import geogebra.common.kernel.Path;
import geogebra.common.kernel.kernelND.GeoPointND;



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
	
	public GeoPointND[] getPoints(); 
	
	public GeoPoint2 getPoint(int i);
	
	/**
	 * Converts polyline to cartesian curve
	 * @param outGeo
	 */
	public void toGeoCurveCartesian(GeoCurveCartesian outGeo);

	public Path getBoundary();

	public GeoPointND[] getPointsND();


}
