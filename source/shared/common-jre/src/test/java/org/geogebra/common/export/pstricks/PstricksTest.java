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

package org.geogebra.common.export.pstricks;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.AutoColor;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.SnapshotChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

class PstricksTest {
	private AppCommon3D app;
	private final ExportGraphicsFactory exportGraphicsFactory = ExportGraphicsCommon::new;
	private final List<String> inputs = List.of(
			"A=(1,1)",
			"f:x=y",
			"g:x>y",
			"h(x)=sin(x)",
			"c:4xx+9yy=16",
			"c1:4xx-9yy=16",
			"c2:4xx-9yy=0",
			"c3:4xx+9y=16",
			"c4:4xx=0",
			"c5:xx+yy=16",
			"v:Vector((0,0),(1,1))",
			"cx:4xx+9yy<16",
			"s:Segment((1,1),(1,2))",
			"slider:Slider(0,1)",
			"Polyline((0,0),(1,1),(2,3))",
			"Polygon((3,1),(1,1),(2,3))",
			"\"GeoGebra Rocks\"",
			"FormulaText(sqrt(x/(x+1)))",
			"hg:Histogram({1,2,3,4,5,6},{1,2,3,4,3,3,5})",
			"ShowAxes(true)",
			"ShowGrid(true)",
			"DataFunction[{1,2,3},{1,4,2}]",
			"Root(x^3-x)",
			"CircleArc((0,0),(0,1),(1,0))",
			"SetColor(A,\"BLUE\")",
			"SetColor(f,\"YELLOW\")",
			"SetColor(g,\"RED\")",
			"SetColor(h,\"GREEN\")",
			"SetColor(hg,\"BLACK\")",
			"Ray((0,0),(1,1))",
			"Integral(2x,0,1)",
			"Integral(-x,2x,0,1)",
			"BoxPlot(1,1,{-1,1,2,3,4,9,9})",
			"LowerSum(x^2,0,1,10)",
			"UpperSum(x^2,0,1,10)",
			"TrapezoidalSum(x^2,0,1,10)",
			"RectangleSum(x^2,0,1,10,0.1)",
			"Slope(x)",
			"Angle(A)",
			"Angle(Vector(A))",
			"Angle(xAxis, yAxis)",
			"sp:Spline((0,0),(0,1),(1,1),(1,0),(0,0))",
			"SetDynamicColor(sp, 1, 0, 0,1)");
	private final List<String> points = IntStream.range(0, 9)
			.boxed()
			.flatMap(i -> Stream.of(
					"Pt" + i + "=(" + (i % 2) + "," + (i / 2) + ")", "SetPointStyle(Pt" + i + "," + i + ")"))
			.toList();
	private final List<String> lines = IntStream.range(0, 5)
			.boxed()
			.flatMap(i -> Stream.of("l" + i + ":x=" + i, "SetLineStyle(l" + i + "," + i + ")"))
			.toList();

	@BeforeEach
	void clear() {
		for (AutoColor color : AutoColor.values()) {
			color.reset();
		}
		app = AppCommonFactory.create3D();
		t("ShowAxes(false)");
		t("ShowGrid(false)");
	}

	private void t(String cmd) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(cmd, false);
	}

	@Test
	void exportObjectsPstricks(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPstricks(app, exportGraphicsFactory);
		testInputs(inputs, ps, "objects-pstricks.tex", reporter);
	}

	@Test
	void exportPointsPstricks(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPstricks(app, exportGraphicsFactory);
		testInputs(points, ps, "points-pstricks.tex", reporter);
	}

	@Test
	void exportLinesPstricks(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPstricks(app, exportGraphicsFactory);
		testInputs(lines, ps, "lines-pstricks.tex", reporter);
	}

	@Test
	void exportObjectsPgf(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPgf(app, exportGraphicsFactory);
		testInputs(inputs, ps, "objects-pgf.tex", reporter);
	}

	@Test
	void exportPointsPgf(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPgf(app, exportGraphicsFactory);
		testInputs(points, ps, "points-pgf.tex", reporter);
	}

	@Test
	void exportLinesPgf(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToPgf(app, exportGraphicsFactory);
		testInputs(lines, ps, "lines-pgf.tex", reporter);
	}

	@Test
	void exportObjectsAsymptote(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToAsymptote(app, exportGraphicsFactory);
		testInputs(inputs, ps, "objects-asymptote.tex", reporter);
	}

	@Test
	void exportPointsAsymptote(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToAsymptote(app, exportGraphicsFactory);
		testInputs(points, ps, "points-asymptote.tex", reporter);
	}

	@Test
	void exportLinesAsymptote(TestReporter reporter) {
		GeoGebraExport ps = new GeoGebraToAsymptote(app, exportGraphicsFactory);
		testInputs(lines, ps, "lines-asymptote.tex", reporter);
	}

	private void testInputs(
			List<String> inputs, GeoGebraExport ps, String string, TestReporter reporter) {
		EuclidianView ev = app.getActiveEuclidianView();
		ExportFrameMinimal frame = new ExportFrameMinimal(ev.getYmin(), ev.getYmax());
		frame.setKeepColor();
		ps.setFrame(frame);
		for (String cmd : inputs) {
			t(cmd);
		}
		String out = generate(ps, frame).trim();
		new SnapshotChecker(reporter, getClass()).assertMatch(string, out);
	}

	private static String generate(GeoGebraExport ps, ExportFrameMinimal frame) {
		ps.generateAllCode();
		return frame.getCode();
	}

	private static class ExportGraphicsCommon extends GGraphicsCommon {
		private final GeoGebraExport export;
		private final Inequality inequality;
		private final FunctionalNVar geo;

		ExportGraphicsCommon(FunctionalNVar geo, Inequality inequality, GeoGebraExport export) {
			this.export = export;
			this.inequality = inequality;
			this.geo = geo;
		}

		@Override
		public void fill(GShape s) {
			export.fillIneq(s, inequality, geo);
		}
	}
}
