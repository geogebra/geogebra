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

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;

/**
 * EdgeType (the four edges of the view) with its projection method of that edge.
 */
public enum EdgeType {
	LEFT {
		@Override
		MyPoint getProjection(double sx, double sy,
				EuclidianViewBounds bounds,
				double marginWorldX, double marginWorldY) {
			double x = bounds.getXmin() - marginWorldX;
			double y = bounds.toRealWorldCoordY(sy);
			return new MyPoint(x, y);
		}
	},
	RIGHT {
		@Override
		MyPoint getProjection(double sx, double sy,
				EuclidianViewBounds bounds,
				double marginWorldX, double marginWorldY) {
			double x = bounds.getXmax() + marginWorldX;
			double y = bounds.toRealWorldCoordY(sy);
			return new MyPoint(x, y);
		}
	},
	TOP {
		@Override
		MyPoint getProjection(double sx, double sy,
				EuclidianViewBounds bounds,
				double marginWorldX, double marginWorldY) {
			double x = bounds.toRealWorldCoordX(sx);
			double y = bounds.getYmax() + marginWorldY;
			return new MyPoint(x, y);
		}
	},
	BOTTOM {
		@Override
		MyPoint getProjection(double sx, double sy,
				EuclidianViewBounds bounds,
				double marginWorldX, double marginWorldY) {
			double x = bounds.toRealWorldCoordX(sx);
			double y = bounds.getYmin() - marginWorldY;
			return new MyPoint(x, y);
		}
	},
	NONE {
		@Override
		MyPoint getProjection(double sx, double sy,
				EuclidianViewBounds bounds,
				double mx, double my) {
			return null;
		}
	};

	/**
	 * @return if the instance is a valid edge.
	 */
	public boolean isValid() {
		return this != NONE;
	}

	/**
	 * Gets the projection on the edge of a given point.
	 * @param sx x screen coordinate
	 * @param sy y screen coordinate
	 * @param bounds {@link EuclidianViewBounds}
	 * @return the projection of (sx, sy) on the edge.
	 */
	abstract MyPoint getProjection(double sx, double sy,
			EuclidianViewBounds bounds,
			double marginWorldX,
			double marginWorldY);

}
