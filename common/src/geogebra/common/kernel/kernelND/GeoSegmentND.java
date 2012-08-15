package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.LimitedPath;

/**
 * Simple interface used to join GeoSegment and GeoSegment3D
 * 
 * @author ggb3D
 *
 */
public interface GeoSegmentND extends GeoLineND, LimitedPath, GeoNumberValue, FromMeta {


	
	/**
	 * Sets start point and end point
	 * @param start start point
	 * @param end end point
	 */
	void setTwoPointsCoords(Coords start, Coords end);

	/** @return length of the segment */
	double getLength();

	/**
	 * @return start point
	 */
	public GeoElement getStartPointAsGeoElement();

	/**
	 * @return end point
	 */
	public GeoElement getEndPointAsGeoElement();

	/**
	 * return the x-coordinate of the point on the segment according to the parameter value
	 * @param parameter the parameter
	 * @return the x-coordinate of the point
	 */
	public double getPointX(double parameter);
	
	/**
	 * return the y-coordinate of the point on the segment according to the parameter value
	 * @param parameter the parameter
	 * @return the y-coordinate of the point
	 */
	public double getPointY(double parameter);


	/**
	 * modify the input points
	 * @param P new first point
	 * @param Q new second point
	 */
	public void modifyInputPoints(GeoPointND P, GeoPointND Q);

}
