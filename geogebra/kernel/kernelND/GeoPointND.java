package geogebra.kernel.kernelND;

import geogebra.kernel.LocateableList;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.Region;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;



/**
 * 
 * @author ggb3D
 *
 * interface for stuff common to 2D and 3D points
 *
 */

public interface GeoPointND {

	
	/** Returns whether this point has changeable numbers as coordinates */
	public boolean hasChangeableCoordParentNumbers();

	public void setLabel(String string);

	public boolean isLabelSet();

	public String getLabel();

	public boolean isInfinite();

	public boolean showInEuclidianView();

	public void remove();
	
	public boolean getSpreadsheetTrace();

	public RegionParameters getRegionParameters();

	public void updateCoords2D();

	public double getX2D();
	
	public double getY2D();

	public void updateCoordsFrom2D(boolean b, CoordSys coordsys);

	public boolean isPointOnPath();

	public int getMode();
	
	public boolean isFinite();

	public void set(GeoPointND p);
	
	public String getStartPointXML();
	
	public LocateableList getLocateableList();

	/** return the coordinates of the vector (this,Q) 
	 * @param Q ending point
	 * @return coords of the vector */
	public double[] vectorTo(GeoPointND Q);
	
	
	public Coords getInhomCoords();
	
	public void getInhomCoords(double[] coords);
	
	public double distance(GeoPointND P);

	public boolean isPointInRegion();
	
	public int getPointSize();
	
	public boolean hasPath();
	
	public PathParameter getPathParameter();
	
	//public void doPath();
	
	public boolean hasRegion();
	
	/** 
	 * Sets homogeneous coordinates and updates
	 * inhomogeneous coordinates
	 */
	public void setCoords(double x, double y, double z);
	
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
     */
    public void setCoords2D(double x, double y, double z);
    
	
	/**
	 * @param dimension
	 * @return the coords of the point in the given dimension (extended or projected)
	 */
	public Coords getInhomCoordsInD(int dimension);
	
	/**
	 * @param dimension
	 * @return the coords of the point in the given dimension (extended or projected)
	 */
	public Coords getCoordsInD(int dimension);
	
	/**
	 * @param coordSys
	 * @return the coords of the point in 2D (projected on coord sys)
	 */
	public Coords getCoordsInD2(CoordSys coordSys);

	public int getPointStyle();

	public boolean getTrace();

	public Path getPath();

	public Region getRegion();
	
	
	/////////////////////////////////////////
	// MOVING THE POINT (3D)
	/////////////////////////////////////////
	
	
	public static int MOVE_MODE_NONE = 0; //for intersection points and fixed points
	public static int MOVE_MODE_XY = 1;
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

	public boolean isDefined();

	public void updateCoords();

	public void setUndefined();

	public void showUndefinedInAlgebraView(boolean b);

	public GeoPointND copy();

	public boolean isAbsoluteStartPoint();

	
	//private boolean movePointMode = MOVE_POINT_MODE_XY;

	
	
}
