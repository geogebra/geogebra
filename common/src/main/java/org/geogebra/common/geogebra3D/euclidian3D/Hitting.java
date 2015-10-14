package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

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
	 * last mouse pos (centered)
	 */
	public GPoint pos;

	protected EuclidianView3D view;

	/**
	 * current threshold
	 */
	protected int threshold;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public Hitting(EuclidianView3D view) {
		this.view = view;
		pos = new GPoint();
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

		view.setCenteredPosition(mouseLoc, pos);
		setOriginDirectionThreshold(view.getHittingOrigin(mouseLoc),
				view.getHittingDirection(), threshold);

		setHits();

	}

	private boolean clippedValuesUpdated;

	public double x0, y0, z0, x1, y1, z1;

	private double[] minmax;

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
		x0 = origin.getX() + direction.getX()
				* minmax[0];
		y0 = origin.getY() + direction.getY()
				* minmax[0];
		z0 = origin.getZ() + direction.getZ()
				* minmax[0];
		x1 = origin.getX() + direction.getX()
				* minmax[1];
		y1 = origin.getY() + direction.getY()
				* minmax[1];
		z1 = origin.getZ() + direction.getZ()
				* minmax[1];

		if ((x1 - x0) * direction.getX() < 0
				|| (y1 - y0) * direction.getY() < 0
				|| (z1 - z0) * direction.getZ() < 0) {
			x0 = Double.NaN;
		}

		clippedValuesUpdated = true;
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
		this.origin = origin;
		this.direction = direction;
		this.threshold = threshold;

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
		view.setCenteredPosition(mouseLoc, pos);
		return view.getDrawList3D().getLabelHit(pos);
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
	 * 
	 * @return true if the hitting use depth values
	 */
	public boolean isSphere() {
		return false;
	}
}
