package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing 3D constant planes.
 * 
 * @author matthieu
 *
 */
public class DrawClippingCube3D extends Drawable3DCurves {

	final static private int MIN = 0;
	final static private int MAX = 1;
	final static private int X = 0;
	final static private int Y = 1;
	final static private int Z = 2;

	private double[][] minMax;
	private double[][] minMaxLarge;

	private double[][] currentBounds;
	private double[][] minMaxObjects;

	private Coords[] vertices;
    private Coords[] verticesLarge;

	private Coords center;

	static private double REDUCTION_LARGE = 0; // (1-1./1)/2

    /**
     * enlarging factor from minMax values to minMaxLarge values
     */
	final static public double REDUCTION_ENLARGE = 1.5;

	static private double[] REDUCTION_VALUES = { (1 - 1. / Math.sqrt(3)) / 2, // small
			(1 - 1. / Math.sqrt(2)) / 2, // medium
			REDUCTION_LARGE // large
	};

	static private double[] INTERIOR_RADIUS_FACTOR = { 1, Math.sqrt(2),
			Math.sqrt(3) };
	private double horizontalDiagonal;

	/**
	 * max value from center to one FRUSTUM edge
	 */
	private double frustumRadius;

	/**
	 * min value from center to one FRUSTUM face
	 */
	private double frustumInteriorRadius;

	private int nearestCornerX = -1;
	private int nearestCornerY = -1;
	private int nearestCornerZ = -1;
	private Coords tmpCoords1 = new Coords(3);
	private Coords tmpCoords2 = new Coords(3);
	private double border;

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
		currentBounds = new double[3][];
		minMaxObjects = new double[3][];

		for (int i = 0; i < 3; i++) {
			minMax[i] = new double[2];
			minMaxLarge[i] = new double[2];
			currentBounds[i] = new double[2];
			minMaxObjects[i] = new double[2];
		}
		clearEnlarge();

