package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class for drawing 3D planes.
 * 
 * @author matthieu
 *
 */
public class DrawPlane3D extends Drawable3DSurfaces {

	/** gl index of the grid */
	private int gridIndex = -1;
	private int gridOutlineIndex = -1;

	protected double xmin, xmax, ymin, ymax;
	double[] minmaxXFinal = new double[2], minmaxYFinal = new double[2];

	/** says if the view direction is parallel to the plane */
	private boolean viewDirectionIsParallel;

	@Override
	public void setWaitForReset() {

		gridIndex = -1;
		gridOutlineIndex = -1;
		super.setWaitForReset();
	}

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 * @param a_plane3D
	 */
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D) {

		this(a_view3D, a_plane3D, null);

	}

	/**
	 * Constructor for helpers
	 * 
	 * @param a_view3D
	 * @param a_plane3D
	 * @param geo2
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

		if (getPlane().isPlateVisible())
			drawPlate(renderer);

	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		drawGeometry(renderer);
	}

	private void drawPlate(Renderer renderer) {
		renderer.setLayer(getLayer() - 1f); // -1f for z-fighting with planes
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);
	}

	@Override
	public void drawGeometryHiding(Renderer renderer) {
		GeoPlane3D plane = getPlane();
		if (plane.isPlateVisible()) {// || plane.isGridVisible())
			drawPlate(renderer);
			/*
			 * renderer.setLayer(getGeoElement().getLayer()-1f); //-1f for
			 * z-fighting with planes
			 * renderer.getGeometryManager().draw(getGeometryIndex());
			 * renderer.getGeometryManager().draw(hidingIndex);
			 * renderer.setLayer(0);
			 */
		}
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		if (!isVisible())
			return;

		if (!isGridVisible())
			return;

		if (viewDirectionIsParallel) {
			renderer.setDashTexture(Textures.DASH_LONG);
			renderer.getGeometryManager().draw(gridOutlineIndex);
		} else {
			renderer.setDashTexture(Textures.DASH_SHORT);
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
		return getPlane().isGridVisible()
				|| viewDirectionIsParallel;
	}

	@Override
	protected boolean updateForItSelf() {
		getPlane().setGridCorners(minmaxXFinal[0],
				minmaxYFinal[0], minmaxXFinal[1], minmaxYFinal[1]);
		return updateGeometry();
	}

	// private int hidingIndex = -1;

	/**
	 * update the geometry
	 * 
	 * @return true
	 */
	protected boolean updateGeometry() {

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = getPlane();
		CoordSys coordsys = geo.getCoordSys();

		float xmin1 = (float) geo.getXmin(), xmax1 = (float) geo.getXmax(), xdelta1 = xmax1
				- xmin1;
		float ymin1 = (float) geo.getYmin(), ymax1 = (float) geo.getYmax(), ydelta1 = ymax1
				- ymin1;

		// plane
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
		surface.draw();
		setSurfaceIndex(surface.end());

		// grid
		if (isGridVisible()) {

			PlotterBrush brush = renderer.getGeometryManager().getBrush();

			if (hasTrace()) {
				brush.start(-1);
			} else {
				brush.start(gridIndex);
			}
			removeGeometryIndex(gridIndex);
			float thickness = brush.setThickness(getGeoElement()
					.getLineThickness(), (float) getView3D().getScale());

			brush.setColor(getGeoElement().getObjectColor());

			double dx = geo.getGridXd();
			geo.getGridYd();
			double dy;
			if (Double.isNaN(dx)) {
				dx = getView3D().getNumbersDistance();
				dy = dx;
			} else {
				dy = geo.getGridYd();
			}

			brush.setAffineTexture((0f - xmin1) / ydelta1, 0.25f);
			int i0 = (int) (ymin1 / dy);
			if (ymin1 > 0)
				i0++;
			for (int i = i0; i <= ymax1 / dy; i++)
				brush.segment(coordsys.getPointForDrawing(xmin1, i * dy),
						coordsys.getPointForDrawing(xmax1, i * dy));
			// along y axis
			brush.setAffineTexture((0f - ymin1) / xdelta1, 0.25f);
			i0 = (int) (xmin1 / dx);
			if (xmin1 > 0)
				i0++;
			for (int i = i0; i <= xmax1 / dx; i++)
				brush.segment(coordsys.getPointForDrawing(i * dx, ymin1),
						coordsys.getPointForDrawing(i * dx, ymax1));

			gridIndex = brush.end();

			brush.start(gridOutlineIndex);
			removeGeometryIndex(gridOutlineIndex);

			boolean showClippingCube = getView3D().showClippingCube();

			// draws the rectangle outline
			if (showClippingCube) {
				brush.setAffineTexture((0f - xmin1) / ydelta1, 0.25f);
			} else
				brush.setPlainTexture();
			brush.segment(
					coordsys.getPointForDrawing(xmin1, ymax1 - thickness),
					coordsys.getPointForDrawing(xmax1, ymax1 - thickness));
			brush.segment(
					coordsys.getPointForDrawing(xmin1, ymin1 + thickness),
					coordsys.getPointForDrawing(xmax1, ymin1 + thickness));

			if (showClippingCube) {
				brush.setAffineTexture((0f - ymin1) / xdelta1, 0.25f);
			}
			brush.segment(
					coordsys.getPointForDrawing(xmin1 + thickness, ymin1),
					coordsys.getPointForDrawing(xmin1 + thickness, ymax1));
			brush.segment(
					coordsys.getPointForDrawing(xmax1 - thickness, ymin1),
					coordsys.getPointForDrawing(xmax1 - thickness, ymax1));

			gridOutlineIndex = brush.end();
		}

		return true;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChanged()) {

			if (!getView3D().viewChangedByTranslate()
					&& !getView3D().viewChangedByZoom()) { // only rotation
				boolean oldViewDirectionIsParallel = viewDirectionIsParallel;
				checkViewDirectionIsParallel(); // done in setWaitForUpdate()
												// too
				if (oldViewDirectionIsParallel != viewDirectionIsParallel) { // maybe
																				// have
																				// to
																				// update
																				// the
																				// outline
					setWaitForUpdate();
				}
				return;
			}

			setWaitForUpdate();
		}

	}

	@Override
	public void setWaitForUpdate() {

		super.setWaitForUpdate();
		setMinMax();
		checkViewDirectionIsParallel();
	}

	/**
	 * set x-y min/max values
	 */
	protected void setMinMax() {
		if (!getGeoElement().isDefined()) {
			return;
		}

		if (getView3D().useClippingCube()) { // make sure the plane goes more
												// than the clipping cube
			setMinMax(getView3D().getClippingVertex(0), getView3D()
					.getClippingVertex(1), getView3D().getClippingVertex(2),
					getView3D().getClippingVertex(4));
		} else { // use interior clipping cube radius
			setMinMax(getView3D().getCenter(), getView3D()
					.getFrustumInteriorRadius());
		}

	}
	
	/**
	 * 
	 * @return plane
	 */
	protected GeoPlane3D getPlane() {
		return (GeoPlane3D) getGeoElement();
	}

	private Coords o = Coords.createInhomCoorsInD3();
	private Coords tmpCoords1 = Coords.createInhomCoorsInD3(),
			tmpCoords2 = Coords.createInhomCoorsInD3();

	/**
	 * sets the min/max regarding a clipping box
	 * 
	 * @param origin
	 *            center of the clipping box
	 * @param vx
	 *            first edge
	 * @param vy
	 *            second edge
	 * @param vz
	 *            third edge
	 */
	private void setMinMax(Coords origin, Coords vx, Coords vy, Coords vz) {

		GeoPlane3D geo = getPlane();

		CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
		origin.projectPlaneInPlaneCoords(m, o);
		minmaxXFinal[0] = o.getX();
		minmaxYFinal[0] = o.getY();
		minmaxXFinal[1] = o.getX();
		minmaxYFinal[1] = o.getY();
		Coords[] v = new Coords[3];
		vx.projectPlaneInPlaneCoords(m, tmpCoords1);
		v[0] = tmpCoords1.sub(o);
		vy.projectPlaneInPlaneCoords(m, tmpCoords1);
		v[1] = tmpCoords1.sub(o);
		vz.projectPlaneInPlaneCoords(m, tmpCoords1);
		v[2] = tmpCoords1.sub(o);
		for (int i = 0; i < 3; i++) {
			double x = v[i].getX();
			if (x < 0)
				minmaxXFinal[0] += x; // sub from xmin
			else
				minmaxXFinal[1] += x; // add to xmax
			double y = v[i].getY();
			if (y < 0)
				minmaxYFinal[0] += y; // sub from ymin
			else
				minmaxYFinal[1] += y; // add to ymax

		}

	}

	private static final double INV_SQRT_2 = 1 / Math.sqrt(2);

	private void setMinMax(Coords origin, double radius) {
		GeoPlane3D geo = getPlane();

		CoordMatrix m = geo.getCoordSys().getDrawingMatrix();
		origin.projectPlaneInPlaneCoords(m, o);

		double a = radius * INV_SQRT_2;

		minmaxXFinal[0] = o.getX() - a;
		minmaxYFinal[0] = o.getY() - a;
		minmaxXFinal[1] = o.getX() + a;
		minmaxYFinal[1] = o.getY() + a;

	}

	private static final double REDUCE_BOUNDS_FACTOR = 0.975;

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

	private void checkViewDirectionIsParallel() {
		if (Kernel.isZero(getPlane().getCoordSys()
				.getEquationVector().dotproduct(getView3D().getEyePosition()))) {
			viewDirectionIsParallel = true;
		} else {
			viewDirectionIsParallel = false;
		}
	}

	@Override
	public void setWaitForUpdateVisualStyle() {
		super.setWaitForUpdateVisualStyle();

		// also update for plane clip
		setWaitForUpdate();
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (getGeoElement().getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		GeoPlane3D plane = getPlane();

		// project hitting origin on plane
		if (hitting.isSphere()) {
			hitting.origin.projectPlane(plane.getCoordSys().getDrawingMatrix(),
					tmpCoords1, tmpCoords2);
		} else {
			hitting.origin.projectPlaneThruVIfPossible(plane.getCoordSys()
					.getDrawingMatrix(), hitting.direction, tmpCoords1,
					tmpCoords2);
		}

		if (!hitting.isInsideClipping(tmpCoords1)) {
			return false;
		}

		double x = tmpCoords2.getX();
		if (x < plane.getXmin()) {
			return false;
		}
		if (x > plane.getXmax()) {
			return false;
		}

		double y = tmpCoords2.getY();
		if (y < plane.getYmin()) {
			return false;
		}
		if (y > plane.getYmax()) {
			return false;
		}

		if (hitting.isSphere()) {
			double d = tmpCoords1.distance(hitting.origin);
			double scale = getView3D().getScale();
			if (d * scale <= hitting.getThreshold()) {
				setZPick(-d, -d);
				return true;
			}

		} else {
			double parameterOnHitting = tmpCoords2.getZ();// TODO use other for
															// non-parallel
															// projection :
															// -hitting.origin.distance(project[0]);
			setZPick(parameterOnHitting, parameterOnHitting);
			return true;
		}

		return false;

	}

}
