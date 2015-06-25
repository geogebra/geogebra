package org.geogebra.common.geogebra3D.euclidianForPlane;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidianFor3D.CurveEvaluableForPlane;
import org.geogebra.common.geogebra3D.euclidianFor3D.DrawAngleFor3D;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Companion for view for plane specific stuff
 * 
 * @author mathieu
 *
 */
public class EuclidianViewForPlaneCompanion extends EuclidianViewFor3DCompanion {

	private ViewCreator plane;

	/**
	 * constructor
	 * 
	 * @param view
	 *            view attached
	 */
	public EuclidianViewForPlaneCompanion(EuclidianView view) {
		super(view);
	}

	private CoordMatrix4x4 transform;

	public void initView(ViewCreator plane) {

		setPlane(plane);

		if (settingsFromLoadFile) {
			// view is created from file, only update matrices
			updateOtherMatrices();
		} else {
			// view is created from scratch
			updateMatrix();
			// set coord system to fit 3D view
			updateCenterAndOrientationRegardingView();
			updateScaleRegardingView();
		}

	}

	/**
	 * set the plane creator
	 * 
	 * @param plane
	 *            plane creator
	 */
	public void setPlane(ViewCreator plane) {
		this.plane = plane;
	}

	public ViewCreator getPlane() {
		return plane;
	}

	/**
	 * update orientation of the view regarding 3D view
	 */
	public void updateScaleRegardingView() {

		double newScale = view.getApplication().getEuclidianView3D()
				.getXscale();
		double w = view.getWidth() / 2;
		double h = view.getHeight() / 2;
		double dx = (w - view.getxZero()) * newScale / view.getXscale();
		double dy = (h - view.getyZero()) * newScale / view.getYscale();

		setCoordSystem(w - dx, h - dy, newScale, newScale);

	}

