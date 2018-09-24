package org.geogebra.common.kernel.geos;

import org.geogebra.commands.AlgebraTest;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppDNoGui;
import org.junit.Assert;
import org.junit.Test;

public class AuralTextTest {

	static AppDNoGui app = AlgebraTest.createApp();

	private static void aural(String in, String... out) {
		GeoElementND[] geos = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(in, true);
		Log.debug(geos[0].getAuralText());
		String[] sentences = geos[0].getAuralText().split("\\.");
		Assert.assertTrue(geos[0].getAuralText().endsWith("."));
		Assert.assertEquals(out.length, sentences.length);
		for (int i = 0; i < out.length; i++) {
			Assert.assertTrue(sentences[i].matches(".*" + out[i] + ".*"));
		}
	}

	@Test
	public void pointAural() {
		aural("(1,1)", "Point", "arrow", "edit");
		aural("Point(xAxis)", "Point", "plus and minus", "edit");
	}

	@Test
	public void point3DAural() {
		aural("(1,1,1)", "Point", "arrow", "edit");
		aural("Point(zAxis)", "Point", "plus and minus", "edit");
	}

	@Test
	public void numberAural() {
		aural("Slider(-5,5)", "Slider", "start animation", "increase",
				"decrease", "edit");
		aural("4"); // TODO should not be empty when tabbing in AV
	}

	@Test
	public void checkboxAural() {
		aural("checkbox()", "Checkbox", "uncheck", "edit");
		aural("false", "Checkbox", " check", "edit");
	}
}
