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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.draw.SvgGraphics;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Grid test
 *
 * @author Zbynek
 *
 */
class DrawGridTest {

	private AppCommon app;

	/**
	 * Create test app
	 */
	@BeforeEach
	void setup() {
		app = AppCommonFactory.create3D();
	}

	/**
	 * Checks the right number of gridlines in EV for 800x600 view and 50px
	 * scale
	 */
	@Test
	void thereShouldBeGridInSVGExport() {
		EuclidianSettings settings = app.getActiveEuclidianView().getSettings();
		app.getActiveEuclidianView()
				.centerView(new GeoPoint(app.getKernel().getConstruction(), 0, 0, 1));
		settings.setGridColor(GColor.BLUE);
		settings.showGrid(true);
		app.getActiveEuclidianView().updateBackground();
		hasBlueLines(143, 30);
		settings.setPositiveAxis(0, true);
		hasBlueLines(102, 21);
		settings.setPositiveAxis(1, true);
		hasBlueLines(72, 15);
		settings.setPositiveAxis(0, false);
		hasBlueLines(113, 24);
		settings.setPositiveAxis(1, false);

		settings.setGridType(EuclidianView.GRID_CARTESIAN);
		hasBlueLines(30, 30);
		settings.setPositiveAxis(0, true);
		hasBlueLines(21, 21);
		settings.setPositiveAxis(1, true);
		hasBlueLines(15, 15);
		settings.setPositiveAxis(0, false);
		hasBlueLines(24, 24);
	}

	@Test
	@Issue("APPS-8185")
	void tinyGridDistanceShouldNotDrawTooManyLines() {
		showGrid(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID, 1E-4);
		assertTrue(countGridLines() < 1000);
	}

	@ParameterizedTest
	@ValueSource(
			ints = {
				EuclidianView.GRID_CARTESIAN,
				EuclidianView.GRID_CARTESIAN_WITH_SUBGRID,
				EuclidianView.GRID_ISOMETRIC,
				EuclidianView.GRID_POLAR,
				EuclidianView.GRID_DOTS
			})
	@Issue("APPS-8185")
	void negativeGridDistanceShouldNotLoopForever(int gridType) {
		showGrid(gridType, -1);
		assertTrue(countGridLines() < 1000);
	}

	private void showGrid(int gridType, double distance) {
		EuclidianView view = app.getActiveEuclidianView();
		view.getSettings().setShowAxes(false, false);
		view.getSettings().showGrid(true);
		// set on the view, settings changes would repaint with the graphics of the headless app
		view.setGridType(gridType);
		view.setGridDistances(new double[] {distance, distance, Math.PI / 6});
	}

	private int countGridLines() {
		LineCountingGraphics graphics = new LineCountingGraphics();
		app.getActiveEuclidianView().paint(graphics);
		return graphics.lineCount;
	}

	private void hasBlueLines(int expectMinor, int expectMajor) {
		SvgGraphics svgGraphics = new SvgGraphics(800, 600);
		app.getActiveEuclidianView().paint(svgGraphics);
		String svg = svgGraphics.toString();
		int start = 0;
		// int lines = 0;
		String[] lines = svg.split("\n");
		int minor = 0;
		int major = 0;
		for (String line : lines) {

			if (line.indexOf("#0000ff", start) > 0) {
				minor++;
				if (line.indexOf("stroke-opacity=\"1", start) > 0) {
					major++;
				}
			}
		}
		assertEquals(expectMinor, minor);
		assertEquals(expectMajor, major);
	}

	/**
	 * Counts lines and shapes and fails fast instead of drawing (almost) forever.
	 */
	private static final class LineCountingGraphics extends GGraphicsCommon {
		private static final int MAX_LINES = 100_000;
		private int lineCount = 0;

		@Override
		public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
			countLine();
		}

		@Override
		public void draw(GShape shape) {
			countLine();
		}

		@Override
		public void fill(GShape shape) {
			countLine();
		}

		private void countLine() {
			lineCount++;
			if (lineCount > MAX_LINES) {
				throw new IllegalStateException("Too many grid lines");
			}
		}
	}
}
