package org.geogebra.common.kernel.discrete.tsp.impl;

public interface Point {

	// private final double x;
	// private final double y;
	// private boolean active = true;



    /**
     * Euclidean distance.
     */
	public double distance(final Point to);
	// {
	// return Math.sqrt(_distance(to));
	// }
   
    /**
     * compare 2 points.
     * no need to square when comparing.
     * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
     */ 
	public double distanceSqr(final Point to);
	// {
	// final double dx = this.x-to.x;
	// final double dy = this.y-to.y;
	// return (dx*dx)+(dy*dy);
	// }

	public boolean isActive();
	// {
	// return active;
	// }

	public void setActive(final boolean active);
	// {
	// this.active = active;
	// }

	public double getX();
	// {
	// return x;
	// }

	public double getY();
	// {
	// return y;
	// }
}
