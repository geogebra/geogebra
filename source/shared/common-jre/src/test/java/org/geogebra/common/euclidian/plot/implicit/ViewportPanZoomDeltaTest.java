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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.kernel.interval.Interval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ViewportPanZoomDeltaTest {
	private static final double DELTA = 1e-9;

	@BeforeEach
	void setupFactory() {
		AwtFactory.setPrototypeIfNull(new AwtFactoryCommon());
	}

	@Test
	void shapeFollowsPanDelta() {
		MutableBounds bounds = new MutableBounds();
		ViewportPanZoomDelta panZoom = new ViewportPanZoomDelta(bounds);
		panZoom.snapshot();
		GRectangle2D source = rectangle(10, 20, 30, 40);

		bounds.xZero = 15;
		bounds.yZero = -8;
		GShape transformed = panZoom.applyTo(source);
		GRectangle2D transformedBounds = transformed.getBounds2D();

		assertAll(
				() -> assertEquals(25, transformedBounds.getX(), DELTA),
				() -> assertEquals(12, transformedBounds.getY(), DELTA),
				() -> assertEquals(30, transformedBounds.getWidth(), DELTA),
				() -> assertEquals(40, transformedBounds.getHeight(), DELTA));
	}

	@Test
	void shapeFollowsScaleDelta() {
		MutableBounds bounds = new MutableBounds();
		ViewportPanZoomDelta panZoom = new ViewportPanZoomDelta(bounds);
		panZoom.snapshot();
		GRectangle2D source = rectangle(10, 20, 30, 40);

		bounds.xScale = 2;
		bounds.yScale = 3;
		GShape transformed = panZoom.applyTo(source);
		GRectangle2D transformedBounds = transformed.getBounds2D();

		assertEquals(20, transformedBounds.getX(), DELTA);
		assertEquals(60, transformedBounds.getY(), DELTA);
		assertEquals(60, transformedBounds.getWidth(), DELTA);
		assertEquals(120, transformedBounds.getHeight(), DELTA);
	}

	@Test
	void areaWrapperStillReturnsArea() {
		MutableBounds bounds = new MutableBounds();
		ViewportPanZoomDelta panZoom = new ViewportPanZoomDelta(bounds);
		panZoom.snapshot();
		GArea source = AwtFactory.getPrototype().newArea(rectangle(0, 0, 10, 10));

		bounds.xZero = 5;
		GArea transformed = panZoom.applyTo(source);

		assertNotNull(transformed);
		assertEquals(5, transformed.getBounds2D().getX(), DELTA);
	}

	private static GRectangle2D rectangle(double x, double y, double width, double height) {
		GRectangle2D rectangle = AwtFactory.getPrototype().newRectangle2D();
		rectangle.setRect(x, y, width, height);
		return rectangle;
	}

	private static final class MutableBounds implements EuclidianViewBounds {
		private double xZero;
		private double yZero;
		private double xScale = 1;
		private double yScale = 1;

		@Override
		public int getWidth() {
			return 100;
		}

		@Override
		public int getHeight() {
			return 100;
		}

		@Override
		public Interval domain() {
			return new Interval(-50, 50);
		}

		@Override
		public Interval range() {
			return new Interval(-50, 50);
		}

		@Override
		public double getXmin() {
			return -50;
		}

		@Override
		public double getXmax() {
			return 50;
		}

		@Override
		public double getYmin() {
			return -50;
		}

		@Override
		public double getYmax() {
			return 50;
		}

		@Override
		public Interval toScreenIntervalX(Interval x) {
			return x;
		}

		@Override
		public Interval toScreenIntervalY(Interval y) {
			return y;
		}

		@Override
		public boolean isOnView(double x, double y) {
			return true;
		}

		@Override
		public double toScreenCoordXd(double x) {
			return xZero + x * xScale;
		}

		@Override
		public double toScreenCoordYd(double y) {
			return yZero + y * yScale;
		}

		@Override
		public double toRealWorldCoordX(double x) {
			return (x - xZero) / xScale;
		}

		@Override
		public double toRealWorldCoordY(double y) {
			return (y - yZero) / yScale;
		}

		@Override
		public boolean isOnView(Interval y) {
			return true;
		}

		@Override
		public double getInvXscale() {
			return 1 / xScale;
		}

		@Override
		public double getInvYscale() {
			return 1 / yScale;
		}

		@Override
		public double getXZero() {
			return xZero;
		}

		@Override
		public double getYZero() {
			return yZero;
		}

		@Override
		public GShape getBoundingPath() {
			return rectangle(0, 0, getWidth(), getHeight());
		}

		@Override
		public double getXScale() {
			return xScale;
		}

		@Override
		public double getYScale() {
			return yScale;
		}
	}
}
