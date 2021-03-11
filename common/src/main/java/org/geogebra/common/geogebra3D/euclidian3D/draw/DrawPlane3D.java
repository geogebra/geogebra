package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class for drawing 3D planes.
 * 
 * @author mathieu
 *
 */
public class DrawPlane3D extends Drawable3DSurfaces {
	private static final double INV_SQRT_2 = 1 / Math.sqrt(2);

	/** gl index of the grid */
	private int gridIndex = -1;
	private int gridOutlineIndex = -1;

	private final double[] minmaxXFinal = new double[2];
	private final double[] minmaxYFinal = new double[2];

	/** says if the view direction is parallel to the plane */
	protected boolean viewDirectionIsParallel;
	private final Coords boundsMin = new Coords(3);
	private final Coords boundsMax = new Coords(3);

	private final Coords tmpCoords1 = Coords.createInhomCoorsInD3();
	private final Coords tmpCoords2 = Coords.createInhomCoorsInD3();
	private Coords vn = new Coords(3);

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param a_plane3D
	 *            plane
	 */
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D) {
		this(a_view3D, a_plane3D, null);
	}

	@Override
	public void setWaitForReset() {
		gridIndex = -1;
		gridOutlineIndex = -1;
		super.setWaitForReset();
	}

	/**
	 * Constructor for helpers
	 * 
	 * @param a_view3D
	 *            view
	 * @param a_plane3D
	 *            plane
	 * @param geo2
	 *            parent geo
	 */
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D,
			GeoElement geo2) {

		super(a_view3D);
		init(a_plane3D, geo2);
		setMinMax();

	}

	/**
	 * @param a_plane3D
	 *            plane
	 * @param geo2
	 *            geo caller
	 */
	protected void init(GeoElement a_plane3D, GeoElement geo2) {
		super.init(a_plane3D);
	}

	@Override
	public void drawGeometry(Renderer renderer) {

		drawPlate(renderer);

	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		drawGeometry(renderer);
	}

	/**
	 * draw the plate if visible
	 * 
	 * @param renderer
	 *            GL renderer
	 */
	protected void drawPlate(Renderer renderer) {
		if (getPlane().isPlateVisible()) {
			renderer.getRendererImpl().setLayer(getLayer());
			renderer.getGeometryManager().draw(getSurfaceIndex());
			renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
		}
	}

	@Override
	public int getLayer() {
		// -1 shift for z-fighting with planes
		return super.getLayer() + Renderer.LAYER_PLANE_SHIFT;
	}

	@Override
	public void drawGeometryHiding(Renderer renderer) {
		drawPlate(renderer);
	}

	@Override
	public void drawOutline(Renderer renderer) {

		if (!isGridVisible()) {
			return;
		}

		if (!viewDirectionIsParallel) {
			renderer.getTextures()
					.setDashFromLineType(getGeoElement().getLineType());
			renderer.getGeometryManager().draw(gridIndex);
		}
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		if (!isVisible()) {
			return;
		}

		if (!isGridVisible()) {
			return;
		}

		if (viewDirectionIsParallel) {
			renderer.getRendererImpl().setDashTexture(Textures.DASH_LONG);
			renderer.getGeometryManager().draw(gridOutlineIndex);
		} else {
			setLineTextureHidden(renderer);
			renderer.getGeometryManager().draw(gridIndex);
		}

	}

	/*
	 * @Override protected void drawGeometryForPicking(Renderer renderer){
	 * drawGeometry(renderer); renderer.getGeometryManager().draw(gridIndex);
	 * renderer.getGeometryManager().draw(gridOutlineIndex); }
	 */

	/**
	 * 
	 * @return true if grid is visible
	 */
	protected boolean isGridVisible() {
		return getPlane().isGridVisible() || viewDirectionIsParallel;
	}

	@Override
	protected boolean updateForItSelf() {
		getPlane().setGridCorners(minmaxXFinal[0], minmaxYFinal[0],
				minmaxXFinal[1], minmaxYFinal[1]);
		if (isGridVisible()) {
			updateGridDistances();
		}
		return updateGeometry();
	}

	private void updateGridDistances() {
		CoordMatrix drawingMatrix = getPlane().getCoordSys().getDrawingMatrix();
		// getPlane().setGridDistances(getView3D().getGridDistances(0),
		// getView3D()
		// .getGridDistances(1));
		getPlane().setGridDistances(
				getView3D().getGridDistances(
						getMaxLengthIndex(drawingMatrix.getVx())),
				getView3D().getGridDistances(
						getMaxLengthIndex(drawingMatrix.getVy())));
	}

	final private static int getMaxLengthIndex(Coords v) {
		int ret = 0;
		double max = Math.abs(v.getX());
		double l = Math.abs(v.getY());
		if (l > max) {
			max = l;
			ret = 1;
		}
		l = Math.abs(v.getZ());
		if (l > max) {
			ret = 2;
		}
		return ret;
	}

	/**
	 * 
	 * @return grid thickness
	 */
	protected int getGridThickness() {
		return getGeoElement().getLineThickness();
	}

	/**
	 * update the geometry
	 * 
	 * @return true
	 */
	protected boolean updateGeometry() {

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = getPlane();
		CoordSys coordsys = geo.getCoordSys();

		float xmin1 = (float) geo.getXmin(), xmax1 = (float) geo.getXmax(),
				xdelta1 = xmax1 - xmin1;
		float ymin1 = (float) geo.getYmin(), ymax1 = (float) geo.getYmax(),
				ydelta1 = ymax1 - ymin1;

		// update bounds
		updateBounds(xmin1, xmax1, ymin1, ymax1);

		// plane
		setPackSurface();
		PlotterSurface surface = renderer.getGeometryManager().getSurface();

		surface.start(geo, getReusableSurfaceIndex());

		surface.setU(xmin1, xmax1);
		surface.setNbU(2);
		surface.setV(ymin1, ymax1);
		surface.setNbV(2);

		if (!getView3D().useClippingCube()) {
			float fading;
			fading = xdelta1 * geo.getFading();
			surface.setUFading(fading, fading);
			fading = ydelta1 * geo.getFading();
			surface.setVFading(fading, fading);
		}
		surface.draw(shouldBePackedForManager());
		setSurfaceIndex(surface.end());
		endPacking();

		// grid
		if (shouldBePackedForManager()) {
			setPackCurve();
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			brush.start(getReusableGeometryIndex());

			if (viewDirectionIsParallel) {
				int t = getGridThickness();
				float scale = (float) getView3D().getScale();
				float thickness = brush.setThickness(t == 0 ? 1 : t,
						(float) getView3D().getScale());
				thickness = thickness / scale;

				// draws the rectangle outline
				brush.setPlainTexture();
				coordsys.getPointForDrawing(xmin1, ymax1 - thickness,
						tmpCoords1);
				coordsys.getPointForDrawing(xmax1, ymax1 - thickness,
						tmpCoords2);
				brush.segment(tmpCoords1, tmpCoords2);
				coordsys.getPointForDrawing(xmin1, ymin1 + thickness,
						tmpCoords1);
				coordsys.getPointForDrawing(xmax1, ymin1 + thickness,
						tmpCoords2);
				brush.segment(tmpCoords1, tmpCoords2);
				coordsys.getPointForDrawing(xmin1 + thickness, ymin1,
						tmpCoords1);
				coordsys.getPointForDrawing(xmin1 + thickness, ymax1,
						tmpCoords2);
				brush.segment(tmpCoords1, tmpCoords2);
				coordsys.getPointForDrawing(xmax1 - thickness, ymin1,
						tmpCoords1);
				coordsys.getPointForDrawing(xmax1 - thickness, ymax1,
						tmpCoords2);
				brush.segment(tmpCoords1, tmpCoords2);
			} else {
				brush.setThickness(getGridThickness(),
						(float) getView3D().getScale());
				double dx = geo.getGridXd();
				double dy;
				if (Double.isNaN(dx)) {
					dx = getView3D().getNumbersDistance();
					dy = dx;
				} else {
					dy = geo.getGridYd();
				}

				brush.setAffineTexture((0f - xmin1) / ydelta1, 0.25f);
				int i0 = (int) (ymin1 / dy);
				if (ymin1 > 0) {
					i0++;
				}
				for (int i = i0; i <= ymax1 / dy; i++) {
					coordsys.getPointForDrawing(xmin1, i * dy, tmpCoords1);
					coordsys.getPointForDrawing(xmax1, i * dy, tmpCoords2);
					brush.segment(tmpCoords1, tmpCoords2);
				}
				// along y axis
				brush.setAffineTexture((0f - ymin1) / xdelta1, 0.25f);
				i0 = (int) (xmin1 / dx);
				if (xmin1 > 0) {
					i0++;
				}
				for (int i = i0; i <= xmax1 / dx; i++) {
					coordsys.getPointForDrawing(i * dx, ymin1, tmpCoords1);
					coordsys.getPointForDrawing(i * dx, ymax1, tmpCoords2);
					brush.segment(tmpCoords1, tmpCoords2);
				}
			}

			setGeometryIndex(brush.end());
			endPacking();

		} else if (isGridVisible()) {

			PlotterBrush brush = renderer.getGeometryManager().getBrush();

			if (hasTrace()) {
				brush.start(-1);
			} else {
				brush.start(gridIndex);
			}
			removeGeometryIndex(gridIndex);
			final float thickness = brush.setThickness(getGridThickness(),
					(float) getView3D().getScale());

			brush.setColor(getGeoElement().getObjectColor());

			double dx = geo.getGridXd();
			double dy;
			if (Double.isNaN(dx)) {
				dx = getView3D().getNumbersDistance();
				dy = dx;
			} else {
				dy = geo.getGridYd();
			}

			brush.setAffineTexture((0f - xmin1) / ydelta1, 0.25f);
			int i0 = (int) (ymin1 / dy);
			if (ymin1 > 0) {
				i0++;
			}
			Coords start = new Coords(4);
			Coords end = new Coords(4);
			for (int i = i0; i <= ymax1 / dy; i++) {
				brush.segment(coordsys.getPointForDrawing(xmin1, i * dy, start),
						coordsys.getPointForDrawing(xmax1, i * dy, end));
			}
			// along y axis
			brush.setAffineTexture((0f - ymin1) / xdelta1, 0.25f);
			i0 = (int) (xmin1 / dx);
			if (xmin1 > 0) {
				i0++;
			}
			for (int i = i0; i <= xmax1 / dx; i++) {
				brush.segment(coordsys.getPointForDrawing(i * dx, ymin1, start),
						coordsys.getPointForDrawing(i * dx, ymax1, end));
			}

			gridIndex = brush.end();

			brush.start(gridOutlineIndex);
			removeGeometryIndex(gridOutlineIndex);

			boolean showClippingCube = getView3D().showClippingCube();

			// draws the rectangle outline
			if (showClippingCube) {
				brush.setAffineTexture((0f - xmin1) / ydelta1, 0.25f);
			} else {
				brush.setPlainTexture();
			}
			brush.segment(
					coordsys.getPointForDrawing(xmin1, ymax1 - thickness,
							start),
					coordsys.getPointForDrawing(xmax1, ymax1 - thickness, end));
			brush.segment(
					coordsys.getPointForDrawing(xmin1, ymin1 + thickness,
							start),
					coordsys.getPointForDrawing(xmax1, ymin1 + thickness, end));

			if (showClippingCube) {
				brush.setAffineTexture((0f - ymin1) / xdelta1, 0.25f);
			}
			brush.segment(
					coordsys.getPointForDrawing(xmin1 + thickness, ymin1,
							start),
					coordsys.getPointForDrawing(xmin1 + thickness, ymax1, end));
			brush.segment(
					coordsys.getPointForDrawing(xmax1 - thickness, ymin1,
							start),
					coordsys.getPointForDrawing(xmax1 - thickness, ymax1, end));

			gridOutlineIndex = brush.end();
		}

		return true;
	}

	/**
	 * Update x, y, z range of bounds given the x-range and y-range of the view
	 * 
	 * @param xmin
	 *            x-min
	 * @param xmax
	 *            x-max
	 * @param ymin
	 *            y-min
	 * @param ymax
	 *            y-max
	 */
	protected void updateBounds(double xmin, double xmax, double ymin,
			double ymax) {

		GeoPlane3D geo = getPlane();
		CoordSys coordsys = geo.getCoordSys();

		// update z min/max
		boundsMin.setZ(Double.POSITIVE_INFINITY);
		boundsMax.setZ(Double.NEGATIVE_INFINITY);

		updateZMinMax(coordsys, xmin, ymin);
		updateZMinMax(coordsys, xmin, ymax);
		updateZMinMax(coordsys, xmax, ymax);
		updateZMinMax(coordsys, xmax, ymin);

		// update x min/max
		boundsMin.setX(xmin);
		boundsMax.setX(xmax);

		// update y min/max
		boundsMin.setY(ymin);
		boundsMax.setY(ymax);
	}

	private void updateZMinMax(CoordSys coordsys, double x, double y) {
		coordsys.getPointForDrawing(x, y, tmpCoords1);
		double z = tmpCoords1.getZ();
		if (z < boundsMin.getZ()) {
			boundsMin.setZ(z);
		}
		if (z > boundsMax.getZ()) {
			boundsMax.setZ(z);
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		if (!Double.isNaN(boundsMin.getX())) {
            if (dontExtend) {
                reduceBounds(boundsMin, boundsMax);
            }
			enlargeBounds(min, max, boundsMin, boundsMax);
		}
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChanged()) {
			if (!getView3D().viewChangedByTranslate()
					&& !getView3D().viewChangedByZoom()) { // only rotation
				boolean oldViewDirectionIsParallel = viewDirectionIsParallel;
				checkViewDirectionIsParallel();
				if (oldViewDirectionIsParallel != viewDirectionIsParallel) {
					// maybe have to update the outline
					setWaitForUpdate(false);
				}
				return;
			}

			setWaitForUpdate();
		}

	}

	@Override
	public void setWaitForUpdate() {
		setWaitForUpdate(true);
	}

	private void setWaitForUpdate(boolean checkViewDirection) {
		super.setWaitForUpdate();
		setMinMax();
		if (checkViewDirection) {
			checkViewDirectionIsParallel();
		}
	}

	/**
	 * set x-y min/max values
	 */
	protected void setMinMax() {
		if (!getGeoElement().isDefined()) {
			return;
		}

		if (getView3D().isXREnabled()) {
			setMinMax(getView3D().getClippingCubeDrawable().getVerticesLarge());
		} else if (getView3D().useClippingCube()
				|| !getView3D().getSettings().hasSameScales()) {
			// make sure the plane goes more than the clipping cube
			setMinMax(getView3D().getClippingCubeDrawable().getVertices());
		} else { // use interior clipping cube radius
			setMinMax(getView3D().getCenter(),
					getView3D().getFrustumInteriorRadius());
		}

	}

	/**
	 * 
	 * @return plane
	 */
	protected GeoPlane3D getPlane() {
		return (GeoPlane3D) getGeoElement();
	}

	private void setMinMax(Coords[] v) {
		GeoPlane3D geo = getPlane();

		CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
		Coords vz = m.getVz();
		if (!getView3D().scaleAndNormalizeNormalXYZ(vz, vn)) {
			vn = vz;
		}

		tmpCoords2.set(v[0]);
		tmpCoords2.projectPlaneThruVInPlaneCoords(m, vn, tmpCoords1);

		minmaxXFinal[0] = tmpCoords1.getX();
		minmaxYFinal[0] = tmpCoords1.getY();
		minmaxXFinal[1] = tmpCoords1.getX();
		minmaxYFinal[1] = tmpCoords1.getY();

		for (int i = 1; i < v.length; i++) {
			enlargeMinMax(v[i], m, vn);
		}
	}

	private void enlargeMinMax(Coords v, CoordMatrix m, Coords vnCoords) {
		tmpCoords2.set(v);
		tmpCoords2.projectPlaneThruVInPlaneCoords(m, vnCoords, tmpCoords1);
		double x = tmpCoords1.getX();
		if (x < minmaxXFinal[0]) {
			minmaxXFinal[0] = x;
		} else if (x > minmaxXFinal[1]) {
			minmaxXFinal[1] = x;
		}
		double y = tmpCoords1.getY();
		if (y < minmaxYFinal[0]) {
			minmaxYFinal[0] = y;
		} else if (y > minmaxYFinal[1]) {
			minmaxYFinal[1] = y;
		}
	}

	private void setMinMax(Coords origin, double radius) {
		GeoPlane3D geo = getPlane();

		CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
		origin.projectPlaneInPlaneCoords(m, tmpCoords1);

		double a = radius * INV_SQRT_2;

		minmaxXFinal[0] = tmpCoords1.getX() - a;
		minmaxYFinal[0] = tmpCoords1.getY() - a;
		minmaxXFinal[1] = tmpCoords1.getX() + a;
		minmaxYFinal[1] = tmpCoords1.getY() + a;
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CURVES);
		super.addToDrawable3DLists(lists);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CURVES);
		super.removeFromDrawable3DLists(lists);
	}

	/**
	 * Update the {@link #viewDirectionIsParallel} flag
	 */
	protected void checkViewDirectionIsParallel() {
		viewDirectionIsParallel = getView3D().showPlaneOutlineIfNeeded()
				&& DoubleUtil
						.isZero(getPlane().getCoordSys().getEquationVector()
								.dotproduct(getView3D().getEyePosition()));
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {
        super.setWaitForUpdateVisualStyle(prop);
        if (prop == GProperty.COLOR || prop == GProperty.HIGHLIGHT) {
            setWaitForUpdateColor();
        } else if (prop == GProperty.VISIBLE) {
            setWaitForUpdateVisibility();
        } else {
            // also update for plane clip
            setWaitForUpdate();
        }
    }

	@Override
	public boolean hit(Hitting hitting) {
		return hit(hitting, tmpCoords1, tmpCoords2);
	}

	/**
	 * @param hitting
	 *            hitting
	 * @param globalCoords
	 *            set to global coords of hit
	 * @param inPlaneCoords
	 *            set to inplane coords of hit
	 * @return whether plane was hit
	 */
	public boolean hit(Hitting hitting, Coords globalCoords, Coords inPlaneCoords) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (getGeoElement()
				.getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		GeoPlane3D plane = getPlane();

		// project hitting origin on plane
		hitting.origin.projectPlaneThruVIfPossible(
				plane.getCoordSys().getDrawingMatrix(), hitting.direction,
				globalCoords, inPlaneCoords);

		if (!hitting.isInsideClipping(globalCoords)) {
			return false;
		}

		double x = inPlaneCoords.getX();
		if (x < plane.getXmin()) {
			return false;
		}
		if (x > plane.getXmax()) {
			return false;
		}

		double y = inPlaneCoords.getY();
		if (y < plane.getYmin()) {
			return false;
		}
		if (y > plane.getYmax()) {
			return false;
		}

		// TODO use other for non-parallel projection :
		// -hitting.origin.distance(project[0]);
		double parameterOnHitting = inPlaneCoords.getZ();
		setZPick(parameterOnHitting, parameterOnHitting,
				hitting.discardPositiveHits(), -parameterOnHitting);
		return true;

	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D,
			boolean exportSurface) {
		if (isVisible()) {
			if (exportSurface) {
				exportToPrinter3D.exportSurface(this, true, false);
			} else {
				if (isGridVisible()) {
					exportToPrinter3D.exportCurve(this, Type.CURVE);
				}
			}
		}
	}

}
