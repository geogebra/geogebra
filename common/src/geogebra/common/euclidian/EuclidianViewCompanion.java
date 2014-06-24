package geogebra.common.euclidian;

import geogebra.common.awt.GDimension;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.EuclidianSettings;

import java.util.ArrayList;



/**
 * 
 * @author mathieu
 * 
 * view companion for methods that have to cross desktop/web
 *
 */
public class EuclidianViewCompanion {

	protected EuclidianView view;


	/**
	 * constructor
	 * @param view view attached
	 */
	public EuclidianViewCompanion(EuclidianView view){
		this.view = view;
	}
	
	/**
	 * 
	 * @return view attached
	 */
	public EuclidianView getView(){
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
	
	
	public boolean isDefault2D() {
		return true;
	}
	
	/**
	 * @param geo
	 *            geo
	 * @return true if geo is visible in this view
	 */
	public boolean isVisibleInThisView(GeoElement geo) {
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
		return view.getApplication().getPlain("xOyPlane");
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
	 * @param v vector
	 * @return true if v is oriented to z+ direction
	 */
	public boolean goToZPlus(Coords v){
		return v.getZ() > 0;
	}
	
	/**
	 * @param geoElement element
	 * @return true if the element can be moved freely in this view
	 */
	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}
	
	
	/**
	 * @param algo algorithm
	 * @return free input points of given algorithm
	 */
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algoParent) {
		return algoParent.getFreeInputPoints();
	}
	
	
	/**
	 * add id to xml
	 * @param sbxml xml
	 */
	public void getXMLid(StringBuilder sbxml){
		if (view.evNo >= 2) {
			sbxml.append("\t<viewNumber ");
			sbxml.append("viewNo=\"");
			sbxml.append(view.evNo);
			sbxml.append("\"");
			sbxml.append("/>\n");
		}
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
		
		view.setXminObject(evs.getXminObject());
		view.setXmaxObject(evs.getXmaxObject());
		view.setYminObject(evs.getYminObject());
		view.setYmaxObject(evs.getYmaxObject());

		view.setBackground(evs.getBackground());
		view.setAxesColor(evs.getAxesColor());
		view.setGridColor(evs.getGridColor());
		view.setAxesLineStyle(evs.getAxesLineStyle());
		view.setGridLineStyle(evs.getGridLineStyle());

		double[] d = evs.getGridDistances();
		if (!evs.getAutomaticGridDistance() && (d == null)) {
			view.setAutomaticGridDistance(false);
		} else if (d == null) {
			view.setAutomaticGridDistance(true);
		} else {
			view.setGridDistances(d);
		}

		view.setShowAxis(0, evs.getShowAxis(0), true);
		view.setShowAxis(1, evs.getShowAxis(1), true);
		String[] tempAxesLabels = evs.getAxesLabels(); 

		// make sure <b>, <i> processed 
		view.setAxisLabel(0, tempAxesLabels[0]); 
		view.setAxisLabel(1, tempAxesLabels[1]);		
		view.setAxesUnitLabels(evs.getAxesUnitLabels());

		view.showAxesNumbers = evs.getShowAxisNumbers();

		// might be Double.NaN, handled in setAxesNumberingDistance()
		if (!evs.getAutomaticAxesNumberingDistance(0)
				&& Double.isNaN(evs.getAxisNumberingDistanceX())) {
			view.setAutomaticAxesNumberingDistance(false, 0);
		} else {
			view.setAxesNumberingDistance(evs.getAxisNumberingDistanceX(), 0);
		}
		if (!evs.getAutomaticAxesNumberingDistance(1)
				&& Double.isNaN(evs.getAxisNumberingDistanceY())) {
			view.setAutomaticAxesNumberingDistance(false, 1);
		} else {
			view.setAxesNumberingDistance(evs.getAxisNumberingDistanceY(), 1);
		}

		view.axesTickStyles[0] = evs.getAxesTickStyles()[0];
		view.axesTickStyles[1] = evs.getAxesTickStyles()[1];

		view.setDrawBorderAxes(evs.getDrawBorderAxes());

		view.axisCross[0] = evs.getAxesCross()[0];
		view.axisCross[1] = evs.getAxesCross()[1];
		view.positiveAxes[0] = evs.getPositiveAxes()[0];
		view.positiveAxes[1] = evs.getPositiveAxes()[1];

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
			view.setCoordSystem(evs.getXZero(), evs.getYZero(), evs.getXscale(),
					evs.getYscale(), true);
			evs.setXminObject(view.xminObject, false);
			evs.setXmaxObject(view.xmaxObject, false);
			evs.setYminObject(view.yminObject, false);
			evs.setYmaxObject(view.ymaxObject, false);
		} else {
			// xmin, ... are OK; just update bounds
			view.updateBounds(true);
		}

		// let's do this after other updates because this might override e.g.
		// xmin
		view.setLockedAxesRatio(evs.getLockedAxesRatio());
	}
	
	
	/**
	 * Paints content of this view.
	 * 
	 * @param g2
	 *            graphics
	 */
	public void paint(geogebra.common.awt.GGraphics2D g2) {
		// Graphics2D g2 = (Graphics2D) g;
		// lastGraphics2D = g2;

		view.setDefRenderingHints(g2);
		// g2.setClip(0, 0, width, height);

		view.paintTheBackground(g2);

		// FOREGROUND
		if (view.antiAliasing) {
			view.setAntialiasing(g2);
		}

		// draw equations, checkboxes and all geo objects
		view.drawObjects(g2);

		if (view.selectionRectangle != null) {
			view.drawZoomRectangle(g2);
		}

		if (view.deletionRectangle != null) {
			view.drawRect(g2, EuclidianView.colDeletionSquare, EuclidianView.strokeDeletionSquare,
					view.deletionRectangle);
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
	 * @param show true to show grid
	 */
	public void showGrid(boolean show) {
		if (show == view.showGrid) {
			return;
		}
		view.showGrid = show;
		view.updateBackgroundImage();
	}
	
	
	/**
	 * @param geo geo
	 * @return new drawable for given geo
	 */
	public DrawableND newDrawable(GeoElement geo) {
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
	public geogebra.common.awt.GAffineTransform getTransform(GeoConicND conic,
			Coords M, Coords[] ev) {
		return conic.getAffineTransform();
	}
	
	
	/**
	 * tranform point coords in view coords
	 * @param point point
	 * @return point coords in view coords
	 */
	public Coords getCoordsForView(GeoPointND point) {
		return point.getInhomCoords();
	}
}
