package org.geogebra.common.kernel.kernelND;

import java.util.ArrayList;

import org.geogebra.common.kernel.LocateableList;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathOrPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.geos.SpreadsheetTraceable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * @author ggb3D
 *
 *         interface for stuff common to 2D and 3D points
 *
 */

public interface GeoPointND extends PointProperties, Translateable,
		SpreadsheetTraceable, PointRotateable, CoordStyle, VectorNDValue,
		Mirrorable, Dilateable, Animatable {

	/** cannot move */
	public static int MOVE_MODE_NONE = 0; // for intersection points and fixed
											// points
	/** cna move in xy directions */
	public static int MOVE_MODE_XY = 1;
	/** can move in z direction */
	public static int MOVE_MODE_Z = 2;
	/** use tool default: XY for move, Z for others */
	public static int MOVE_MODE_TOOL_DEFAULT = 3;
	/** can move in xyz directions */
	public static int MOVE_MODE_XYZ = 4;

	/** @return whether this point has changeable numbers as coordinates */
	@Override
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
	 * @return x-coord
	 */
	public double getInhomX();

	/**
	 * @return y-coord
	 */
	public double getInhomY();

	/**
	 * @return z-coord
	 */
	public double getInhomZ();

	/**
	 * @return x-coord for 2D
	 */
	public double getX2D();

	/**
	 * @return y-coord for 2D
	 */
	public double getY2D();

	/**
	 * @param b
	 *            update
	 * @param coordsys
	 *            coordinate system of 2D view
	 */
	public void updateCoordsFrom2D(boolean b, CoordSys coordsys);

	/**
	 * @param doPathOrRegion
	 *            do path or region
	 */
	public void updateCoordsFrom2D(boolean doPathOrRegion);

	/**
	 * @return true if all coords are finite
	 */
	public boolean isFinite();

	/**
	 * @param sb string builder to append the correct xml representation as a
	 * 	start point for an object (button, vector, text)
	 */
	void appendStartPointXML(StringBuilder sb);

	/**
	 * @return list of locateables this is a start point of
	 */
	public LocateableList getLocateableList();

	/**
	 * return the coordinates of the vector (this,Q)
	 * 
	 * @param Q
	 *            ending point
	 * @return coords of the vector
	 */
	public double[] vectorTo(GeoPointND Q);

	/**
	 * @return inhomogeneous coords
	 */
	public Coords getInhomCoords();

	/**
	 * @param coords
	 *            homogeneous coords
	 */
	public void getInhomCoords(double[] coords);

	/**
	 * @return path parameter
	 */
	public PathParameter getPathParameter();

	/**
	 * @return true if this is point in region
	 */
	public boolean hasRegion();

	/**
	 * Sets homogeneous coordinates and updates inhomogeneous coordinates
	 * 
	 * @param x
	 *            first coord
	 * @param y
	 *            second coord
	 * @param z
	 *            third coord
	 */
	public void setCoords(double x, double y, double z);

	/**
	 * Sets homogeneous coordinates and updates inhomogeneous coordinates
	 * 
	 * @param x
	 *            first coord
	 * @param y
	 *            second coord
	 * @param z
	 *            third coord
	 * @param w
	 *            fourth coord
	 */
	public void setCoords(double x, double y, double z, double w);

	/**
	 * Sets homogenous coordinates and updates inhomogenous coordinates
	 * 
	 * @param v
	 *            coords
	 * @param doPathOrRegion
	 *            says if path (or region) calculations have to be done
	 */
	public void setCoords(Coords v, boolean doPathOrRegion);

	/**
	 * set 2D coords
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setCoords2D(double x, double y, double z);

	/**
	 * @param dimension
	 *            dimension
	 * @return the coords of the point in the given dimension (extended or
	 *         projected)
	 */
	public Coords getInhomCoordsInD(int dimension);

	/**
	 * @return the coords of the point in 3D
	 */
	public Coords getInhomCoordsInD3();

	/**
	 * @return the coords of the point in 2D
	 */
	public Coords getInhomCoordsInD2();

	/**
	 * @return the coords of the point in 2D
	 */
	public Coords getCoordsInD2();

	/**
	 * @return the coords of the point in 3D
	 */
	public Coords getCoordsInD3();

	/**
	 * @param dimension
	 *            dimension
	 * @return the coords of the point in the given dimension (extended or
	 *         projected)
	 */
	public Coords getCoordsInD(int dimension);

	/**
	 * @param coordSys
	 *            coord system
	 * @return the coords of the point in 2D (projected on coord sys)
	 */
	public Coords getCoordsInD2(CoordSys coordSys);

	/**
	 * @param coordSys
	 *            coord system
	 * @return the coords of the point in 2D (projected on coord sys) or null if
	 *         not included in coord sys
	 */
	public Coords getCoordsInD2IfInPlane(CoordSys coordSys);

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

	/**
	 * sets the move mode (along xOy or along Oz)
	 * 
	 * @param mode
	 *            view tool mode
	 */
	public void switchMoveMode(int mode);

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
	 * @param b
	 *            flag to show/hide this in AV when undefined
	 */
	public void showUndefinedInAlgebraView(boolean b);

	/**
	 * @return copy of this point
	 */
	@Override
	public GeoPointND copy();

	/**
	 * @return tue if this is start point and has absolute screen position
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

	/**
	 * 
	 * @param path
	 *            a path
	 * @return distance from point to path
	 */
	public double distanceToPath(PathOrPoint path);

	/**
	 * @param path
	 *            path
	 * @param isStartPoint
	 *            whether this is start point of the path
	 */
	public void addIncidence(GeoElement path, boolean isStartPoint);

	/**
	 * @param path
	 *            path this belongs to
	 */
	public void setPath(Path path);

	public Coords getCoords();

	/**
	 * @return list of objects that use this as corner
	 */
	public boolean hasLocateableList();

	/**
	 * @param locateableList
	 *            list of locateables with this corner
	 */
	public void setLocateableList(LocateableList locateableList);

	/**
	 * Copy coordinates from point.
	 * 
	 * @param point
	 *            source point
	 */
	public void setCoordsFromPoint(GeoPointND point);

	/**
	 * @param geo
	 *            incident path
	 */
	public void removeIncidence(GeoElement geo);

	/**
	 * @return list of objects (paths) this belongs to
	 */
	public ArrayList<GeoElement> getIncidenceList();

	/**
	 * @param geo
	 *            point
	 * @return whether the two points are equal
	 */
	public boolean isEqualPointND(GeoPointND geo);

	/**
	 * Change coordinates of this point to linear combination of two MyPoints.
	 * 
	 * @param param1
	 *            weight of frst point
	 * @param param2
	 *            weight of second point
	 * @param leftPoint
	 *            first point
	 * @param rightPoint
	 *            second point
	 */
	public void set(double param1, double param2, MyPoint leftPoint,
			MyPoint rightPoint);

	/**
	 * @param phi
	 *            angle
	 * @param center
	 *            rotation center
	 */
	public void rotate(NumberValue phi, Coords center);

	/**
	 * @param r
	 *            parent region
	 */
	public void setRegion(Region r);

	/**
	 * @return animation value (0 to 1)
	 */
	public double getAnimationValue();

	/**
	 * @param val
	 *            animation value (0 to 1)
	 */
	public void setAnimationValue(double val);

	/**
	 * @param start
	 *            whether this point is animating
	 */
	public void setAnimating(boolean start);

	/**
	 * @param rwTransVec
	 *            translation vector (ignored if endPos given)
	 * @param endPosition
	 *            end position
	 * @return whether move happened
	 */
	public boolean movePoint(Coords rwTransVec, Coords endPosition);

	/**
	 * @param pointND
	 *            template element
	 * @param macroFeedback
	 *            whether to allow moving macro moveable outputs
	 */
	public void set(GeoElementND pointND, boolean macroFeedback);

	/**
	 * Remove reference to path
	 */
	public void removePath();

	/**
	 * used for GeoPoint3D
	 * 
	 * @param ccp
	 *            changeable coord parent
	 */
	public void setChangeableParentIfNull(ChangeableParent ccp);

	/**
	 * 
	 * @return current (3D) view zScale (if set)
	 */
	public double getZScale();

	/**
	 * @param tpl
	 *            - string template
	 * @return description for points ("Point A" instead of "A = (0,0)")
	 */
	public String toStringDescription(StringTemplate tpl);

	/**
	 * @return string mode: Kernel.COORD_COMPLEX, COORD_CARTESIAN etc.
	 */
	@Override
	public int getToStringMode();

	public void addToPathParameter(double d);

	/**
	 * set region changed with x, y coords
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 */
	void setRegionChanged(double x, double y);

	/**
	 * point changed on a polygon as path
	 * 
	 * @param polygon
	 *            polygon
	 */
	void pointChanged(GeoPolygon polygon);
}
