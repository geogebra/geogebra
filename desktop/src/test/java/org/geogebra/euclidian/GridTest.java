package org.geogebra.euclidian;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.main.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GridTest {

	private static AppDNoGui app;
	@BeforeClass
	public static void setup(){
		app = CommandsTest.createApp();
	}
	@Test
	public void thereShouldBeGridInSVGExport() {
		EuclidianSettings settings = app.getActiveEuclidianView().getSettings();
		app.getActiveEuclidianView().centerView(
				new GeoPoint(app.getKernel().getConstruction(), 0, 0, 1));
		settings.setGridColor(GColor.BLUE);
		settings.showGrid(true);
		app.getActiveEuclidianView().updateBackground();
		hasBlueLines(143);
		settings.setPositiveAxis(0, true);
		hasBlueLines(102);
		settings.setPositiveAxis(1, true);
		hasBlueLines(72);
		settings.setPositiveAxis(0, false);
		hasBlueLines(113);
	}

	private void hasBlueLines(int i) {
		ByteArrayOutputStream ss = new ByteArrayOutputStream();
		String svg = "";
		GraphicExportDialog.exportSVG(app,
				app.getActiveEuclidianView(), ss, false,
				800, 600, 8, 6, 1, false);
		try {
			svg = new String(ss.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int start = 0;
		int lines = 0;
		while (svg.indexOf("#0000ff", start) > 0) {
			start = svg.indexOf("#0000ff", start) + 2;
			lines++;
		}
		Assert.assertEquals(i, lines);
	}
}
