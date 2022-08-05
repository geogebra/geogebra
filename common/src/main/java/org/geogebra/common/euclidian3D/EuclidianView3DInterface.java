package org.geogebra.common.euclidian3D;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * @author mathieu
 *
 *         Interface for 3D view
 */
public interface EuclidianView3DInterface extends EuclidianViewInterfaceCommon {
	/** default scene x-coord of origin */
	public static final double XZERO_SCENE_STANDARD = 0;
	/** default scene y-coord of origin */
	public static final double YZERO_SCENE_STANDARD = 0;
	/** default scene z-coord of origin */
	public static final double ZZERO_SCENE_STANDARD = -1.5;
	public final static double ANGLE_ROT_OZ = -60;
	public final static double ANGLE_ROT_XOY = 20;

    final static public int PROJECTION_ORTHOGRAPHIC = 0;
    final static public int PROJECTION_PERSPECTIVE = 1;
    final static public int PROJECTION_GLASSES = 2;
    final static public int PROJECTION_OBLIQUE = 3;

	/**
	 * rotate to default
	 */
	public void setDefaultRotAnimation();

	/**
	 * start a rotation animation to be in the vector direction
	 * 
	 * @param vn
	 *            vector direction
	 * @param checkSameValues
	 *            say if we check already in vector direction, to swap the view
	 * @param animated
	 *            say if rotation will be animated
	 */
	public void setRotAnimation(Coords vn, boolean checkSameValues,
			boolean animated);

	/**
	 * start a rotation animation to set angle around Oz axis
	 * 
	 * @param rotOz
	 *            angle around Oz
	 * @param checkSameValues
	 *            say if we check already in vector direction, to swap the view
	 * @param animated
	 *            say if rotation will be animated
	 */
	public void setRotAnimation(double rotOz, boolean checkSameValues,
			boolean animated);

	/**
	 * start a rotation animation to be in the vector direction, shortest way
	 * 
	 * @param v
	 *            vector direction
	 * @param animated
	 *            say if rotation will be animated
	 */
	public void setClosestRotAnimation(Coords v, boolean animated);

	/**
	 * @return Returns the zmin.
	 */
	public double getZmin();

	/**
	 * @return Returns the zmax.
	 */
	public double getZmax();

	/**
	 * sets the use of the clipping cube
	 * 
	 * @param flag
	 *            flag
	 */
	public void setUseClippingCube(boolean flag);

	/**
	 * sets if the clipping cube is shown
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowClippingCube(boolean flag);

	/**
	 * sets the reduction of the clipping box
	 * 
	 * @param value
	 *            reduction
	 */
	public void setClippingReduction(int value);

	/**
	 * 
	 * @param projection
	 *            projection type
	 */
	public void setProjection(int projection);

	/**
	 * sets the visibility of xOy plane grid
	 * 
	 * @param flag
	 *            flag
	 * @return whether it changed
	 */
	public boolean setShowGrid(boolean flag);

	/**
	 * sets the visibility of xOy plane
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowPlane(boolean flag);

	/**
	 * sets the visibility of xOy plane plate
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowPlate(boolean flag);

	/**
	 * sets the rotation matrix
	 * 
	 * @param theta
	 *            argument
	 * @param phi
	 *            alt angle
	 */
	public void setRotXYinDegrees(double theta, double phi);

	/**
	 * sets the origin
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public void setZeroFromXML(double x, double y, double z);

	/**
	 * set Matrix for view3D
	 */
	public void updateMatrix();

	/**
	 * tells the view it has changed
	 */
	public void setViewChanged();

	/**
	 * tell the view that it has to be updated
	 * 
	 */
	public void setWaitForUpdate();

	/**
	 * set if y axis is up (and not z axis)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setYAxisVertical(boolean flag);

	/**
	 * @return screen z-coord of origin
	 */
	public double getZZero();

	/**
	 * update all drawables
	 */
	public void updateAllDrawables();

	/**
	 * 
	 * @return eye position
	 */
	public Coords getEyePosition();

	/**
	 * @param boundsMin2
	 *            real world view min
	 * @param boundsMax2
	 *            real world view max
	 */
	public void zoomRW(Coords boundsMin2, Coords boundsMax2);
	
	/**
	 * set export will be done on next 3D frame
	 * 
	 * @param format - export format
	 * @param showDialog - true if export dialog should be shown, export directly otherwise
	 */
	public void setExport3D(final Format format, boolean showDialog);
	
	/**
	 * zoom y & z axes ratio regarding x axis
	 * 
	 * @param zoomFactorY
	 *            y:x ratio
	 * @param zoomFactorZ
	 *            z:x ratio
	 */
	public void zoomAxesRatio(double zoomFactorY, double zoomFactorZ);

	/**
	 * Keeps the zoom, but makes sure the bound objects are free. This is necessary
	 * in File->New because there might have been dynamic xmin bounds
	 */
	public void resetXYMinMaxObjects();

	/**
	 * set the settings to standard view position, orientation, scaling
	 */
	public void setSettingsToStandardView();

	/**
	 * 
	 * @return renderer
	 */
	public Renderer getRenderer();

	public void setXZero(double xZero);

	public void setYZero(double xZero);

	public void setZZero(double xZero);

	/**
	 * @param da
	 *            angle change
	 */
	public void shiftRotAboutZ(double da);

	/**
	 * @param db
	 *            angle change
	 */
	void shiftRotAboutY(double db);

	/**
	 * @return angle around Oz
	 */
	public double getAngleA();

	/**
	 * @return xOy plane tilting
	 */
	double getAngleB();

	/**
	 * show focus on geo (if something needs to be done)
	 * 
	 * @param geo
	 *            geo
	 */
	public void showFocusOn(GeoElement geo);

	/**
	 * set 3D cursor visibility
	 * 
	 * @param flag
	 *            flag
	 */
	public void setCursor3DVisible(boolean flag);

	/**
	 * return the matrix : screen coords -> scene coords.
	 *
	 * @return the matrix : screen coords -> scene coords.
	 */
	public CoordMatrix4x4 getToSceneMatrix();

	/**
	 * @return screen : real world z-coord ratio
	 */
	double getZscale();

}
