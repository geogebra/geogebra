package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * 
 * @author mathieu
 * 
 *         view companion for methods that have to cross desktop/web
 *
 */
public class EuclidianViewCompanion {
	/** the view */
	protected EuclidianView view;

	/**
	 * constructor
	 * 
	 * @param view
	 *            view attached
	 */
	public EuclidianViewCompanion(EuclidianView view) {
		setView(view);
	}

	/**
	 * @param view
	 *            euclidian view
	 */
	protected void setView(EuclidianView view) {
		this.view = view;
	}

	/**
	 * 
	 * @return view attached
	 */
	public EuclidianView getView() {
		return view;
	}

	/**
	 * Updates xmin, xmax, ... for updateSize()
	 */
	public void setXYMinMaxForUpdateSize() {
		view.setXYMinMaxForSetCoordSystem();
	}

	/**
	 * 
	 * @param geo
	 *            angle
	 * @return drawable for this angle
	 */
	protected DrawAngle newDrawAngle(GeoAngle geo) {
		return new DrawAngle(view, geo);
	}

	/**
	 * @return whether this is EV1/EV2 rather than view for plane
	 */
	public boolean isDefault2D() {
		return true;
	}

	/**
	 * @param geo
	 *            geo
	 * @return true if geo is visible in this view
	 */
	public boolean isVisibleInThisView(GeoElementND geo) {
		return geo.isVisibleInView(view.getViewID());
	}

	/**
	 * tranform in view coords
	 * 
	 * @param coords
	 *            point
	 * @return the same coords for classic 2d view
	 */
	public Coords getCoordsForView(Coords coords) {
		return coords;
	}

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix representation of the plane shown by this view
	 */
	public CoordMatrix getMatrix() {
		return null;
	}

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix inverse representation of the plane shown by this view
	 */
	public CoordMatrix getInverseMatrix() {
		return null;
	}

	/**
	 * 
	 * @return string description of plane from the view was created
	 */
	public String getFromPlaneString() {
		return "xOyPlane";
	}

	/**
	 * 
	 * @return string translated description of plane from the view was created
	 */
	public String getTranslatedFromPlaneString() {
		return view.getApplication().getLocalization().getMenu("xOyPlane");
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoPlaneND getPlaneContaining() {
		return view.kernel.getDefaultPlane();
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoDirectionND getDirection() {
		return getPlaneContaining();
	}

	/**
	 * 
	 * @param v
	 *            vector
	 * @return true if v is oriented to z+ direction
	 */
	public boolean goToZPlus(Coords v) {
		return v.getZ() > 0;
	}

	/**
	 * @param geo
	 *            element
	 * @return true if the element can be moved freely in this view
	 */
	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}

	/**
	 * @param algo
	 *            algorithm
	 * @return free input points of given algorithm
	 */
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algo) {
		return algo.getFreeInputPoints();
	}

	/**
	 * add id to xml
	 * 
	 * @param sbxml
	 *            xml
	 */
	public void getXMLid(StringBuilder sbxml) {
		if (view.evNo >= 2) {
			getXMLidNoCheck(sbxml);
		}
	}

	/**
	 * add id to xml
	 * 
	 * @param sbxml
	 *            xml
	 */
	protected void getXMLidNoCheck(StringBuilder sbxml) {
		sbxml.append("\t<viewNumber ");
		sbxml.append("viewNo=\"");
		sbxml.append(view.evNo);
		sbxml.append("\"");
		sbxml.append("/>\n");
	}

	/**
	 * returns settings in XML format
	 * 
	 * @param sbxml
	 *            string builder
	 * @param asPreference
	 *            true for preferences
	 */
	public void getXML(StringBuilder sbxml, boolean asPreference) {
		view.startXML(sbxml, asPreference);
		view.endXML(sbxml);
	}

