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

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoStadium;
import org.geogebra.common.kernel.matrix.CoordSys;

public class DrawStadium extends DrawLocus {
	private final GeoStadium stadium;

	/**
	 * Creates new drawable for given locus
	 * @param view view
	 * @param stadium locus
	 * @param transformSys coord system of transformed locus
	 */
	public DrawStadium(EuclidianView view,
			GeoStadium stadium,
			CoordSys transformSys) {
		super(view, stadium, transformSys);
		this.stadium = stadium;
	}

	/**
	 * @return midpoints of left, right and bottom sides of the bounding rectangle
	 * (assuming the stadium is horizontal)
	 */
	@Override
	public ArrayList<GPoint2D> toPoints() {
		ArrayList<GPoint2D> points = new ArrayList<>();
		GeoPoint p = stadium.getP();
		GeoPoint q = stadium.getQ();
		GPoint2D heightVec = getHeightVec(stadium, p, q);
		MyPoint leftMidpoint = new MyPoint(view.toScreenCoordXd(p.x + heightVec.y),
				view.toScreenCoordYd(p.y - heightVec.x));
		MyPoint rightMidpoint = new MyPoint(view.toScreenCoordXd(q.x - heightVec.y),
				view.toScreenCoordYd(q.y + heightVec.x));
		MyPoint bottomMidpoint = getBottomMidpoint(heightVec, leftMidpoint, rightMidpoint);
		points.add(leftMidpoint);
		points.add(rightMidpoint);
		points.add(bottomMidpoint);
		return points;
	}

	private MyPoint getBottomMidpoint(GPoint2D heightVec, MyPoint leftMidpoint,
			MyPoint rightMidpoint) {
		return new MyPoint(
				(leftMidpoint.x + rightMidpoint.x) / 2 + heightVec.x * view.getXscale(),
				(leftMidpoint.y + rightMidpoint.y) / 2 + heightVec.y * view.getYscale());
	}

	private GPoint2D getHeightVec(GeoStadium shapeLocus, GeoPoint p, GeoPoint q) {
		return new GPoint2D(shapeLocus.getHeight().getValue() * (p.y - q.y) / p.distance(q) / 2,
		 shapeLocus.getHeight().getValue() * (p.x - q.x) / p.distance(q) / 2);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		GPoint2D left = points.get(0);
		GPoint2D right = points.get(1);
		GPoint2D ref = points.get(2);

		MyPoint rwLeft = new MyPoint(view.toRealWorldCoordX(left.x),
				view.toRealWorldCoordY(left.y));
		MyPoint rwRight = new MyPoint(view.toRealWorldCoordX(right.x),
				view.toRealWorldCoordY(right.y));
		MyPoint rwRef = new MyPoint(view.toRealWorldCoordX(ref.x), view.toRealWorldCoordY(ref.y));

		// Step 1: Compute midpoint
		double midX = (rwLeft.x + rwRight.x) / 2;
		double midY = (rwLeft.y + rwRight.y) / 2;
		double radius = Math.hypot(midX - rwRef.x, midY - rwRef.y);
		double halfHeightX = rwRef.x - midX;
		double halfHeightY = rwRef.y - midY;
		stadium.update(rwLeft.x + halfHeightY,
				rwLeft.y + halfHeightX,
				rwRight.x - halfHeightY,
				rwRight.y - halfHeightX, radius * 2);
		stadium.getParentAlgorithm().update();
	}
}
