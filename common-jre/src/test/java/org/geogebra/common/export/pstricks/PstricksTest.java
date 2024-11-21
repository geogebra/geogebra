package org.geogebra.common.export.pstricks;

import java.util.ArrayList;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.AppCommon3D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PstricksTest {
	private static AppCommon3D app;
	private static ArrayList<String> inputs;
	private final ExportGraphicsFactory exportGraphicsFactory
			= ExportGraphicsCommon::new;

	@Before
	public void clear() {
		app.getKernel().clearConstruction(true);
		t("ShowAxes(false)");
		t("ShowGrid(false)");
	}

	/** Set up the app */
	@BeforeClass
	public static void setUp() {
		app = AppCommonFactory.create3D();
		inputs = new ArrayList<>();
		createObjects();
	}

	private static void add(String cmd) {
		inputs.add(cmd);
	}

	private static void t(String cmd) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(cmd, false);
	}

	@Test
	public void exportPstricks() {
		GeoGebraExport ps = new GeoGebraToPstricks(app, exportGraphicsFactory);
		testInputs(ps, "\\end{document}");
	}

	@Test
	public void exportPgf() {
		GeoGebraExport ps = new GeoGebraToPgf(app, exportGraphicsFactory);
		testInputs(ps, "\\end{document}");

	}

	@Test
	public void exportAsymptote() {
		GeoGebraExport ps = new GeoGebraToAsymptote(app, exportGraphicsFactory);
		testInputs(ps, "/* end of picture */");
	}

	private void testInputs(GeoGebraExport ps, String string) {
		String last = "";
		EuclidianView ev = app.getActiveEuclidianView();

		ExportFrameMinimal frame = new ExportFrameMinimal(ev.getYmin(),
				ev.getYmax());
		frame.setKeepColor();
		GeoElement slider = app.getKernel().lookupLabel("anim");
		if (slider instanceof GeoNumeric) {
			frame.setSlider((GeoNumeric) slider);
		}
		ps.setFrame(frame);
		for (String cmd : inputs) {
			t(cmd);
			String out = generate(ps, frame);
			if (out.equals(last)) {
				Assert.fail(cmd);
			}
			last = out;
			Assert.assertEquals(string,
					out.substring(out.length() - string.length()));
		}
	}

	private static void createObjects() {
		add("A=(1,1)");
		add("f:x=y");
		add("g:x>y");
		add("h(x)=sin(x)");
		add("c:4xx+9yy=16");
		add("c1:4xx-9yy=16");
		add("c2:4xx-9yy=0");
		add("c3:4xx+9y=16");
		add("c4:4xx=0");
		add("c5:xx+yy=16");
		add("v:Vector((0,0),(1,1))");
		add("cx:4xx+9yy<16");
		add("s:Segment((1,1),(1,2))");
		add("slider:Slider(0,1)");
		add("Polyline((0,0),(1,1),(2,3))");
		add("Polygon((3,1),(1,1),(2,3))");
		add("\"GeoGebra Rocks\"");
		add("FormulaText(sqrt(x/(x+1)))");
		add("hg:Histogram({1,2,3,4,5,6},{1,2,3,4,3,3,5})");
		add("ShowAxes(true)");
		add("ShowGrid(true)");
		add("DataFunction[{1,2,3},{1,4,2}]");
		add("Root(x^3-x)");
		add("CircleArc((0,0),(0,1),(1,0))");
		add("SetColor(A,\"BLUE\")");
		add("SetColor(f,\"YELLOW\")");
		add("SetColor(g,\"RED\")");
		add("SetColor(h,\"GREEN\")");
		add("SetColor(hg,\"BLACK\")");
		add("Ray((0,0),(1,1))");
		add("Integral(2x,0,1)");
		add("Integral(-x,2x,0,1)");
		add("BoxPlot(1,1,{-1,1,2,3,4,9,9})");
		add("LowerSum(x^2,0,1,10)");
		add("UpperSum(x^2,0,1,10)");
		add("TrapezoidalSum(x^2,0,1,10)");
		add("RectangleSum(x^2,0,1,10,0.1)");
		add("Slope(x)");
		add("Angle(A)");
		add("Angle(Vector(A))");
		add("Angle(xAxis, yAxis)");
		add("sp:Spline((0,0),(0,1),(1,1),(1,0),(0,0))");
		add("SetDynamicColor(sp, 1, 0, 0,1)");
		for (int i = 1; i < 9; i++) {
			add("Pt" + i + "=(0," + i + ")");
			add("SetPointStyle(Pt" + i + "," + i + ")");
		}
		for (int i = 1; i < 5; i++) {
			add("l" + i + ":x=" + i);
			add("SetLineStyle(l" + i + "," + i + ")");
		}
	}

	private static String generate(GeoGebraExport ps,
			ExportFrameMinimal frame) {
		ps.generateAllCode();
		return frame.getCode();

	}

	private static class ExportGraphicsCommon extends GGraphicsCommon {
		private final GeoGebraExport export;
		private final Inequality inequality;
		private final FunctionalNVar geo;

		public ExportGraphicsCommon(FunctionalNVar geo, Inequality inequality,
				GeoGebraExport export) {
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
