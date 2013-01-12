package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.LocateableList;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.RegionParameters;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.PointProperties;

import java.util.TreeSet;



/**
 * 
 * @author ggb3D
 *
 * interface for stuff common to 2D and 3D points
 *
 */

public interface GeoPointND extends GeoElementND, PointProperties{

	
	/** @return whether this point has changeable numbers as coordinates */
	public boolean hasChangeableCoordParentNumbers();

	/**
	 * @return region parameters if this is point in region
	 */
	public RegionParameters getRegionParameters();

	/**
	 * Update coords for 2D from homogeneous coords
	 */
	public void updateCoords2D();

	/**
	 * @return x-coord for 2D
	 */
	public double getX2D();
	
	/**
	 * @return y-coord for 2D
	 */
	public double getY2D();

	/**
	 * @param b update
	 * @param coordsys coordinate system of 2D view
	 */
	public void updateCoordsFrom2D(boolean b, CoordSys coordsys);

	/**
	 * @return mode (complex / polar / cartesian)
	 */
	public int getMode();
	
	/**
	 * @return true if all coords are finite
	 */
	public boolean isFinite();

	/**
	 * @param p copy algebraic properties from another point
	 */
	public void set(GeoPointND p);
	
	/**
	 * @return string representation for XML if this is start point
	 * for some locateable
	 */
	public String getStartPointXML();
	
	/**
	 * @return list of locateables this is a start point of
	 */
	public LocateableList getLocateableList();

	/** return the coordinates of the vector (this,Q) 
	 * @param Q ending point
	 * @return coords of the vector */
	public double[] vectorTo(GeoPointND Q);
	
	
	/**
	 * @return inhomogeneous coords
	 */
	public Coords getInhomCoords();
	
	/**
	 * @param coords homogeneous coords
	 */
	public void getInhomCoords(double[] coords);

	/**
	 * @return true if this is point on path
	 *TODO merge with isPointOnPath
	 */
	public boolean hasPath();
	
	/**
	 * @return path parameter
	 */
	public PathParameter getPathParameter();
	
	/**
	 * @return true if this is point in region
	 */
	public boolean hasRegion();
	
	/** 
	 * Sets homogeneous coordinates and updates
	 * inhomogeneous coordinates
	 * @param x first coord
	 * @param y second coord
	 * @param z third coord
	 */
	public void setCoords(double x, double y, double z);
	
	/**
	 * Sets homogeneous coordinates and updates
	 * inhomogeneous coordinates
	 * @param x first coord
	 * @param y second coord
	 * @param z third coord
	 * @param w fourth coord
	 */
	public void setCoords(double x, double y, double z, double w);
	

    
	/** Sets homogenous coordinates and updates
	 * inhomogenous coordinates
	 * @param v coords
	 * @param doPathOrRegion says if path (or region) calculations have to be done
	 */    
	public void setCoords(Coords v, boolean doPathOrRegion);

    /** set 2D coords
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord
     */
    public void setCoords2D(double x, double y, double z);
    
	
	/**
	 * @param dimension dimension
	 * @return the coords of the point in the given dimension (extended or projected)
	 */
	public Coords getInhomCoordsInD(int dimension);
	
	/**
	 * @param dimension dimension
	 * @return the coords of the point in the given dimension (extended or projected)
	 */
	public Coords getCoordsInD(int dimension);
	
	/**
	 * @param coordSys coord system
	 * @return the coords of the point in 2D (projected on coord sys)
	 */
	public Coords getCoordsInD2(CoordSys coordSys);

	/**
	 * @return path on which this point lies
	 */
	public Path getPath();

	/**
	 * @return region in which this point lies
	 */
	public Region getRegion();
	
	
	/////////////////////////////////////////
	// MOVING THE POINT (3D)
	/////////////////////////////////////////
	
	/** cannot move */
	public static int MOVE_MODE_NONE = 0; //for intersection points and fixed points
	/** cna move in xy directions */
	public static int MOVE_MODE_XY = 1;
	/** can move in z direction*/
	public static int MOVE_MODE_Z = 2;
	
	/**
	 * sets the move mode (along xOy or along Oz)
	 */
	public void switchMoveMode();
	
	/**
	 * 
	 * @return the move mode (along xOy or along Oz)
	 */
	public int getMoveMode();

	/**
	 * Update inhomogenous coords based on homegenous
	 */
	public void updateCoords();

	/**
	 * @param b flag to show/hide this in AV when undefined
	 */
	public void showUndefinedInAlgebraView(boolean b);
	/**
	 * @return copy of this point
	 */
	public GeoPointND copy();

	/**
	 * @return tue if this is start point and 
	 * has absolute screen position
	 */
	public boolean isAbsoluteStartPoint();


	/**
	 * @return true if this can be displayed in EV
	 */
	public boolean showInEuclidianView();

	/**
	 * @return true if tracing
	 */
	public boolean getTrace();
	//private boolean movePointMode = MOVE_POINT_MODE_XY;
	/**
	 * @return 2 for 2D points, 3 for 3D points
	 */
	public int getDimension();
	
    /**
     * Changes coord style to CARTESIAN
     */
    public void setCartesian();
	
    /**
     * Changes coord style to CARTESIAN 3D
     */
	public void setCartesian3D();


	/**
	 * set that this point is vertex of the polygon
	 * @param polygon polygon
	 */
	public void setVertexOf(GeoPolygon polygon);
	
	/**
	 * 
	 * @return polygons that this point is vertex of
	 */
	public TreeSet<GeoPolygon> getVertexOf();
	

	
}
