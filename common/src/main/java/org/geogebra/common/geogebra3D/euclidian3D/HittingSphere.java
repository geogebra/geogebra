package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * 
 * Hitting with a sphere
 * 
 * @author mathieu
 *
 */
public class HittingSphere extends Hitting {

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public HittingSphere(EuclidianView3D view) {
		super(view);
	}

	/**
	 * set the hits
	 * 
	 * @param pos
	 *            mouse 3D location
	 * @param threshold
	 *            threshold
	 */
	public void setHits(Coords pos, int threshold) {

		origin = pos;
		direction = view.getHittingDirection();

		this.threshold = threshold;

		setHits();

	}

	@Override
	public boolean isSphere() {
		return true;
	}

}
