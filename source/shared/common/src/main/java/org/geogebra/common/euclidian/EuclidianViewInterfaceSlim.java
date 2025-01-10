package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collection;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.kernel.LayerView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Minimal interface for Euclidian view
 *
 */
public interface EuclidianViewInterfaceSlim extends LayerView {

	/**
	 * @return true if this is Graphics or Graphics 2
	 */
	boolean isDefault2D();

	/**
	 * @return true if this is Graphics 3D
	 */
	boolean isEuclidianView3D();

	/**
	 * @param algo
	 *            algorithm
	 * @return free input points of given algorithm
	 */
	ArrayList<GeoElementND> getFreeInputPoints(AlgoElement algo);

	/**
	 * @param geoElement
	 *            element
	 * @return true if the element can be moved freely in this view
	 */
	boolean isMoveable(GeoElement geoElement);

	/**
	 * @return width in pixels of physical view. use getMaxXScreen() -
	 *         getMinXScreen() if you need the width for exporting
	 */
	int getWidth();

	/**
	 * @return width in pixels of physical view as a double.
	 */
	double getWidthD();

	/**
	 * @return height in pixels of physical view. use getMaxYScreen() -
	 *         getMinYScreen() if you need the width for exporting
	 */
	int getHeight();

	/**
	 * @return height in pixels of physical view as a double.
	 */
	double getHeightD();

	/**
	 * @return width of the EV's visible part
	 */
	int getVisibleWidth();

	/**
	 * @return height of the EV's visible part
	 */
	int getVisibleHeight();

	/**
	 * @return visible width based on the values in the EuclidianSettings object
	 */
	int calcVisibleWidthFromSettings();

	/**
	 * @return visible height based on the values in the EuclidianSettings object
	 */
	int calcVisibleHeightFromSettings();

	/**
	 * convert screen coordinate x to real world coordinate x
	 *
	 * @param x
	 *            screen coord
	 * @return real world coord
	 */
	double toRealWorldCoordX(double x);

	/**
	 * convert screen coordinate x to real world coordinate x
	 *
	 * @param y
	 *            screen coord
	 * @return real world coord
	 */
	double toRealWorldCoordY(double y);

	/**
	 * Update bounds from bound objects
	 *
	 * @param updateDrawables
	 *            whether drawables need updating
	 * @param updateSettings
	 *            whether the settings object should be changed (to prevent
	 *            infinite recursion)
	 */
	void updateBounds(boolean updateDrawables, boolean updateSettings);

	/**
	 * Replaces old bound by new bound in all positions where it is used
	 *
	 * @param oldBound
	 *            old bound object
	 * @param newBound
	 *            replacement bound object
	 */
	void replaceBoundObject(GeoNumeric oldBound, GeoNumeric newBound);

	/**
	 * @return euclidian controller
	 */
	EuclidianController getEuclidianController();

	/**
	 * @return grid distance: {x-distance,y-distance}
	 */
	double[] getGridDistances();

	/**
	 * @return real world coord of right bound
	 */
	double getXmax();

	/**
	 * @return real world coord of top bound
	 */
	double getYmax();

	/**
	 * @return real world coord of left bound
	 */
	double getXmin();

	/**
	 * @return real world coord of bottom bound
	 */
	double getYmin();

	/**
	 * @return screen : real world x-coord ratio
	 */
	double getXscale();

	/**
	 * @return screen : real world y-coord ratio
	 */
	double getYscale();

	/**
	 * @param geo
	 *            geo
	 * @return drawable for given geo
	 */
	DrawableND getDrawableND(GeoElement geo);

	/**
	 * @param geo
	 *            geo
	 * @return new drawable for given geo
	 */
	DrawableND newDrawable(GeoElementND geo);

	/**
	 * Zooms w.r.t P with given zoom factor
	 *
	 * @param px
	 *            x(P)
	 * @param py
	 *            y(P)
	 * @param factor
	 *            zoom factor
	 * @param steps
	 *            number of animation steps
	 * @param storeUndo
	 *            true to store undo
	 */
	void zoom(double px, double py, double factor, int steps,
			boolean storeUndo);

	/**
	 * Returns point capturing mode.
	 *
	 * @return point capturing mode.
	 */
	int getPointCapturingMode();

	/**
	 * @param capturingMode
	 *            new point capturing mode
	 */
	void setPointCapturing(int capturingMode);

	/**
	 * @return list of points that are capturing other points
	 */
	Collection<? extends GeoPointND> getStickyPointList();

	/**
	 * @param r
	 *            new selection rectangle
	 */
	void setSelectionRectangle(GRectangle r);

	/**
	 * Set real world bounds of this view
	 *
	 * @param xmin
	 *            x min
	 * @param xmax
	 *            x max
	 * @param ymin
	 *            y min
	 * @param ymax
	 *            y max
	 */
	void setRealWorldCoordSystem(double xmin, double xmax, double ymin,
			double ymax);

	/**
	 * center the view on point
	 *
	 * @param point
	 *            point
	 */
	void centerView(GeoPointND point);

	/**
	 * @return y-offset of topmost slider in pixels
	 */
	int getSliderOffsetY();

	/**
	 * @return settings
	 */
	EuclidianSettings getSettings();

	/**
	 * @return y-offset of first combobox
	 */
	int getComboOffsetY();

	/**
	 * @param sys
	 *            coord system
	 * @return whether the system is incident with the one of this view
	 */
	boolean isInPlane(CoordSys sys);

	/**
	 * Get the safe area insets of this view.
	 * @return safe area insets
	 */
	EdgeInsets getSafeAreaInsets();

	/**
	 * Set the safe area insets for this view.
	 * @param safeAreaInsets safe area insets
	 */
	void setSafeAreaInsets(EdgeInsets safeAreaInsets);

	/**
	 * @param rwX
	 *            real world x-coord
	 * @return screen x-coord
	 */
	double toScreenCoordXd(double rwX);

	/**
	 * @param rwY
	 *            real world y-coord
	 * @return screen y-coord
	 */
	double toScreenCoordYd(double rwY);

}

