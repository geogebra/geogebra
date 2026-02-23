/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author mathieu
 *
 *         Interface for lines (lines, segments, ray, ...) in any dimension
 */
public interface GeoLineND extends GeoDirectionND, LinearEquationRepresentable {

	/**
	 * returns the point at position lambda on the coord sys in the dimension
	 * given
	 * 
	 * @param dimension
	 *            dimension of returned point
	 * @param lambda
	 *            position on the line
	 * @return the point at position lambda on the coord sys
	 */
	Coords getPointInD(int dimension, double lambda);

	/**
	 * @return true if tracing
	 */
	boolean getTrace();

	/**
	 * @param m
	 *            plane
	 * @return the (a,b,c) equation vector that describe the line in the plane
	 *         described by the matrix m (ie ax+by+c=0 is an equation of the
	 *         line in the plane)
	 */
	Coords getCartesianEquationVector(CoordMatrix m);

	/**
	 * @return coords of the starting point
	 */
	Coords getStartInhomCoords();

	/**
	 * @return inhom coords of the end point
	 */
	Coords getEndInhomCoords();

	/**
	 * see PathOrPoint
	 * 
	 * @return min parameter
	 */
	double getMinParameter();

	/**
	 * see PathOrPoint
	 * 
	 * @return max parameter
	 */
	double getMaxParameter();

	/**
	 * 
	 * @param p
	 *            point
	 * @param minPrecision
	 *            precision
	 * @return true if point is on the path
	 */
	boolean isOnPath(GeoPointND p, double minPrecision);

	/**
	 * @param coords
	 *            point
	 * @param eps
	 *            precision
	 * @return true if point is on path (with given precision)
	 */
	boolean isOnPath(Coords coords, double eps);

	/**
	 * when intersection point is calculated, check if not outside limited path
	 * (segment, ray)
	 * 
	 * @param coords
	 *            point
	 * @param eps
	 *            precision
	 * @return true if not outside
	 */
	boolean respectLimitedPath(Coords coords, double eps);

	/**
	 * check if the parameter is possible on the line
	 * 
	 * @param parameter
	 *            parameter
	 * @return true if possible
	 */
	boolean respectLimitedPath(double parameter);

	/**
	 * @param p
	 *            point
	 * @param minPrecision
	 *            precision
	 * @return true if point is on this line (ignoring limits for segment/ray)
	 */
	boolean isOnFullLine(Coords p, double minPrecision);

	/**
	 * @return end point
	 */
	GeoPointND getEndPoint();

	/**
	 * @return start point
	 */
	GeoPointND getStartPoint();

	/**
	 * Removes a point from list of points that are registered as points on this
	 * line
	 * 
	 * @param point
	 *            point to be removed
	 */
	void removePointOnLine(GeoPointND point);

	/**
	 * Adds a point to the list of points that this line passes through.
	 * 
	 * @param point
	 *            point
	 */
	void addPointOnLine(GeoPointND point);

	/**
	 * returns the distance from this line to line g.
	 * 
	 * @param g
	 *            line
	 * @return distance distance between this and g
	 */
	double distance(GeoLineND g);

	/**
	 * 
	 * @return copy
	 */
	@Override
	GeoLineND copy();

	/**
	 * make parallel line through (pointX, pointY)
	 * 
	 * @param pointX
	 *            x coord
	 * @param pointY
	 *            y coord
	 */
	void setLineThrough(double pointX, double pointY);

	/**
	 * 
	 * @return line direction for equation (to keep integers if some)
	 */
	Coords getDirectionForEquation();

	/**
	 * Initialize startpoint to the closest point to (0,0,0)
	 * 
	 * @return the start point
	 */
	GeoPointND setStandardStartPoint();

	/**
	 * @param point
	 *            new start point
	 */
	void setStartPoint(GeoPointND point);

	/**
	 * @return line origin (in 2D the same as start point)
	 */
	Coords getOrigin();

	/**
	 * @param t
	 *            parameter
	 * @return value at given parameter as vector
	 */
	ExpressionValue evaluateCurve(double t);

}
