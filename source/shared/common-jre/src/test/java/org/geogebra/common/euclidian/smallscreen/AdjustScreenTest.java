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

package org.geogebra.common.euclidian.smallscreen;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.smallscreen.AdjustScreen.CoordinateSystem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/** Tests fitting saved coordinate systems to different viewport sizes. */
@Issue("MOW-1899")
class AdjustScreenTest extends BaseAppTestSetup {

	@ParameterizedTest(name = "{0}")
	@MethodSource("viewportCases")
	void testFitToViewport(
			String scenario,
			CoordinateSystem source,
			int sourceWidth,
			int sourceHeight,
			int targetWidth,
			int targetHeight,
			CoordinateSystem expected) {
		CoordinateSystem actual =
				AdjustScreen.fitToViewport(source, sourceWidth, sourceHeight, targetWidth, targetHeight);
		assertAll(
				scenario,
				() -> assertEquals(expected.xZero(), actual.xZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.yZero(), actual.yZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.xScale(), actual.xScale(), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.yScale(), actual.yScale(), Kernel.MAX_PRECISION));
	}

	private static Stream<Arguments> viewportCases() {
		return Stream.of(
				Arguments.of(
						"width limited with a noncentral origin",
						new CoordinateSystem(900, 600, 1, 1),
						1200,
						800,
						800,
						600,
						new CoordinateSystem(600, 1300.0 / 3, 2.0 / 3, 2.0 / 3)),
				Arguments.of(
						"height limited when returning to the original viewport size",
						new CoordinateSystem(600, 1300.0 / 3, 2.0 / 3, 2.0 / 3),
						800,
						600,
						1200,
						800,
						new CoordinateSystem(2600.0 / 3, 5200.0 / 9, 8.0 / 9, 8.0 / 9)),
				Arguments.of(
						"width limited with unequal axis scales",
						new CoordinateSystem(900, 600, 0.4, 0.3),
						1200,
						800,
						800,
						600,
						new CoordinateSystem(600, 1300.0 / 3, 4.0 / 15, 0.2)),
				Arguments.of(
						"landscape to portrait with vertical spare space",
						new CoordinateSystem(900, 450, 40, 60),
						1200,
						600,
						600,
						900,
						new CoordinateSystem(450, 525, 20, 30)),
				Arguments.of(
						"portrait to landscape with horizontal spare space",
						new CoordinateSystem(450, 675, 40, 60),
						600,
						900,
						1200,
						600,
						new CoordinateSystem(700, 450, 80.0 / 3, 40)),
				Arguments.of(
						"same size preserves a panned origin and unequal scales",
						new CoordinateSystem(-150, 900, 40, 60),
						1200,
						600,
						1200,
						600,
						new CoordinateSystem(-150, 900, 40, 60)),
				Arguments.of(
						"same aspect ratio shrinks without centering offsets",
						new CoordinateSystem(-200, 900, 40, 60),
						1200,
						600,
						600,
						300,
						new CoordinateSystem(-100, 450, 20, 30)),
				Arguments.of(
						"same aspect ratio enlarges without centering offsets",
						new CoordinateSystem(-100, 450, 20, 30),
						600,
						300,
						1200,
						600,
						new CoordinateSystem(-200, 900, 40, 60)),
				Arguments.of(
						"larger viewport enlarges and centers with a different aspect ratio",
						new CoordinateSystem(-150, 1200, 40, 60),
						600,
						900,
						1800,
						1800,
						new CoordinateSystem(0, 2400, 80, 120)));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("viewportCases")
	void testAdjustSavedViews(
			String scenario,
			CoordinateSystem source,
			int sourceWidth,
			int sourceHeight,
			int targetWidth,
			int targetHeight,
			CoordinateSystem expected) {
		setupNotesApp();
		EuclidianSettings settings = getEuclidianSettings();
		settings.setSizeFromFile(AwtFactory.getPrototype().newDimension(sourceWidth, sourceHeight));
		settings.setFileCoordSystem(source.xZero(), source.yZero(), source.xScale(), source.yScale());
		EuclidianView view = getApp().getEuclidianView1();

		// Set CoordSystem just to differ from saved values
		view.setCoordSystem(-123, 987, 17, 31);
		view.setPreferredSize(AwtFactory.getPrototype().newDimension(targetWidth, targetHeight));
		AdjustScreen.adjustCoordSystem(view);
		assertAll(
				() -> assertEquals(expected.xZero(), view.getXZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.yZero(), view.getYZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.xScale(), view.getScale(0), Kernel.MAX_PRECISION),
				() -> assertEquals(expected.yScale(), view.getScale(1), Kernel.MAX_PRECISION));
	}

	@ParameterizedTest
	@CsvSource({"0, 600", "800, 0", "0, 0", "-1, 600", "800, -1"})
	void adjustCoordSystemPreservesCurrentViewWhenSavedSizeIsInvalid(int width, int height) {
		setupNotesApp();
		EuclidianSettings settings = getEuclidianSettings();
		settings.setFileCoordSystem(12, 44, 0.5, 0.5);
		settings.setSizeFromFile(AwtFactory.getPrototype().newDimension(width, height));
		EuclidianView view = getApp().getEuclidianView1();
		view.setPreferredSize(AwtFactory.getPrototype().newDimension(800, 600));

		// Set CoordSystem just to differ from saved values
		view.setCoordSystem(-123, 987, 17, 31);
		AdjustScreen.adjustCoordSystem(view);
		assertAll(
				() -> assertEquals(-123, view.getXZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(987, view.getYZero(), Kernel.MAX_PRECISION),
				() -> assertEquals(17, view.getScale(0), Kernel.MAX_PRECISION),
				() -> assertEquals(31, view.getScale(1), Kernel.MAX_PRECISION));
	}
}
