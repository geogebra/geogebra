package geogebra.kernel.kernelND;

import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.Coords;

/**
 * @author mathieu
 *
 * Interface for lines (lines, segments, ray, ...) in any dimension
 */
public interface GeoLineND extends GeoDirectionND{
	
	
	/** returns the point at position lambda on the coord sys in the dimension given
	 * @param dimension 
	 * @param lambda 
	 * @return the point at position lambda on the coord sys  
	 * */
	public Coords getPointInD(int dimension, double lambda);

	public boolean getTrace();
	
	/**
	 * 
	 * @param m
	 * @return the (a,b,c) equation vector that describe the line
	 * in the plane described by the matrix m
	 * (ie ax+by+c=0 is an equation of the line in the plane)
	 */
	public Coords getCartesianEquationVector(CoordMatrix m);
	
	/**
	 * 
	 * @param dimension 
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
	 * @param p
	 * @param minPrecision
	 * @return true if point is on the path
	 */
	public boolean isOnPath(GeoPointND p, double minPrecision);
	
	public boolean isOnPath(Coords coords, double eps);
	
	/**
	 * when intersection point is calculated, check if not outside limited path (segment, ray)
	 * @param coords
	 * @param eps
	 * @return true if not outside
	 */
	public boolean respectLimitedPath(Coords coords, double eps);

	public boolean isOnFullLine(Coords p, double minPrecision);

	public GeoPointND getEndPoint();
	
	public GeoPointND getStartPoint();

}
