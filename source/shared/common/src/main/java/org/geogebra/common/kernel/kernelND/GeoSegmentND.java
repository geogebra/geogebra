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

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.LimitedPath;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Simple interface used to join GeoSegment and GeoSegment3D
 * 
 * @author ggb3D
 *
 */
public interface GeoSegmentND extends GeoLineND, LimitedPath, GeoNumberValue,
		FromMeta, Parametrizable {

	/**
	 * Sets start point and end point with inhom coords
	 * 
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 */
	public void setTwoPointsInhomCoords(Coords start, Coords end);

	/** @return length of the segment */
	double getLength();

	/**
	 * @return start point
	 */
	public GeoElement getStartPointAsGeoElement();

	/**
	 * @return end point
	 */
	public GeoElement getEndPointAsGeoElement();

	/**
	 * return the x-coordinate of the point on the segment according to the
	 * parameter value
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the x-coordinate of the point
	 */
	public double getPointX(double parameter);

	/**
	 * return the y-coordinate of the point on the segment according to the
	 * parameter value
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the y-coordinate of the point
	 */
	public double getPointY(double parameter);

	/**
	 * modify the input points
	 * 
	 * @param P
	 *            new first point
	 * @param Q
	 *            new second point
	 */
	public void modifyInputPoints(GeoPointND P, GeoPointND Q);

	/**
	 * set the segment through the two points
	 * 
	 * @param locusPoint
	 *            first point
	 * @param locusPoint2
	 *            second point
	 */
	public void setCoords(MyPoint locusPoint, MyPoint locusPoint2);

	/**
	 * 
	 * @return unlabeled copy with free Input Points
	 */
	GeoElement copyFreeSegment();

	/**
	 * used for GeoSegment3D
	 * 
	 * @param ccp
	 *            changeable coord parent
	 */
	public void setChangeableParentIfNull(ChangeableParent ccp);

}
