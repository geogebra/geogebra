package org.geogebra.export;

import java.util.ArrayList;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.ExportFrameMinimal;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.desktop.export.pstricks.GeoGebraToAsymptoteD;
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

	private static void createObjects() {
		t("(1,1)");
		t("x=y");
		t("x>y");
		t("sin(x)");
		t("4xx+9yy=16");
	}

	private static String generate(GeoGebraExport ps) {
		EuclidianView ev = app.getActiveEuclidianView();
		ExportFrameMinimal frame = new ExportFrameMinimal(ev.getYmin(),
				ev.getYmax());
		ps.setFrame(frame);
		ps.generateAllCode();
		return frame.getCode();

	}
}