	private void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		view.setCoordSystem(xZero, yZero, xscale, yscale);
	}

	private Coords tmpCoords = new Coords(4);

	/**
	 * update center and orientation of the view regarding 3D view
	 */
	public void updateCenterAndOrientationRegardingView() {

		setTransformRegardingView();
		updateMatrix();

		EuclidianView3DInterface view3D = view.getApplication()
				.getEuclidianView3D();

		// coords of the bounding box center in the 3D view
		Coords c = new Coords(-view3D.getXZero(), -view3D.getYZero(),
				-view3D.getZZero(), 1);

		// project it in this view coord sys
		c.projectPlaneInPlaneCoords(getMatrix(), tmpCoords);

		// take this projection for center
		int x = view.toScreenCoordX(tmpCoords.getX());
		int y = view.toScreenCoordY(tmpCoords.getY());

		setCoordSystem(view.getWidth() / 2 - x + view.getxZero(),
				view.getHeight() / 2 - y + view.getyZero(), view.getXscale(),
				view.getYscale());

	}

	private CoordMatrix4x4 planeMatrix, transformedMatrix;
	private CoordMatrix inverseTransformedMatrix;

	@Override
	public CoordMatrix getMatrix() {

		return transformedMatrix;

	}

	@Override
	public CoordMatrix getInverseMatrix() {
		return inverseTransformedMatrix;
	}

	public void updateMatrix() {

		if (!plane.isDefined()) {
			// force plane matrix for Drawables creation
			planeMatrix = CoordMatrix4x4.IDENTITY;
			transformedMatrix = CoordMatrix4x4.IDENTITY;
			inverseTransformedMatrix = CoordMatrix4x4.IDENTITY;
			return;
		}

		if (transform == null) // transform has not already been set
			transform = CoordMatrix4x4.IDENTITY;

		updateOtherMatrices();
	}

	private final void updateOtherMatrices() {
		// // use continuity
		// Coords vx1 = Coords.UNDEFINED;
		// if (transformedMatrix!=null){
		// vx1 = transformedMatrix.getVx();
		// }
		planeMatrix = plane.getCoordSys().getDrawingMatrix();

		transformedMatrix = planeMatrix.mul(transform);// transform.mul(planeMatrix);

		// Coords vx2 = transformedMatrix.getVx();
		// App.debug("\nvx1=\n"+vx1+"\nvx2=\n"+vx2);
		// double dxx = vx1.dotproduct(vx2);
		// if (dxx < 0){
		// transformRotate += 180;
		// if (transformRotate > 180){
		// transformRotate -= 360;
		// }
		// setTransform();
		// transformedMatrix = planeMatrix.mul(transform);
		// }

		inverseTransformedMatrix = transformedMatrix.inverse();

	}

	private int transformMirror;
	private int transformRotate;

	public void setTransformRegardingView() {

		Coords directionView3D = ((EuclidianView3D) view.getApplication()
				.getEuclidianView3D()).getViewDirection();
		CoordMatrix toScreenMatrix = ((EuclidianView3D) view.getApplication()
				.getEuclidianView3D()).getToScreenMatrix();

		// front or back view
		double p = plane.getCoordSys().getNormal().dotproduct(directionView3D);
		if (p <= 0) {
			transform = CoordMatrix4x4.IDENTITY;
			transformMirror = 1;
		} else {
			transform = CoordMatrix4x4.MIRROR_Y;
			transformMirror = -1;
		}

		// Application.debug("transform=\n"+transform);

		// CoordMatrix m = toScreenMatrix.mul(planeMatrix.mul(transform));
		CoordMatrix m = toScreenMatrix.mul(planeMatrix);

		// App.debug("m=\n"+m);

		double vXx = m.get(1, 1);
		double vXy = m.get(2, 1);
		double vYx = m.get(1, 2);
		double vYy = m.get(2, 2);

		transformRotate = 0;
		// is vX vertical and vY horizontal ?
		if (Math.abs(vXy) > Math.abs(vXx) && Math.abs(vYx) > Math.abs(vYy)) {
			if (vYx * transformMirror >= 0) {
				transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);
				transformRotate = 90;
			} else {
				transform = CoordMatrix4x4.ROTATION_OZ_M90.mul(transform);
				transformRotate = -90;
			}
			// check vX direction
		} else if (vXx * transformMirror < 0) {
			transform = CoordMatrix4x4.MIRROR_O.mul(transform);
			transformRotate = 180;
		}

		updateMatrix();

		// TODO only if new matrix != old matrix
		view.updateAllDrawables(true);
	}

	/**
	 * set transform from values
	 */
	public void setTransform() {

		if (transformMirror == 1)
			transform = CoordMatrix4x4.IDENTITY;
		else
			transform = CoordMatrix4x4.MIRROR_Y;

		if (transformRotate == 90)
			transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);
		else if (transformRotate == -90)
			transform = CoordMatrix4x4.ROTATION_OZ_M90.mul(transform);
		else if (transformRotate == 180)
			transform = CoordMatrix4x4.MIRROR_O.mul(transform);

	}

	@Override
	public void getXML(StringBuilder sbxml, boolean asPreference) {

		if (!view.isShowing()) {
			// we don't want to store view for plane that is not showing
			App.debug("view is not showing");
			return;
		}

		view.startXML(sbxml, asPreference);

		// transform
		sbxml.append("\t<transformForPlane ");
		sbxml.append("mirror=\"");
		sbxml.append(transformMirror == -1);
		sbxml.append("\"");
		sbxml.append(" rotate=\"");
		sbxml.append(transformRotate);
		sbxml.append("\"");
		sbxml.append("/>\n");

		view.endXML(sbxml);

	}

	private boolean settingsFromLoadFile = false;

	@Override
	public void settingsChanged(AbstractSettings settings) {

		super.settingsChanged(settings);

		EuclidianSettingsForPlane evs = (EuclidianSettingsForPlane) settings;

		// transform
		transformMirror = 1;
		if (evs.getMirror())
			transformMirror = -1;

		transformRotate = evs.getRotate();

		setTransform();

		settingsFromLoadFile = evs.isFromLoadFile();
		evs.setFromLoadFile(false);

	}

	@Override
	public void setXYMinMaxForUpdateSize() {

		if (settingsFromLoadFile) {
			// prevent update when loading file
			if (view.getWidth() == 0 || view.getHeight() == 0) {
				super.setXYMinMaxForUpdateSize();
				return;
			}

			// prevent update when loading file
			if (view.xmax - view.xmin < Kernel.MAX_PRECISION
					|| view.ymax - view.ymin < Kernel.MAX_PRECISION) {
				super.setXYMinMaxForUpdateSize();
				return;
			}
		}

		// keep center of the view at center of the frame

		double c = (view.xmin + view.xmax) / 2;
		double l = view.getWidth() * view.getInvXscale() / 2;
		view.xmin = c - l;
		view.xmax = c + l;

		c = (view.ymin + view.ymax) / 2;
		l = view.getHeight() * view.getInvYscale() / 2;
		view.ymax = c + l;
		view.ymin = c - l;
	}

	@Override
	protected DrawAngle newDrawAngle(GeoAngle geo) {
		return new DrawAngleFor3D(view, geo);
	}

	@Override
	public boolean isDefault2D() {
		return false;
	}

	public void updateForPlane() {
		updateMatrix();
		view.updateAllDrawables(true);
	}


	@Override
	public boolean isVisibleInThisView(GeoElement geo) {

		// prevent not implemented type to be displayed (TODO remove)
		switch (geo.getGeoClassType()) {
		case POINT:
		case POINT3D:
		case SEGMENT:
		case SEGMENT3D:
		case LINE:
		case LINE3D:
		case RAY:
		case RAY3D:
		case VECTOR:
		case VECTOR3D:
		case POLYGON:
		case POLYGON3D:
		case CONIC:
		case CONIC3D:
		case CONICSECTION:
		case CONICPART:
		case ANGLE3D:
		case TEXT:
		case LOCUS:
		case CURVE_CARTESIAN3D:
			return geo.isVisibleInViewForPlane();
		case ANGLE:
			if (geo.isIndependent()) { // no slider in view for plane (for now)
				return false;
			}
			return geo.isVisibleInViewForPlane();
		default:
			return false;
		}

	}

	@Override
	public Coords getCoordsForView(Coords coords) {
		return coords.projectPlaneWithInverseMatrix(getInverseMatrix());
	}

	/**
	 * @param coords
	 *            in view plane
	 * @return coords in 3D world
	 */
	public Coords getCoordsFromView(Coords coords) {
		return getMatrix().mul(coords);
	}

	/**
	 * @param x
	 *            x coord in view plane
	 * @param y
	 *            y coord in view plane
	 * @return coords in 3D world
	 */
	public Coords getCoordsFromView(double x, double y) {
		return getCoordsFromView(new Coords(x, y, 0, 1));
	}

	@Override
	public String getFromPlaneString() {
		if (plane == null)
			return "";
		return plane.toGeoElement().getLabel(StringTemplate.defaultTemplate);
	}

	@Override
	public String getTranslatedFromPlaneString() {
		if (plane == null)
			return "";

		if (plane instanceof GeoPlaneND) {
			return view
					.getApplication()
					.getLocalization()
					.getPlain(
							"PlaneA",
							((GeoElement) plane)
									.getLabel(StringTemplate.defaultTemplate));
		}
		return view
				.getApplication()
				.getLocalization()
				.getPlain(
						"PlaneFromA",
						((GeoElement) plane)
								.getLabel(StringTemplate.defaultTemplate));
	}

	@Override
	public GeoPlaneND getPlaneContaining() {
		if (plane instanceof GeoPlaneND) {
			return (GeoPlaneND) plane;
		}
		return view.getKernel().getManager3D().Plane3D(plane);

	}

	@Override
	public GeoDirectionND getDirection() {
		return plane;
	}

	@Override
	public boolean goToZPlus(Coords v) {
		double dot = v.dotproduct(getDirection().getDirectionInD3());
		return (dot > 0) ^ (transformMirror == -1);
	}

	/**
	 * 
	 * @param clockwise
	 * @return clockwise (resp. not(clockwise)) if clockwise is displayed as it
	 *         in the view
	 */
	public boolean viewOrientationForClockwise(boolean clockwise) {
		if (transformMirror == 1)
			return clockwise;

		return !clockwise;
	}

	@Override
	public boolean isMoveable(GeoElement geo) {
		if (hasForParent(geo)) {
			return false;
		}
		return super.isMoveable(geo);
	}

	/**
	 * @param geo
	 *            geo
	 * @return true if the geo is parent of the view
	 */
	public boolean hasForParent(GeoElement geo) {
		return geo.isParentOf(plane);
	}

	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algoParent) {
		ArrayList<GeoPointND> list = algoParent.getFreeInputPoints();
		ArrayList<GeoPointND> ret = new ArrayList<GeoPointND>();
		for (GeoPointND p : list)
			if (!hasForParent((GeoElement) p))
				ret.add(p);
		return ret;
	}

	@Override
	public void getXMLid(StringBuilder sbxml) {

		sbxml.append("\t<viewId ");
		sbxml.append("plane=\"");
		sbxml.append(((GeoElement) plane).getLabelSimple());
		sbxml.append("\"");
		sbxml.append("/>\n");

	}

	@Override
	public void paint(org.geogebra.common.awt.GGraphics2D g2) {
		if (!plane.isDefined()) {
			// draws the view in gray
			g2.setColor(GColor.LIGHT_GRAY);
			g2.fillRect(0, 0, view.getWidth(), view.getHeight());
			return;
		}

		super.paint(g2);
	}

	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos() {
		view.getKernel().notifyAddAll(view);
	}

	@Override
	public void attachView() {
		view.getKernel().attach(view);
	}

	@Override
	public boolean showGrid(boolean show) {
		EuclidianSettings settings = view.getApplication().getSettings()
				.getEuclidianForPlane(getFromPlaneString());
		if (settings != null)
			settings.setShowGridSetting(show);
		return super.showGrid(show);
	}

	private DockPanel panel;

	/**
	 * set the dock panel of the view
	 * 
	 * @param panel
	 *            dock panel containing
	 */
	public void setDockPanel(DockPanel panel) {
		this.panel = panel;
		this.id = panel.getViewId();
	}

	private int id;

	public int getId() {
		return id;
	}

	/**
	 * remove the view when the creator doens't exist anymore
	 */
	public void doRemove() {
		removeFromGuiAndKernel();
		((App3DCompanion) view.getApplication().getCompanion())
				.removeEuclidianViewForPlaneFromList(this);

	}

	/**
	 * remove panel from gui and view from kernel
	 */
	public void removeFromGuiAndKernel() {
		panel.closePanel();
		view.getApplication().getGuiManager().getLayout().getDockManager()
				.unRegisterPanel(panel);
		view.getKernel().detach(view);
	}

	/**
	 * update all drawables
	 * 
	 * @param repaint
	 *            says if repaint is needed
	 */
	public void updateAllDrawables(boolean repaint) {
		view.updateAllDrawables(repaint);

	}


	@Override
	protected DrawableND newDrawParametricCurve(GeoCurveCartesian3D geo) {
		return new DrawParametricCurve(view, new CurveEvaluableForPlane(geo,
				this));
	}

}
