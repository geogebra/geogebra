package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawImplicitCurve;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.junit.Ignore;
import org.junit.Test;

public class DrawImplicitCurveTest extends BaseUnitTest {

	public static final String REFERENCE_FILE = "src/test/resources/implicitPath.txt";
	private static final boolean SAVE_REFERENCE = false;

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
		drawImplicitCurve.draw(view.getGraphicsForPen());
		if (SAVE_REFERENCE) {
			saveLog(plotterMock);
			return;
		}

		try {
			String expected = load().trim();
			assertEquals(expected, plotterMock.result());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void saveLog(PathPlotterMock plotterMock) {
		try (PrintWriter out = new PrintWriter(REFERENCE_FILE)) {
			out.println(plotterMock.result());
		} catch (FileNotFoundException e) {
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
		drawImplicitCurve.draw(view.getGraphicsForPen());
		fail();
	}
}
