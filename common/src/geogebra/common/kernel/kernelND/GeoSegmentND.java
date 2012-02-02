package geogebra.common.kernel.kernelND;

import geogebra.common.awt.Color;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.LimitedPath;

/**
 * Simple interface used to join GeoSegment and GeoSegment3D
 * 
 * @author ggb3D
 *
 */
public interface GeoSegmentND extends GeoLineND, LimitedPath {


	void setLabel(String string);

	void setObjColor(Color objectColor);

	void setEuclidianVisible(boolean visible);

	void setTwoPointsCoords(Coords start, Coords end);
	
	void update();

	void setLineType(int type);

	void setLineThickness(int th);

	
	double getLength();

	public GeoElement getStartPointAsGeoElement();

	public GeoElement getEndPointAsGeoElement();

	
	////////////////////////////////////////////////
	// Path Interface
	
	boolean isOnPath(GeoPointND p, double eps);
	
	void pointChanged(GeoPointND p);
	
	public void pathChanged(GeoPointND PI);

	
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


	boolean isEuclidianVisible();

	
	// highlighting when segment of a polygon or polyhedron
	/**
	 * sets the highlighting ancestor
	 * @param geo 
	 * 
	 */
	public void setHighlightingAncestor(GeoElement geo);
	
	/**
	 * 
	 * @return the highlighting ancestor
	 */
	public GeoElement getHighlightingAncestor();


	boolean isLabelVisible();


	public GeoPointND getStartPoint();

	public GeoPointND getEndPoint();

	public void updateVisualStyle();

	public boolean keepsTypeOnGeometricTransform();

	/**
	 * modify the input points
	 * @param P new first point
	 * @param Q new second point
	 */
	public void modifyInputPoints(GeoPointND P, GeoPointND Q);

}
