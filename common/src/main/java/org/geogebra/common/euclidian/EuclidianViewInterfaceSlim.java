package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collection;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.LayerView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Minimal interface for Euclidian view
 *
 */
public interface EuclidianViewInterfaceSlim extends LayerView {

	/**
	 * @return true if this is Graphics or Graphics 2
	 */
	public boolean isDefault2D();

	/**
	 * @return true if this is Graphics 3D
	 */
	public boolean isEuclidianView3D();

	/**
	 * @param algo
	 *            algorithm
	 * @return free input points of given algorithm
	 */
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algo);

	/**
	 * @param geoElement
	 *            element
	 * @return true if the element can be moved freely in this view
	 */
	boolean isMoveable(GeoElement geoElement);

	/**
	 * @return width in pixels
	 */
	int getWidth();

	/**
	 * @return height in pixels
	 */
	int getHeight();

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 *            screen coord
	 * @return real world coord
	 */
	public double toRealWorldCoordX(double x);

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param y
	 *            screen coord
	 * @return real world coord
	 */
	public double toRealWorldCoordY(double y);

	/**
	 * Update bounds from bound objects
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
	DrawableND newDrawable(GeoElement geo);

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
	void zoom(double px, double py, double factor, int steps, boolean storeUndo);

	/**
	 * Returns point capturing mode.
	 * 
	 * @return point capturing mode.
	 */
	public int getPointCapturingMode();

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
	public void setRealWorldCoordSystem(double xmin, double xmax, double ymin,
			double ymax);

	/**
	 * center the view on point
	 * 
	 * @param point
	 *            point
	 */
	public void centerView(GeoPointND point);

	/**
	 * @return y-offset of topmost slider in pixels
	 */
	public int getSliderOffsetY();

	public double getMinPixelDistance();

	public EuclidianSettings getSettings();

	public int getComboOffsetY();

}
