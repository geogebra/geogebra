package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;



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
	
	public GeoPoint getPoint(int i);
	
	/**
	 * Converts polyline to cartesian curve
	 * @param outGeo
	 */
	public void toGeoCurveCartesian(GeoCurveCartesian outGeo);

	public Path getBoundary();


}
