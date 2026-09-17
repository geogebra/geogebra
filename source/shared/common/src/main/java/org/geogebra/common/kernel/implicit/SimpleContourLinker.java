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

package org.geogebra.common.kernel.implicit;

import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;

public class SimpleContourLinker extends ContourLinker {

	private PointList points1;
	private PointList points2;
	private ContourAssembler contour;
	private double threshold;

	public SimpleContourLinker(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public void link(MyPoint p0, MyPoint p1, boolean canSwap) {
		MyPoint[] pts = new MyPoint[] {p0, p1};
		if (canSwap && pts[0].x > pts[1].x) {
			MyPoint temp = pts[0];
			pts[0] = pts[1];
			pts[1] = temp;
		}

		ListIterator<PointList> itr1 = listIterator();
		ListIterator<PointList> itr2 = listIterator();
		boolean pt1Start = false, pt0End = false;
		while (itr1.hasNext()) {
			points1 = itr1.next();
			if (equal(pts[1], points1.start)) {
				pt1Start = true;
				break;
			}
		}

		while (itr2.hasNext()) {
			points2 = itr2.next();
			if (equal(pts[0], points2.end)) {
				pt0End = true;
				break;
			}
		}

		if (pt1Start && pt0End) {
			itr1.remove();
			points2.mergeTo(points1);
		} else if (pt1Start) {
			points1.extendBack(pts[0]);
		} else if (pt0End) {
			points2.extendFront(pts[1]);
		} else {
			addFirst(new PointList(pts[0], pts[1]));
		}
		if (contour != null && size() > threshold) {
			contour.flush();
		}
	}

	public void setContour(ContourAssembler contour) {
		this.contour = contour;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