		vertices = new Coords[8];
        verticesLarge = new Coords[8];
		for (int i = 0; i < 8; i++) {
			vertices[i] = new Coords(0, 0, 0, 1);
            verticesLarge[i] = new Coords(0, 0, 0, 1);
		}
	}

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
	 * @param minmax
	 *            min/max values for bounds
	 */
	public void setXYZMinMax(double[][] minmax) {
		minMax[0][0] = minmax[0][0];
		minMax[0][1] = minmax[0][1];
		minMax[1][0] = minmax[1][0];
		minMax[1][1] = minmax[1][1];
		minMax[2][0] = minmax[2][0];
		minMax[2][1] = minmax[2][1];
	}

	/**
	 * update the x,y,z min/max values
	 *
	 */
	public void doUpdateMinMax() {

        EuclidianView3D view = getView3D();

        Renderer renderer = view.getRenderer();

        double xscale = view.getXscale();
        double yscale = view.getYscale();
        double zscale = view.getZscale();

		double x0 = -view.getXZero();
		double y0 = -view.getYZero();
		double z0 = -view.getZZero();

		double halfWidth = renderer.getWidth() / 2.0;
		double bottom = renderer.getBottom();
		double top = renderer.getTop();

		if (view.isXREnabled()) {
			XRManagerInterface<?> arManager = renderer.getXRManager();
			if (arManager != null) {
				double arScaleFactor = renderer.getXRManager().getXRScaleFactor();
				halfWidth *= arScaleFactor;
				bottom *= arScaleFactor;
				top *= arScaleFactor;
			}
		}

        currentBounds[X][MIN] = -halfWidth / xscale + x0;
        currentBounds[X][MAX] = halfWidth / xscale + x0;

        if (getView3D().getYAxisVertical()) {
            currentBounds[Y][MIN] = (bottom) / yscale + y0;
            currentBounds[Y][MAX] = (top) / yscale + y0;
            currentBounds[Z][MIN] = -halfWidth / zscale + z0;
            currentBounds[Z][MAX] = halfWidth / zscale + z0;
        } else {
            currentBounds[Z][MIN] = (bottom) / zscale + z0;
            currentBounds[Z][MAX] = (top) / zscale + z0;
            currentBounds[Y][MIN] = -halfWidth / yscale + y0;
            currentBounds[Y][MAX] = halfWidth / yscale + y0;
        }

        int reductionIndex = ((GeoClippingCube3D) getGeoElement())
                .getReduction();
        double rv = 0;
        if (renderer.reduceForClipping()) {
            rv = REDUCTION_VALUES[reductionIndex];
        }
        double xr = (currentBounds[X][MAX] - currentBounds[X][MIN]);
        double yr = (currentBounds[Y][MAX] - currentBounds[Y][MIN]);
        double zr = (currentBounds[Z][MAX] - currentBounds[Z][MIN]);

        if (view.isXREnabled()) {
            for (int i = 0; i < 3; i++) {
                mayEnlarge(currentBounds[i], minMaxObjects[i]);
            }
        }

        minMax[X][MIN] = currentBounds[X][MIN] + xr * rv;
        minMax[X][MAX] = currentBounds[X][MAX] - xr * rv;
        minMax[Y][MIN] = currentBounds[Y][MIN] + yr * rv;
        minMax[Y][MAX] = currentBounds[Y][MAX] - yr * rv;
        minMax[Z][MIN] = currentBounds[Z][MIN] + zr * rv;
        minMax[Z][MAX] = currentBounds[Z][MAX] - zr * rv;

        standsOnFloorIfAR(minMax);

        setVertices();

        horizontalDiagonal = renderer.getWidth() * (1 - 2 * rv) * Math.sqrt(2);

        double scaleMax = Math.max(Math.max(xscale, yscale), zscale);
        double scaleMin = Math.min(Math.min(xscale, yscale), zscale);
        double w, h, d;
        if (view.isXREnabled()) {
            w = (currentBounds[X][MAX] - currentBounds[X][MIN]) * xscale;
            h = (currentBounds[Y][MAX] - currentBounds[Y][MIN]) * yscale;
            d = (currentBounds[Z][MAX] - currentBounds[Z][MIN]) * zscale;
        } else {
            w = renderer.getWidth();
            h = renderer.getHeight();
            d = renderer.getVisibleDepth();
        }
        frustumRadius = Math.sqrt(w * w + h * h + d * d) / (2 * scaleMin);

        frustumInteriorRadius = Math.min(w, Math.min(h, d)) / (2 * scaleMax);
        frustumInteriorRadius *= INTERIOR_RADIUS_FACTOR[reductionIndex];

        view.setXYMinMax(minMax);

        // minMaxLarge to cut lines

        rv = REDUCTION_ENLARGE * rv + (1 - REDUCTION_ENLARGE) / 2;

        minMaxLarge[X][MIN] = currentBounds[X][MIN] + xr * rv;
        minMaxLarge[X][MAX] = currentBounds[X][MAX] - xr * rv;
        minMaxLarge[Y][MIN] = currentBounds[Y][MIN] + yr * rv;
        minMaxLarge[Y][MAX] = currentBounds[Y][MAX] - yr * rv;
        minMaxLarge[Z][MIN] = currentBounds[Z][MIN] + zr * rv;
        minMaxLarge[Z][MAX] = currentBounds[Z][MAX] - zr * rv;

        standsOnFloorIfAR(minMaxLarge);

        // update ev 3D depending algos
        getView3D().updateBounds();
    }

    private void standsOnFloorIfAR(double[][] mm) {
        EuclidianView3D view = getView3D();
        if (view.isXREnabled()) {
			mm[Z][MIN] = view.getARMinZ();
        }
    }

    public double getRV(int index) {
		return REDUCTION_VALUES[index];
	}

    /**
     * update the x,y,z min/max values
     *
     * @return the min/max values
     */
    public double[][] updateMinMax() {
        doUpdateMinMax();
        return minMax;
    }

    /**
     * update the x,y,z min/max values
     *
     * @return the min/max values (large)
     */
    public double[][] updateMinMaxLarge() {
        doUpdateMinMax();
        return minMaxLarge;
    }

    private static void mayEnlarge(double[] v, double[] enlarge) {
		if (v[MIN] > enlarge[MIN]) {
			v[MIN] = enlarge[MIN];
		}
		mayEnlargeMax(v, enlarge);
	}

    private static void mayEnlargeMax(double[] v, double[] enlarge) {
        if (v[MAX] < enlarge[MAX]) {
            v[MAX] = enlarge[MAX];
        }
    }

	/**
	 * enlarge min/max regarding object coords
	 * 
	 * @param v
	 *            object coords
	 * @return true if bounds need to be updated
	 */
	public boolean enlargeFor(Coords v) {
		boolean needsUpdate = false;
		for (int i = 0; i < 3; i++) {
			double value = v.get(i + 1);
			if (minMaxObjects[i][MIN] > value) {
				minMaxObjects[i][MIN] = value;
				needsUpdate |= currentBounds[i][MIN] > value;
			}
			if (minMaxObjects[i][MAX] < value) {
				minMaxObjects[i][MAX] = value;
				needsUpdate |= currentBounds[i][MAX] < value;
			}
		}
		return needsUpdate;
	}

	/**
	 * Reset min/max enlarging values for clipping cube
	 */
	public void clearEnlarge() {
        for (int i = 0; i < 3; i++) {
            minMaxObjects[i][MIN] = Double.POSITIVE_INFINITY;
            minMaxObjects[i][MAX] = Double.NEGATIVE_INFINITY;
        }
    }

	/**
	 * @param xmin
	 *            xmin
	 * @param xmax
	 *            xmax
	 * @param ymin
	 *            ymin
	 * @param ymax
	 *            ymax
	 * @param zmin
	 *            zmin
	 * @param zmax
	 *            zmax
	 * @return updated min/max matrix
	 */
	public double[][] updateMinMax(double xmin, double xmax, double ymin, double ymax, double zmin,
			double zmax) {

		minMax[0][0] = xmin;
		minMax[0][1] = xmax;
		minMax[1][0] = ymin;
		minMax[1][1] = ymax;
		minMax[2][0] = zmin;
		minMax[2][1] = zmax;

		setVertices();

		double w = xmax - xmin;
		double h = ymax - ymin;
		double d = zmax - zmin;

		EuclidianView3D view = getView3D();
		double xscale = view.getXscale();
		int reductionIndex = ((GeoClippingCube3D) getGeoElement()).getReduction();
		horizontalDiagonal = w / xscale * Math.sqrt(2);

		frustumRadius = Math.sqrt(w * w + h * h + d * d) / 2;

		frustumInteriorRadius = Math.min(w, Math.min(h, d)) / 2;
		frustumInteriorRadius *= INTERIOR_RADIUS_FACTOR[reductionIndex];

		view.setXYMinMax(minMax);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				minMaxLarge[i][j] = minMax[i][j];
			}
		}

		// update ev 3D depending algos
		getView3D().updateBounds();

		return minMax;
	}

	/**
	 * update corner nearest to the eye
	 * 
	 * @return true if nearest corner has changed
	 */
	public boolean updateNearestCorner() {
		Coords eye = getView3D().getEyePosition();
		int x, y, z;
		if (getView3D()
				.getProjection() == EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC
				|| getView3D()
						.getProjection() == EuclidianView3DInterface.PROJECTION_OBLIQUE) {
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
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					Coords vertex = vertices[x + 2 * y + 4 * z];
					vertex.setX(minMax[X][x]);
					vertex.setY(minMax[Y][y]);
					vertex.setZ(minMax[Z][z]);
                    vertex = verticesLarge[x + 2 * y + 4 * z];
                    vertex.setX(minMaxLarge[X][x]);
                    vertex.setY(minMaxLarge[Y][y]);
                    vertex.setZ(minMaxLarge[Z][z]);
				}
			}
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
	 * @return vertices
	 */
	public Coords[] getVertices() {
		return vertices;
	}

    /**
     *
     * @return verticesLarge
     */
    public Coords[] getVerticesLarge() {
        return verticesLarge;
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

    /**
     *
     * @return minimum z, large value
     */
	public double getZminLarge() {
	    return minMaxLarge[Z][MIN];
    }

	private void setVertexWithBorder(int x, int y, int z, double border,
			Coords c) {
		Coords v = vertices[x + 2 * y + 4 * z];
		c.setX(v.getX() + border * (1 - 2 * x) / getView3D().getXscale());
		c.setY(v.getY() + border * (1 - 2 * y) / getView3D().getYscale());
		c.setZ(v.getZ() + border * (1 - 2 * z) / getView3D().getZscale());
	}

	/*
	 * @Override protected boolean isVisible(){ return
	 * getView3D().useClippingCube(); }
	 */

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		// clippingBorder = (float)
		// (GeoElement.MAX_LINE_WIDTH*PlotterBrush.LINE3D_THICKNESS/getView3D().getScale());

		// geometry
		setPackCurve();
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(getReusableGeometryIndex());
		// use 1.5 factor for border to avoid self clipping
		border = 1.5 * brush.setThickness(getGeoElement().getLineThickness(),
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
		endPacking();

		updateRendererClipPlanes();

		return true;
	}

	private void drawSegment(PlotterBrush brush, int x1, int y1, int z1, int x2,
			int y2, int z2) {

		setVertexWithBorder(x1, y1, z1, border, tmpCoords1);
		setVertexWithBorder(x2, y2, z2, border, tmpCoords2);
		brush.segment(tmpCoords1, tmpCoords2);
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
		// nothing to do
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
	public double[] getIntervalClippedLarge(double[] minmax, Coords o,
			Coords v) {

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
	private static double[] updateInterval(double[] minmax, double v1,
			double v2) {
		double vMin, vMax;
		if (v1 > v2) {
			vMax = v1;
			vMin = v2;
		} else {
			vMax = v2;
			vMin = v1;
		}

		if (vMin > minmax[0]) {
			minmax[0] = vMin;
		}

		if (vMax < minmax[1]) {
			minmax[1] = vMax;
		}

		return minmax;
	}

	@Override
	public boolean isVisible() {
		return getView3D().showClippingCube();
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
