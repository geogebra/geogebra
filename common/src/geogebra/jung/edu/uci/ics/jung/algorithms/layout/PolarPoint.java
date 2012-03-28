/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.layout;

import java.awt.geom.Point2D;

/**
 * Represents a point in polar coordinates: distance and angle from the origin.
 * Includes conversions between polar and Cartesian
 * coordinates (Point2D).
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 */
public class PolarPoint 
{
	double theta;
	double radius;
	
	/**
	 * Creates a new instance with radius and angle each 0.
	 */
	public PolarPoint() {
		this(0,0);
	}

	/**
	 * Creates a new instance with radius {@code radius} and angle {@code theta}.
	 */
	public PolarPoint(double theta, double radius) {
		this.theta = theta;
		this.radius = radius;
	}
	
	/**
	 * Returns the angle for this point.
	 */
	public double getTheta() { return theta; }

	/**
	 * Returns the radius for this point.
	 */
	public double getRadius() { return radius; }
	
	/**
	 * Sets the angle for this point to {@code theta}.
	 */
	public void setTheta(double theta) { this.theta = theta; }
	
	/**
	 * Sets the radius for this point to {@code theta}.
	 */
	public void setRadius(double radius) { this.radius = radius; }

	/**
	 * Returns the result of converting <code>polar</code> to Cartesian coordinates.
	 */
	public static Point2D polarToCartesian(PolarPoint polar) {
		return polarToCartesian(polar.getTheta(), polar.getRadius());
	}

	/**
	 * Returns the result of converting <code>(theta, radius)</code> to Cartesian coordinates.
	 */
	public static Point2D polarToCartesian(double theta, double radius) {
		return new Point2D.Double(radius*Math.cos(theta), radius*Math.sin(theta));
	}

	/**
	 * Returns the result of converting <code>point</code> to polar coordinates.
	 */
	public static PolarPoint cartesianToPolar(Point2D point) {
		return cartesianToPolar(point.getX(), point.getY());
	}

	/**
	 * Returns the result of converting <code>(x, y)</code> to polar coordinates.
	 */
	public static PolarPoint cartesianToPolar(double x, double y) {
		double theta = Math.atan2(y,x);
		double radius = Math.sqrt(x*x+y*y);
		return new PolarPoint(theta, radius);
	}
	
	@Override
	public String toString() {
	    return "PolarPoint[" + radius + "," + theta +"]";
	}
	
	/**
	 * Sets the angle and radius of this point to those of {@code p}.
	 */
	public void setLocation(PolarPoint p) {
		this.theta = p.getTheta();
		this.radius = p.getRadius();
	}
}