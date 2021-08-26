package org.geogebra.euclidian;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Grid test
 * 
 * @author Zbynek
 *
 */
public class GridTest {

	private static AppDNoGui app;

	/**
	 * Create test app
	 */
	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
	}

	/**
	 * Checks the right number of gridlines in EV for 800x600 view and 50px
	 * scale
	 */
	@Test
	public void thereShouldBeGridInSVGExport() {
		EuclidianSettings settings = app.getActiveEuclidianView().getSettings();
		app.getActiveEuclidianView().centerView(
				new GeoPoint(app.getKernel().getConstruction(), 0, 0, 1));
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

	private static void hasBlueLines(int expectMinor, int expectMajor) {
		ByteArrayOutputStream ss = new ByteArrayOutputStream();
		String svg = "";
		GraphicExportDialog.exportSVG(app, app.getActiveEuclidianView(), ss,
				false, 800, 600, 8, 6, 1, false);
		try {
			svg = new String(ss.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Assert.assertEquals(expectMinor, minor);
		Assert.assertEquals(expectMajor, major);
	}
}
