package org.geogebra.export;

import java.util.ArrayList;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.ExportFrameMinimal;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.desktop.export.pstricks.GeoGebraToAsymptoteD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPdfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPgfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.euclidian.TestEvent;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Pstricks {
	private static AppDNoGui app;
	private static EuclidianController ec;
	private static ArrayList<TestEvent> events = new ArrayList<>();

	@BeforeClass
	public static void setup() {
		app = CommandsTest.createApp();
		ec = app.getActiveEuclidianView().getEuclidianController();
	}

	private static void t(String cmd) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(cmd, false);
	}

	@Test
	public void exportPstricks() {
		createObjects();
		GeoGebraExport ps = new GeoGebraToPstricksD(app);
		String out = generate(ps);
		Assert.assertEquals("\\end{document}",
				out.substring(out.length() - 14));
	}

	@Test
	public void exportPgf() {
		createObjects();
		GeoGebraExport ps = new GeoGebraToPgfD(app);
		String out = generate(ps);
		Assert.assertEquals("\\end{document}",
				out.substring(out.length() - 14));

	}

	@Test
	public void exportAsymptote() {
		createObjects();
		GeoGebraExport ps = new GeoGebraToAsymptoteD(app);
		String out = generate(ps);
		Assert.assertEquals("/* end of picture */",
				out.substring(out.length() - 20));
	}

	@Test
	public void exportPdf() {
		createObjects();
		GeoGebraExport ps = new GeoGebraToPdfD(app);
		String out = generate(ps);
		Assert.assertEquals("\\end{document}",
				out.substring(out.length() - 14));
	}

	private static void createObjects() {
		t("A=(1,1)");
		t("f:x=y");
		t("g:x>y");
		t("h(x)=sin(x)");
		t("c:4xx+9yy=16");
		t("cx:4xx+9yy<16");
		t("slider:Slider(0,1)");
		t("Polyline((0,0),(1,1),(2,3))");
		t("Polygon((3,1),(1,1),(2,3))");
		t("\"GeoGebra Rocks\"");
		t("FormulaText(sqrt(x/(x+1)))");
		t("ShowAxes(true)");
		t("ShowGrid(true)");
		t("SetColor(a,\"BLUE\")");
		t("SetColor(f,\"YELLOW\")");
		t("SetColor(g,\"RED\")");
		t("SetColor(h,\"GREEN\")");
		t("SetColor(c,\"BLACK\")");
		t("Angle(A)");
		t("Angle(xAxis, yAxis)");
		for (int i = 0; i < 9; i++) {
			t("Pt"+i+"=(0,"+i+")");
			t("SetPointStyle(Pt" + i + "," + i + ")");
		}
	}

	private static String generate(GeoGebraExport ps) {
		EuclidianView ev = app.getActiveEuclidianView();
		ExportFrameMinimal frame = new ExportFrameMinimal(ev.getYmin(),
				ev.getYmax());
		GeoElement slider = app.getKernel().lookupLabel("slider");
		if (slider instanceof GeoNumeric) {
			frame.setSlider((GeoNumeric) slider);
		}
		ps.setFrame(frame);
		ps.generateAllCode();
		return frame.getCode();

	}
}
