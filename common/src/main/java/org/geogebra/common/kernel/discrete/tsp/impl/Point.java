package org.geogebra.common.kernel.discrete.tsp.impl;

public final class Point {

    private final double x;
    private final double y;
    private boolean active = true;

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Euclidean distance.
     * tour wraps around N-1 to 0.
     */ 
    public static double distance(final Point[] points) {
        final int len = points.length; 
	double d = points[len-1].distance(points[0]);
        for(int i = 1; i < len; i++)
            d += points[i-1].distance(points[i]);
        return d;
    }

    /**
     * Euclidean distance.
     */
    public final double distance(final Point to) {
        return Math.sqrt(_distance(to));
    }
   
    /**
     * compare 2 points.
     * no need to square when comparing.
     * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
     */ 
    public final double _distance(final Point to) {
        final double dx = this.x-to.x;
        final double dy = this.y-to.y;
	return (dx*dx)+(dy*dy); 
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
    	this.active = active;
    }

    public String toString() {
        return x + " " + y;
    }

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