	/**
	 * @param settings
	 *            settings
	 */
	public void settingsChanged(AbstractSettings settings) {
		EuclidianSettings evs = (EuclidianSettings) settings;

		view.getKernel().getConstruction().setIgnoringNewTypes(true);
		setMinMaxObjectsInView(evs);
		view.getKernel().getConstruction().setIgnoringNewTypes(false);
		view.setBackground(evs.getBackground());
		view.setAxesColor(evs.getAxesColor());
		view.setGridColor(evs.getGridColor());
		view.setAxesLineStyle(evs.getAxesLineStyle());
		view.setGridLineStyle(evs.getGridLineStyle());

		// bold/italic/size for axes labels
		view.initFontCoords();

		double[] d = evs.getGridDistances();
		if (!evs.getAutomaticGridDistance() && (d == null)) {
			view.setAutomaticGridDistance(false);
		} else if (d == null) {
			view.setAutomaticGridDistance(true);
		} else {
			view.setGridDistances(d);
		}
		int viewDim = view.getDimension();
		for (int i = 0; i < viewDim; i++) {
			view.setShowAxis(i, evs.getShowAxis(i), true);
		}
		String[] tempAxesLabels = evs.getAxesLabels();

		// make sure <b>, <i> processed
		for (int i = 0; i < viewDim; i++) {
			view.setAxisLabel(i, tempAxesLabels[i]);
		}
		view.setAxesUnitLabels(evs.getAxesUnitLabels());

		view.showAxesNumbers = evs.getShowAxisNumbers();

		// might be Double.NaN, handled in setAxesNumberingDistance()
		for (int i = 0; i < viewDim; i++) {
			if (!evs.getAutomaticAxesNumberingDistance(i)
					&& isNaN(evs.getAxisNumberingDistance(i))) {
				view.setAutomaticAxesNumberingDistance(false, i);
			} else {
				view.setAxesNumberingDistance(evs.getAxisNumberingDistance(i),
						i);
			}
		}

		for (int i = 0; i < viewDim; i++) {
			view.setAxisTickStyle(i, evs.getAxesTickStyles()[i]);
		}

		view.setDrawBorderAxes(evs.getDrawBorderAxes());

		for (int i = 0; i < viewDim; i++) {
			view.axisCross[i] = evs.getAxesCross()[i];
			view.positiveAxes[i] = evs.getPositiveAxes()[i];
		}

		GDimension ps = evs.getPreferredSize();
		if (ps != null) {
			view.setPreferredSize(ps);
		}

		view.showGrid(evs.getShowGrid());

		view.setGridIsBold(evs.getGridIsBold());

		view.setGridType(evs.getGridType());

		view.pointCapturingMode = evs.getPointCapturingMode();

		view.setAllowShowMouseCoords(evs.getAllowShowMouseCoords());

		view.setAllowToolTips(evs.getAllowToolTips());

		view.synchronizeMenuBarAndEuclidianStyleBar(evs);

		if (!evs.hasDynamicBounds()) {
			// the xmin, xmax, ... we read from Settings are nulls;
			// use the double values instead
			double x0 = evs.getXZero();
			double y0 = evs.getYZero();
			if (view.getKeepCenter() && view.isShowing()) {
				// we may need to shift center if windows/settings sizes
				// don't match
				int w = view.getWidth();
				int h = view.getHeight();
				if (w > EuclidianView.MIN_WIDTH
						&& h > EuclidianView.MIN_HEIGHT) {
					int sw = evs.getWidth();
					int sh = evs.getHeight();
					// Log.debug("x0:" + x0 + ", y0:" + y0 + ", " + sw + "x"
					// + sh + ", view: " + w + "x" + h);
					if (sw == 0) {
						// no dimension from file: center the view
						sw = (int) Math.round(x0 * 2);
						sh = (int) Math.round(y0 * 2);
					}
					x0 += (w - sw) / 2.0;
					y0 += (h - sh) / 2.0;
					evs.setSize(w, h);
					evs.setOriginNoUpdate(x0, y0);
				}
				// Log.debug(">> x0:" + x0 + ", y0:" + y0);
			}

			view.setCoordSystem(x0, y0, evs.getXscale(), evs.getYscale(), true);
			setMinMaxObjectsInSettings(evs);
		} else {
			// xmin, ... are OK; just update bounds
			view.updateBounds(true, true);
		}

		// let's do this after other updates because this might override e.g.
		// xmin
		view.setLockedAxesRatio(evs.getLockedAxesRatio());
	}

	private static boolean isNaN(GeoNumberValue axisNumberingDistance) {
		return axisNumberingDistance == null
				|| Double.isNaN(axisNumberingDistance.getDouble());
	}

	protected void setMinMaxObjectsInView(EuclidianSettings evs) {
		view.setXminObject(evs.getXminObject());
		view.setXmaxObject(evs.getXmaxObject());
		view.setYminObject(evs.getYminObject());
		view.setYmaxObject(evs.getYmaxObject());
	}

