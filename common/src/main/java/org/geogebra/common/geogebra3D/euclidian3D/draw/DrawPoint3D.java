package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

//TODO does not extend Drawable3DCurves

/**
 * Class for drawing 3D points.
 * 
 * @author matthieu
 * 
 *
 */
public class DrawPoint3D extends Drawable3DCurves implements Previewable,
		Functional2Var {

	/** factor for drawing points */
	public static final float DRAW_POINT_FACTOR = 1.5f;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 * @param point
	 */
	public DrawPoint3D(EuclidianView3D view3D, GeoPointND point) {

		super(view3D, (GeoElement) point);

	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex(), center);

	}

	@Override
	public void drawInObjFormat(Renderer renderer) {
		if (isVisible()) {
			renderer.getGeometryManager().drawInObjFormat(getGeoElement(),
					getGeometryIndex());
		}
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		drawGeometry(renderer);
	}

	@Override
	protected void setLineTextureHidden(Renderer renderer) {
		// nothing to do here
	}

	private Coords center = new Coords(4);

	/**
	 * 
	 * @return point center
	 */
	public Coords getCenter() {
		return center;
	}

	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	@Override
	protected boolean updateForItSelf() {

		GeoPointND point = (GeoPointND) getGeoElement();
		center.setValues(point.getInhomCoordsInD3(), 3);
		center.setW(point.getPointSize()); // put point size in fourth unused
											// coord
		setGeometryIndex(getView3D()
				.getRenderer()
				.getGeometryManager()
				.drawPoint(point.getPointSize(), center,
						getReusableGeometryIndex()));

		// bounds
		double radius = point.getPointSize() / getView3D().getScale()
				* DrawPoint3D.DRAW_POINT_FACTOR;
		boundsMin.setX(center.getX() - radius);
		boundsMin.setY(center.getY() - radius);
		boundsMin.setZ(center.getZ() - radius);
		boundsMax.setX(center.getX() + radius);
		boundsMax.setY(center.getY() + radius);
		boundsMax.setZ(center.getZ() + radius);

		return true;
	}

	@Override
	protected void doRemoveGeometryIndex(int index) {
		// for shaders: use Manager templates -- no remove for points
		if (!getView3D().getRenderer().useShaders()) {
			super.doRemoveGeometryIndex(index);
		}
	}

	@Override
	protected void updateForView() {

		if (getView3D().viewChangedByZoom()) {
			updateForItSelf();
		}

	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_POINT;
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * @param a_view3D
	 */
	public DrawPoint3D(EuclidianView3D a_view3D) {

		super(a_view3D);

		setGeoElement(a_view3D.getCursor3D());

	}

	@Override
	public void disposePreview() {
		// TODO Auto-generated method stub

	}

	public void updateMousePos(double xRW, double yRW) {

	}

	public void updatePreview() {

	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_POINTS);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_POINTS);
	}

	// /////////////////////////////////
	// FUNCTION2VAR INTERFACE
	// /////////////////////////////////

	public Coords evaluatePoint(double u, double v) {

		GeoPointND point = (GeoPointND) getGeoElement();

		double r = point.getPointSize() / getView3D().getScale() * 1.5;
		Coords n = new Coords(new double[] { Math.cos(u) * Math.cos(v) * r,
				Math.sin(u) * Math.cos(v) * r, Math.sin(v) * r });

		return (Coords) n.add(point.getInhomCoordsInD3());
	}

	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] { Math.cos(u) * Math.cos(v),
				Math.sin(u) * Math.cos(v), Math.sin(v) });
	}

	public double getMinParameter(int index) {
		switch (index) {
		case 0: // u
		default:
			return 0;
		case 1: // v
			return -Math.PI / 2;
		}
	}

	public double getMaxParameter(int index) {
		switch (index) {
		case 0: // u
		default:
			return 2 * Math.PI;
		case 1: // v
			return Math.PI / 2;
		}

	}

	@Override
	protected float getLabelOffsetX() {
		// consistent with DrawPoint
		return super.getLabelOffsetX() + 4;
	}

	@Override
	protected float getLabelOffsetY() {
		// consistent with DrawPoint
		return super.getLabelOffsetY() - 2
				* ((GeoPointND) getGeoElement()).getPointSize();
	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_POINTS;
	}

	@Override
	public boolean hit(Hitting hitting) {

		GeoPointND point = (GeoPointND) getGeoElement();
		Coords p = point.getInhomCoordsInD3();

		return DrawPoint3D.hit(hitting, p, this, point.getPointSize(), project,
				parameters, false);

	}

	@Override
	public boolean hitForList(Hitting hitting) {
		if (hasGeoElementVisible() && getGeoElement().isPickable()) {
			GeoPointND point = (GeoPointND) getGeoElement();
			Coords p = point.getInhomCoordsInD3();

			return DrawPoint3D.hit(hitting, p, this, point.getPointSize(),
					project, parameters, true);
		}

		return false;

	}

	/**
	 * 
	 * @param hitting
	 *            hitting
	 * @param p
	 *            point coords
	 * @param drawable
	 *            drawable calling
	 * @param pointSize
	 *            point size
	 * @param project
	 *            temp coords for projection
	 * @param parameters
	 *            temp values for paramters
	 * @param checkRealPointSize
	 *            true if we check point size (and not threshold)
	 * @return true if the hitting hits the point
	 */
	static public boolean hit(Hitting hitting, Coords p, Drawable3D drawable,
			int pointSize, Coords project, double[] parameters,
			boolean checkRealPointSize) {

		if (hitting.isSphere()) {
			double d = p.distance(hitting.origin);
			double scale = drawable.getView3D().getScale();
			if (d * scale <= pointSize + hitting.getThreshold()) {
				// double z = -parameters[0];
				// double dz = pointSize/scale;
				drawable.setZPick(-d, -d);
				return true;
			}
		} else {
			p.projectLine(hitting.origin, hitting.direction, project,
					parameters);

			if (!hitting.isInsideClipping(project)) {
				return false;
			}

			double d = p.distance(project);
			double scale = drawable.getView3D().getScale();
			boolean hitted;
			if (checkRealPointSize) {
				hitted = d * scale <= pointSize + 2;
			} else {
				hitted = d * scale <= DrawPoint.getSelectionThreshold(hitting
						.getThreshold());
			}
			if (hitted) {
				double z = -parameters[0];
				double dz = pointSize / scale;
				drawable.setZPick(z + dz, z - dz);
				return true;
			}

		}

		return false;
	}

	private Coords project = Coords.createInhomCoorsInD3();

	private double[] parameters = new double[2];

	@Override
	protected TraceIndex newTraceIndex() {
		return new TraceIndex(getGeometryIndex(), getSurfaceIndex(),
				center.copyVector());
	}

	@Override
	protected void drawGeom(Renderer renderer, TraceIndex index) {
		renderer.getGeometryManager().draw(index.geom, index.center);
	}

	@Override
	public void enlargeBounds(Coords min, Coords max) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}

}
