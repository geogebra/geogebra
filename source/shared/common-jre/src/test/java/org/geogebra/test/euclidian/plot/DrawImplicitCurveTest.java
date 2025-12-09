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

package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawImplicitCurve;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.junit.Ignore;
import org.junit.Test;

public class DrawImplicitCurveTest extends BaseUnitTest {

	public static final String REFERENCE_FILE = "src/test/resources/implicitPath.txt";
	private static final boolean SAVE_REFERENCE = false;
	private final GGraphics2D graphics2D = new GGraphicsCommon();

	@Test
	public void testImplicitCurvesPlotTheSame() {
		GeoImplicit geo = add("sin(x+y)-cos(x y)+1=0");
		final EuclidianView view = getApp().getActiveEuclidianView();
		final PathPlotterMock plotterMock = new PathPlotterMock();

		DrawImplicitCurve drawImplicitCurve = new DrawImplicitCurve(view, geo) {
			@Override
			protected GeneralPathClippedForCurvePlotter newGeneralPath() {
				GeneralPathClippedForCurvePlotterMock curvePlotterMock =
						new GeneralPathClippedForCurvePlotterMock(view, plotterMock);
				curvePlotterMock.setDelimiter("\n");
				return curvePlotterMock;
			}
		};
		geo.setEuclidianVisible(true);
		drawImplicitCurve.update();
		drawImplicitCurve.draw(graphics2D);
		if (SAVE_REFERENCE) {
			saveLog(plotterMock);
			return;
		}

		try {
			String expected = load().trim().replaceAll("\r", "");
			assertEquals(expected, plotterMock.result());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void saveLog(PathPlotterMock plotterMock) {
		try (PrintWriter out = new PrintWriter(REFERENCE_FILE, StandardCharsets.UTF_8)) {
			out.println(plotterMock.result());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String load() throws IOException {
		Path filePath = Paths.get(REFERENCE_FILE);
		return Files.readString(filePath);
	}

	@Ignore
	@Test
	public void testDrawDiff() {
		GeoImplicit geo = add("(x^2 + y^2 -1) (x^2 +y^2 - 4) +1=0");
		final EuclidianView view = getApp().getActiveEuclidianView();

		DrawImplicitCurve drawImplicitCurve = new DrawImplicitCurve(view, geo);
		geo.setEuclidianVisible(true);
		drawImplicitCurve.update();
		drawImplicitCurve.draw(graphics2D);
		fail();
	}
}
