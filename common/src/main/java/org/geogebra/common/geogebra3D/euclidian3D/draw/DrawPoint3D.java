package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Geometry3DGetterManager;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Geometry3DGetter.GeometryType;

//TODO does not extend Drawable3DCurves

/**
 * Class for drawing 3D points.
 * 
 * @author matthieu
 * 
 *
 */
public class DrawPoint3D extends Drawable3DCurves
		implements Previewable, Functional2Var {

	/** factor for drawing points */
	public static final float DRAW_POINT_FACTOR = 1.5f;

	private Coords center = new Coords(4);
	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);
	private Coords project = Coords.createInhomCoorsInD3();

	private double[] parameters = new double[2];

	/**
	 * common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param point
	 *            point
	 */
	public DrawPoint3D(EuclidianView3D view3D, GeoPointND point) {

		super(view3D, (GeoElement) point);

	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			exportToPrinter3D.exportCurve(this, Type.POINT);
		}
	}

	@Override
	public void export(Geometry3DGetterManager manager, boolean exportSurface) {
		if (isVisible()) {
			GeoElement geo = getGeoElement();
			manager.export(geo, getGeometryIndex(), geo.getObjectColor(), 1,
					GeometryType.CURVE);
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

	@Override
	protected boolean updateForItSelf() {

		GeoPointND point = (GeoPointND) getGeoElement();
		float size = getView3D().getSizeForPoint(point.getPointSize());
		Coords c = point.getInhomCoordsInD3();
		center.setValues(c, 3);

		// warning: plotter will scale center coords
		setGeometryIndex(getView3D().getRenderer().getGeometryManager()
				.drawPoint(this, size, center, getReusableGeometryIndex()));

		// bounds
		double radius = size / getView3D().getScale()
				* DrawPoint3D.DRAW_POINT_FACTOR;
		boundsMin.setX(c.getX() - radius);
		boundsMin.setY(c.getY() - radius);
		boundsMin.setZ(c.getZ() - radius);
		boundsMax.setX(c.getX() + radius);
		boundsMax.setY(c.getY() + radius);
		boundsMax.setZ(c.getZ() + radius);

		return true;
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
	 * Preview constructor
	 * 
	 * @param a_view3D
	 *            view
	 */
	public DrawPoint3D(EuclidianView3D a_view3D) {

		super(a_view3D);

		setGeoElement(a_view3D.getCursor3D());

	}

	@Override
	public void updateMousePos(double xRW, double yRW) {
		// not needed in 3D
	}

	@Override
	public void updatePreview() {
		// not needed
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

	@Override
	public void evaluatePoint(double u, double v, Coords point) {
		GeoPointND geoPoint = (GeoPointND) getGeoElement();
		double r = geoPoint.getPointSize() / getView3D().getScale() * 1.5;
		point.set(Math.cos(u) * Math.cos(v) * r, Math.sin(u) * Math.cos(v) * r,
				Math.sin(v) * r, 1);
		point.setAdd3(point, geoPoint.getInhomCoordsInD3());
	}

	@Override
	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] { Math.cos(u) * Math.cos(v),
				Math.sin(u) * Math.cos(v), Math.sin(v) });
	}

	@Override
	public double getMinParameter(int index) {
		switch (index) {
		case 0: // u
		default:
			return 0;
		case 1: // v
			return -Math.PI / 2;
		}
	}

	@Override
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
		return super.getLabelOffsetY()
				- 2 * ((GeoPointND) getGeoElement()).getPointSize();
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

		p.projectLine(hitting.origin, hitting.direction, project, parameters);

		if (!hitting.isInsideClipping(project)) {
			return false;
		}

		double d = drawable.getView3D().getScaledDistance(p, project);
		boolean hitOk;
		if (checkRealPointSize) {
			hitOk = d <= pointSize + 2;
		} else {
			hitOk = d <= DrawPoint
					.getSelectionThreshold(hitting.getThreshold());
		}
		if (hitOk) {
			double z = -parameters[0];
			double dz = pointSize / drawable.getView3D().getScale();
			drawable.setZPick(z + dz, z - dz, hitting.discardPositiveHits(),
					parameters[0]);
			return true;
		}

		return false;
	}

	@Override
	protected TraceIndex newTraceIndex() {
		return new TraceIndex(getGeometryIndex(), getSurfaceIndex());
	}

	@Override
	protected void drawGeom(Renderer renderer, TraceIndex index) {
		renderer.getGeometryManager().draw(index.geom);
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
	    if (dontExtend) {
            Coords c = ((GeoPointND) getGeoElement()).getInhomCoordsInD3();
            enlargeBounds(min, max, c, c);
        } else {
            enlargeBounds(min, max, boundsMin, boundsMax);
        }
	}

	@Override
	protected void drawTracesOutline(Renderer renderer, boolean hidden) {

		if (!hidden) {
			return;
		}

		if (trace == null) {
			return;
		}

		for (Entry<TraceSettings, ArrayList<TraceIndex>> settings : trace
				.entrySet()) {
			ArrayList<TraceIndex> indices = settings.getValue();
			setDrawingColor(settings.getKey().getColor());
			// Log.debug(indices.size());
			for (TraceIndex index : indices) {
				drawGeom(renderer, index);
			}
		}

	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(false);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityNoSurface(visible);
	}

}
