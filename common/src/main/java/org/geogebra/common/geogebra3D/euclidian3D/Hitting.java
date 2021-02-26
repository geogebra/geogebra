package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * class for rays, spheres, etc. that can hit 3D objects in 3D view
 * 
 * @author Proprietaire
 *
 */
public class Hitting {

	/**
	 * origin of the ray
	 */
	public Coords origin;

	/**
	 * direction of the ray
	 */
	public Coords direction;

	/**
	 * origin of the ray (screen coords)
	 */
	public Coords originScreen;

	/**
	 * direction of the ray (screen coords)
	 */
	public Coords directionScreen;

	/**
	 * View
	 */
	protected EuclidianView3D view;

	/**
	 * current threshold
	 */
	protected int threshold;
	private boolean clippedValuesUpdated;

	/** start point x */
	public double x0;
	/** start point y */
	public double y0;
	/** start point z */
	public double z0;
	/** end point x */
	public double x1;
	/** end point y */
	public double y1;
	/** end point z */
	public double z1;
	private double vx;
	private double vy;
	private double vz;
	private double squareNorm;

	private double[] minmax;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public Hitting(EuclidianView3D view) {
		this.view = view;

		origin = new Coords(4);
		origin.setW(1);

		direction = new Coords(4);

		originScreen = new Coords(4);
		originScreen.setW(1);

		directionScreen = new Coords(4);

	}

	/**
	 * set the hits
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @param threshold
	 *            threshold
	 */
	public void setHits(GPoint mouseLoc, int threshold) {

		setOriginDirectionThreshold(mouseLoc, threshold);
		setHits();
	}

	/**
	 * calculate clipped x, y, z values if not already updated
	 * 
	 */
	public void calculateClippedValues() {

		if (clippedValuesUpdated) {
			return;
		}

		if (minmax == null) {
			minmax = new double[2];
		}
		minmax[0] = Double.NEGATIVE_INFINITY;
		minmax[1] = Double.POSITIVE_INFINITY;
		view.getIntervalClipped(minmax, origin, direction);
		x0 = origin.getX() + direction.getX() * minmax[0];
		y0 = origin.getY() + direction.getY() * minmax[0];
		z0 = origin.getZ() + direction.getZ() * minmax[0];
		x1 = origin.getX() + direction.getX() * minmax[1];
		y1 = origin.getY() + direction.getY() * minmax[1];
		z1 = origin.getZ() + direction.getZ() * minmax[1];

		vx = x1 - x0;
		vy = y1 - y0;
		vz = z1 - z0;
		if (vx * direction.getX() < 0 || vy * direction.getY() < 0
				|| vz * direction.getZ() < 0) {
			x0 = Double.NaN;
		} else {
			squareNorm = vx * vx + vy * vy + vz * vz;
		}

		clippedValuesUpdated = true;
	}

	/**
	 * set origin, direction, threshold
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @param threshold
	 *            threshold
	 */
	public void setOriginDirectionThreshold(GPoint mouseLoc, int threshold) {

		view.getHittingOrigin(mouseLoc, origin);
		origin.setW(1);
		view.getHittingDirection(direction);
		direction.setW(0);
		setOriginDirectionThreshold(threshold);
	}

	/**
	 * set origin, direction, threshold
	 * 
	 * @param origin
	 *            origin
	 * @param direction
	 *            direction
	 * @param threshold
	 *            threshold
	 */
	public void setOriginDirectionThreshold(Coords origin, Coords direction,
			int threshold) {

		this.origin.set3(origin);
		this.direction.set3(direction);
		setOriginDirectionThreshold(threshold);
	}

	private void setOriginDirectionThreshold(int threshold) {
		this.threshold = threshold;

		// screen coords
		originScreen.set3(origin);
		view.toScreenCoords3D(originScreen);
		directionScreen.set3(direction);
		view.toScreenCoords3D(directionScreen);

		// we need to update clipped values
		clippedValuesUpdated = false;
	}

	/**
	 * set hits
	 */
	protected void setHits() {
		Hits3D hits = view.getHits3D();
		hits.init();

		if (view.getShowPlane()) {
			view.getPlaneDrawable().hitIfVisibleAndPickable(this, hits);
		}
		for (int i = 0; i < 3; i++) {
			view.getAxisDrawable(i).hitIfVisibleAndPickable(this, hits);
		}
		view.getDrawList3D().hit(this, hits);

		double zNear = hits.sort();
		view.setZNearest(zNear);
	}

	/**
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @return first hitted label geo
	 */
	public GeoElement getLabelHit(GPoint mouseLoc) {
		if (view.getProjection() == EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC) {
			return view.getDrawList3D().getLabelHit(originScreen.getX(),
					originScreen.getY());
		}

		return view.getDrawList3D().getLabelHit(originScreen, directionScreen);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @return true if this hits the label
	 */
	public boolean hitLabel(DrawLabel3D label) {
		if (view.getProjection() == EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC) {
			return label.hit(originScreen.getX(), originScreen.getY());
		}

		return label.hit(originScreen, directionScreen);
	}

	/**
	 * 
	 * @param p
	 *            point coords
	 * @return true if the point is inside the clipping box (if used)
	 */
	final public boolean isInsideClipping(Coords p) {
		if (view.useClippingCube()) {
			return view.isInside(p);
		}

		return true;

	}

	/**
	 * 
	 * @return current threshold
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * @return norm^2
	 */
	public double getSquareNorm() {
		return squareNorm;
	}

	/**
	 * @return x1 - x0
	 */
	public double getVx() {
		return vx;
	}

	/**
	 * @return y1 - y0
	 */
	public double getVy() {
		return vy;
	}

	/**
	 * @return z1 - z0
	 */
	public double getVz() {
		return vz;
	}

    /**
     *
     * @return true if discards positive hits
     */
	public boolean discardPositiveHits() {
	    return view.isXREnabled();
    }
}