	protected void setMinMaxObjectsInSettings(EuclidianSettings evs) {
		evs.setXminObject(view.xminObject, false);
		evs.setXmaxObject(view.xmaxObject, false);
		evs.setYminObject(view.yminObject, false);
		evs.setYmaxObject(view.ymaxObject, false);
	}

	/**
	 * Paints content of this view.
	 * 
	 * @param g2
	 *            graphics
	 */
	public void paint(GGraphics2D g2) {
		view.paintTheBackground(g2);

		g2.setAntialiasing();

		// draw equations, checkboxes and all geo objects
		view.drawObjects(g2);

		if (view.selectionRectangle != null) {
			view.drawZoomRectangle(g2);
		}

		// draw bounding box
		if (view.getBoundingBox() != null) {
			view.getBoundingBox().draw(g2);
		}

		if (view.getFocusedGroupGeoBoundingBox() != null) {
			view.getFocusedGroupGeoBoundingBox().draw(g2);
		}

		// draw shape preview for shape tools
		view.drawShapePreview(g2);

		if (view.deletionRectangle != null) {
			view.drawRect(g2, EuclidianView.colDeletionSquare,
					EuclidianView.strokeDeletionSquare, view.deletionRectangle);
		}

		if (view.allowShowMouseCoords && view.showMouseCoords
				&& (view.showAxes[0] || view.showAxes[1] || view.showGrid)) {
			view.drawMouseCoords(g2);
		}
		if (view.showAxesRatio) {
			view.drawAxesRatio(g2);
		}

		if (view.kernel.needToShowAnimationButton()) {
			view.drawAnimationButtons(g2);
		}
	}

	/**
	 * Attach this view to kernel and add all objects created so far
	 */
	public void attachView() {
		view.kernel.notifyAddAll(view);
		view.kernel.attach(view);
	}

	/**
	 * @param show
	 *            true to show grid
	 * @return whether the setting changed
	 */
	public boolean showGrid(boolean show) {
		if (show == view.showGrid) {
			return false;
		}
		view.showGrid = show;
		view.updateBackgroundImage();
		return true;
	}

	/**
	 * @param geo
	 *            geo
	 * @return new drawable for given geo
	 */
	public DrawableND newDrawable(GeoElementND geo) {
		return EuclidianDraw.newDrawable(view, geo);
	}

	/**
	 * Returns transform from eigenvector space to screen coords
	 * 
	 * @param conic
	 *            conic
	 * @param M
	 *            conic's midpoint
	 * @param ev
	 *            eigenvectors
	 * @return affine transform of the conic for this view
	 */
	public GAffineTransform getTransform(GeoConicND conic, Coords M,
			Coords[] ev) {
		return conic.getAffineTransform();
	}

	/**
	 * tranform point coords in view coords
	 * 
	 * @param point
	 *            point
	 * @return point coords in view coords
	 */
	public Coords getCoordsForView(GeoPointND point) {
		return point.getInhomCoords();
	}

	/**
	 * Size changed, make sure our settings reflect that but do not update
	 * drawables
	 */
	protected void updateSizeKeepDrawables() {
		view.updateSizeKeepDrawables();
	}

	/**
	 * @param sys
	 *            coord system
	 * @return whether the coord system is in the plane of this view
	 */
	public boolean isInPlane(CoordSys sys) {
		return sys.getEquationVector()
				.isEqual(CoordSys.Identity3D.getEquationVector());
	}

	/**
	 * @param xRW
	 *            real world x-coord
	 * @param yRW
	 *            real world y-coord
	 * @param tmpCoordsL3
	 *            target coordinates
	 */
	public void getCoordsFromView(double xRW, double yRW, Coords tmpCoordsL3) {
		tmpCoordsL3.setX(xRW);
		tmpCoordsL3.setY(yRW);
		tmpCoordsL3.setZ(0);
	}

	/**
	 * set hits for current mouse loc
	 * 
	 * @param type
	 *            event type
	 */
	public void setHits(PointerEventType type) {
		view.setHits(view.getEuclidianController().getMouseLoc(), type);
	}

	/**
	 * @return whether mouse is hovering over this view
	 */
	public boolean hasMouse() {
		return view.hasMouse2D();
	}

	/**
	 * @param mode
	 *            mode
	 * @param m
	 *            mode setting event type
	 */
	public void setMode(int mode, ModeSetter m) {
		// used for some input3D
	}

	public DrawableND newDrawParametricCurve(ParametricCurve geo) {
		return new DrawParametricCurve(view, geo);
	}

}
