package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.Feature;

/**
 * Class for drawing 3D constant planes.
 * 
 * @author matthieu
 *
 */
public class DrawClippingCube3D extends Drawable3DCurves {

	private double[][] minMax, minMaxLarge;

	private Coords[] vertices;

	private Coords center;

	static private double REDUCTION_LARGE = 0; // (1-1./1)/2

	static private double REDUCTION_ENLARGE = 1.5;

	static private double[] REDUCTION_VALUES = { (1 - 1. / Math.sqrt(3)) / 2, // small
			(1 - 1. / Math.sqrt(2)) / 2, // medium
			REDUCTION_LARGE // large
	};

	static private double[] INTERIOR_RADIUS_FACTOR = { 1, Math.sqrt(2),
			Math.sqrt(3) };

	static private int REDUCTION_LENGTH = 3;

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param clippingCube
	 *            geo
	 */
	public DrawClippingCube3D(EuclidianView3D a_view3D,
			GeoClippingCube3D clippingCube) {

		super(a_view3D, clippingCube);

		center = new Coords(4);
		center.setW(1);

		minMax = new double[3][];
		minMaxLarge = new double[3][];

		for (int i = 0; i < 3; i++) {
			minMax[i] = new double[2];
			minMaxLarge[i] = new double[2];
		}

		vertices = new Coords[8];
		for (int i = 0; i < 8; i++)
			vertices[i] = new Coords(0, 0, 0, 1);
	}

	/*
	 * public double xmin(){ return minMax[0][0]; } public double ymin(){ return
	 * minMax[1][0]; } public double zmin(){ return minMax[2][0]; } public
	 * double xmax(){ return minMax[0][1]; } public double ymax(){ return
	 * minMax[1][1]; } public double zmax(){ return minMax[2][1]; }
	 */

	private double horizontalDiagonal;

	/**
	 * max value from center to one FRUSTUM edge
	 */
	private double frustumRadius;

	/**
	 * min value from center to one FRUSTUM face
	 */
	private double frustumInteriorRadius;

	/**
	 * 
	 * @return big diagonal
	 */
	public double getHorizontalDiagonal() {
		return horizontalDiagonal;
	}

	/**
	 * @return max value from center to one FRUSTUM edge
	 */
	public double getFrustumRadius() {
		return frustumRadius;
	}

	/**
	 * @return min value from center to one FRUSTUM face
	 */
	public double getFrustumInteriorRadius() {
		return frustumInteriorRadius;
	}

	/**
	 * update the x,y,z min/max values
	 * 
	 * @return the min/max values
	 */
	public double[][] updateMinMax() {

		EuclidianView3D view = getView3D();

		Renderer renderer = view.getRenderer();

		double xscale = view.getXscale();
		double yscale = view.getYscale();
		double zscale = view.getZscale();

		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		double x0 = origin.getX(), y0 = origin.getY(), z0 = origin.getZ();


		double ymin, ymax, zmin, zmax;
		double halfWidth = renderer.getWidth() / 2;

		double xmin = -halfWidth / xscale + x0;
		double xmax = halfWidth / xscale + x0;

		if (getView3D().getYAxisVertical()) {
			zmin = (renderer.getBottom()) / yscale + y0;
			zmax = (renderer.getTop()) / yscale + y0;
			ymin = -halfWidth / zscale + z0;
			ymax = halfWidth / zscale + z0;
		} else {
			ymin = (renderer.getBottom()) / zscale + z0;
			ymax = (renderer.getTop()) / zscale + z0;
			zmin = -halfWidth / yscale + y0;
			zmax = halfWidth / yscale + y0;
		}

		int reductionIndex = ((GeoClippingCube3D) getGeoElement())
				.getReduction();
		double rv = REDUCTION_VALUES[reductionIndex];
		double xr = (xmax - xmin) * rv;
		double yr = (ymax - ymin) * rv;
		double zr = (zmax - zmin) * rv;

		minMax[0][0] = xmin + xr;
		minMax[0][1] = xmax - xr;
		minMax[2][0] = ymin + yr; // z values : when 0 orientation, z is up on
									// screen
		minMax[2][1] = ymax - yr;
		minMax[1][0] = zmin + zr;
		minMax[1][1] = zmax - zr;

		setVertices();

		horizontalDiagonal = renderer.getWidth() * (1 - 2 * rv) * Math.sqrt(2);

		double scaleMax = Math.max(Math.max(xscale, yscale), zscale);
		double scaleMin = Math.min(Math.min(xscale, yscale), zscale);
		int w = renderer.getWidth();
		int h = renderer.getHeight();
		int d = renderer.getVisibleDepth();
		frustumRadius = Math.sqrt(w * w + h * h + d * d) / (2 * scaleMin);

		frustumInteriorRadius = Math.min(w, Math.min(h, d)) / (2 * scaleMax);
		frustumInteriorRadius *= INTERIOR_RADIUS_FACTOR[reductionIndex];

		// double h = minMax[2][1]-minMax[2][0]; frustumRadius = h/2;

		view.setXYMinMax(minMax);

		// minMaxLarge to cut lines

		rv = REDUCTION_ENLARGE * rv + (1 - REDUCTION_ENLARGE) / 2;// (1-(1-rv*2)*REDUCTION_ENLARGE)/2;
		xr = (xmax - xmin) * rv;
		yr = (ymax - ymin) * rv;
		zr = (zmax - zmin) * rv;

		minMaxLarge[0][0] = xmin + xr;
		minMaxLarge[0][1] = xmax - xr;
		minMaxLarge[2][0] = ymin + yr;
		minMaxLarge[2][1] = ymax - yr;
		minMaxLarge[1][0] = zmin + zr;
		minMaxLarge[1][1] = zmax - zr;

		// update ev 3D depending algos
		getView3D().updateBounds();

		return minMax;
	}

