package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;

/**
 * @author mathieu
 *
 * Interface for lines (lines, segments, ray, ...) in any dimension
 */
public interface GeoLineND extends GeoDirectionND{
	
	
	/** returns the point at position lambda on the coord sys in the dimension given
	 * @param dimension dimension of returned point
	 * @param lambda position on the line
	 * @return the point at position lambda on the coord sys  
	 * */
	public Coords getPointInD(int dimension, double lambda);
	/**
	 * @return true if tracing
	 */
	public boolean getTrace();
	
	/**
	 * @param m plane
	 * @return the (a,b,c) equation vector that describe the line
	 * in the plane described by the matrix m
	 * (ie ax+by+c=0 is an equation of the line in the plane)
	 */
	public Coords getCartesianEquationVector(CoordMatrix m);
	
	/**
	 * @return coords of the starting point
	 */
	public Coords getStartInhomCoords();



	/**
	 * @return inhom coords of the end point
	 */
	public Coords getEndInhomCoords();
	
	/** see PathOrPoint 
	 * @return min parameter */
	public double getMinParameter();
	
	/** see PathOrPoint 
	 * @return max parameter */
	public double getMaxParameter();

	/**
	 * 
	 * @param p point
	 * @param minPrecision precision
	 * @return true if point is on the path
	 */
	public boolean isOnPath(GeoPointND p, double minPrecision);
	
	/**
	 * @param coords point
	 * @param eps precision
	 * @return true if point is on path (with given precision)
	 */
	public boolean isOnPath(Coords coords, double eps);
	
	/**
	 * when intersection point is calculated, check if not outside limited path (segment, ray)
	 * @param coords point
	 * @param eps precision
	 * @return true if not outside
	 */
	public boolean respectLimitedPath(Coords coords, double eps);

	/**
	 * @param p point
	 * @param minPrecision precision
	 * @return true if point is on this line (ignoring limits for segment/ray)
	 */
	public boolean isOnFullLine(Coords p, double minPrecision);

	/**
	 * @return end point
	 */
	public GeoPointND getEndPoint();
	
	/**
	 * @return start point
	 */
	public GeoPointND getStartPoint();

	/**
	 * Removes a point from list of points that 
	 * are registered as points on this line
	 * @param point point to be removed
	 */
	public void removePointOnLine(GeoPointND point);
	
	/**
	 * Adds a point to the list of points that this line passes through.
	 * @param point point
	 */
	public void addPointOnLine(GeoPointND point);
	
	/**
	 * returns the distance from this line to line g.
	 * @param g line
	 * @return distance distance between this and g
	 */
	public double distance(GeoLineND g);
	

	/** set equation mode to implicit */
	public void setToImplicit();
	
	/** change equation mode to explicit */
	public void setToExplicit();
	
	/**
	 * Switch to parametric mode and set parameter name
	 * 
	 * @param parameter
	 *            name
	 */
	public void setToParametric(String parameter);

}