	private int nearestCornerX = -1, nearestCornerY = -1, nearestCornerZ = -1;

	/**
	 * update corner nearest to the eye
	 * 
	 * @return true if nearest corner has changed
	 */
	public boolean updateNearestCorner() {
		Coords eye = getView3D().getEyePosition();
		int x, y, z;
		if (getView3D().getProjection() == EuclidianView3D.PROJECTION_ORTHOGRAPHIC
				|| getView3D().getProjection() == EuclidianView3D.PROJECTION_OBLIQUE) {
			x = eye.getX() > 0 ? 0 : 1;
			y = eye.getY() > 0 ? 0 : 1;
			z = eye.getZ() > 0 ? 0 : 1;
		} else {
			x = eye.getX() > 0 ? 1 : 0;
			y = eye.getY() > 0 ? 1 : 0;
			z = eye.getZ() > 0 ? 1 : 0;
		}
		boolean changed = false;
		if (x != nearestCornerX) {
			nearestCornerX = x;
			changed = true;
		}
		if (y != nearestCornerY) {
			nearestCornerY = y;
			changed = true;
		}
		if (z != nearestCornerZ) {
			nearestCornerZ = z;
			changed = true;
		}
		return changed;
	}

	private void setVertices() {
		for (int x = 0; x < 2; x++)
			for (int y = 0; y < 2; y++)
				for (int z = 0; z < 2; z++) {
					Coords vertex = vertices[x + 2 * y + 4 * z];
					vertex.setX(minMax[0][x]);
					vertex.setY(minMax[1][y]);
					vertex.setZ(minMax[2][z]);
				}

		for (int i = 0; i < 3; i++) {
			center.set(i + 1, (minMax[i][0] + minMax[i][1]) / 2);
		}
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th vertex
	 */
	public Coords getVertex(int i) {
		return vertices[i];
	}

	/**
	 * 
	 * @return coords of the center point
	 */
	public Coords getCenter() {
		return center;
	}

	/**
	 * 
	 * @return x, y, z min-max values
	 */
	public double[][] getMinMax() {
		return minMax;
	}

	private void setVertexWithBorder(int x, int y, int z, double border,
			Coords c) {
		Coords v = vertices[x + 2 * y + 4 * z];
		if (getView3D().getApplication().has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			c.setX(v.getX() + border * (1 - 2 * x) / getView3D().getXscale());
			c.setY(v.getY() + border * (1 - 2 * y) / getView3D().getYscale());
			c.setZ(v.getZ() + border * (1 - 2 * z) / getView3D().getZscale());
		} else {
			c.setX(v.getX() + border * (1 - 2 * x));
			c.setY(v.getY() + border * (1 - 2 * y));
			c.setZ(v.getZ() + border * (1 - 2 * z));
		}
	}

	/*
	 * @Override protected boolean isVisible(){ return
	 * getView3D().useClippingCube(); }
	 */

	private Coords tmpCoords1 = new Coords(3), tmpCoords2 = new Coords(3);
	private double border;

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		// clippingBorder = (float)
		// (GeoElement.MAX_LINE_WIDTH*PlotterBrush.LINE3D_THICKNESS/getView3D().getScale());

		// geometry
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(getReusableGeometryIndex());
		// use 1.5 factor for border to avoid self clipping
		border = 1.5 * brush.setThickness(getGeoElement()
				.getLineThickness(),
				(float) getView3D().getScale());
		brush.setAffineTexture(0.5f, 0.25f);

		drawSegment(brush, 0, 0, 0, 1, 0, 0);
		drawSegment(brush, 0, 0, 0, 0, 1, 0);
		drawSegment(brush, 0, 0, 0, 0, 0, 1);

		drawSegment(brush, 1, 1, 0, 0, 1, 0);
		drawSegment(brush, 1, 1, 0, 1, 0, 0);
		drawSegment(brush, 1, 1, 0, 1, 1, 1);

		drawSegment(brush, 1, 0, 1, 0, 0, 1);
		drawSegment(brush, 1, 0, 1, 1, 1, 1);
		drawSegment(brush, 1, 0, 1, 1, 0, 0);

		drawSegment(brush, 0, 1, 1, 1, 1, 1);
		drawSegment(brush, 0, 1, 1, 0, 0, 1);
		drawSegment(brush, 0, 1, 1, 0, 1, 0);

		setGeometryIndex(brush.end());
		
		updateRendererClipPlanes();

		return true;
	}

	private void drawSegment(PlotterBrush brush, int x1, int y1, int z1,
			int x2, int y2, int z2) {
		if (isNearestCorner(x1, y1, z1)) {
			return;
		}
		if (isNearestCorner(x2, y2, z2)) {
			return;
		}
		setVertexWithBorder(x1, y1, z1, border, tmpCoords1);
		setVertexWithBorder(x2, y2, z2, border, tmpCoords2);
		brush.segment(tmpCoords1, tmpCoords2);
	}

	private boolean isNearestCorner(int x, int y, int z) {

		if (!getView3D().getApplication().has(Feature.NO_CLIPPING_BOX_ON_FRONT)) {
			return false;
		}

		if (x != nearestCornerX) {
			return false;
		}
		if (y != nearestCornerY) {
			return false;
		}
		if (z != nearestCornerZ) {
			return false;
		}
		return true;
	}

	/**
	 * update renderer clips planes
	 */
	public void updateRendererClipPlanes() {
		Renderer renderer = getView3D().getRenderer();
		renderer.setClipPlanes(minMax);
	}

	@Override
	protected void updateForView() {

	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * for a line described by (o,v), return the min and max parameters to draw
	 * the line
	 * 
	 * @param minmax
	 *            initial interval
	 * @param o
	 *            origin of the line
	 * @param v
	 *            direction of the line
	 * @return interval to draw the line
	 */
	public double[] getIntervalClippedLarge(double[] minmax, Coords o, Coords v) {

		for (int i = 1; i <= 3; i++) {
			double min = (minMaxLarge[i - 1][0] - o.get(i)) / v.get(i);
			double max = (minMaxLarge[i - 1][1] - o.get(i)) / v.get(i);
			updateInterval(minmax, min, max);
		}

		return minmax;
	}

	/**
	 * for a line described by (o,v), return the min and max parameters to draw
	 * the line
	 * 
	 * @param minmax
	 *            initial interval
	 * @param o
	 *            origin of the line
	 * @param v
	 *            direction of the line
	 * @return interval to draw the line
	 */
	public double[] getIntervalClipped(double[] minmax, Coords o, Coords v) {

		for (int i = 1; i <= 3; i++) {
			updateInterval(minmax, o, v, i, minMax[i - 1][0], minMax[i - 1][1]);
		}

		return minmax;
	}

	/**
	 * intersect minmax interval with interval for (o,v)_index between boundsMin
	 * and boundsMax
	 * 
	 * @param minmax
	 *            interval to update
	 * @param o
	 *            origin
	 * @param v
	 *            direction
	 * @param index
	 *            x/y/z
	 * @param boundsMin
	 *            min for bounds
	 * @param boundsMax
	 *            max for bounds
	 */
	public static void updateInterval(double[] minmax, Coords o, Coords v,
			int index, double boundsMin, double boundsMax) {
		double min = (boundsMin - o.get(index)) / v.get(index);
		double max = (boundsMax - o.get(index)) / v.get(index);
		updateInterval(minmax, min, max);
	}

	/**
	 * return the intersection of intervals [minmax] and [v1,v2]
	 * 
	 * @param minmax
	 *            initial interval
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 * @return intersection interval
	 */
	private static double[] updateInterval(double[] minmax, double v1, double v2) {

		if (v1 > v2) {
			double v = v1;
			v1 = v2;
			v2 = v;
		}

		if (v1 > minmax[0])
			minmax[0] = v1;

		if (v2 < minmax[1])
			minmax[1] = v2;

		return minmax;
	}

	@Override
	protected boolean isVisible() {
		return true;
		// TODO -- take care of updateRendererClipPlanes();
		// return getView3D().showClippingCube();
	}

}
