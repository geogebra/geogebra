package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

public class FixUnfixTest extends BaseUnitTest {

	@Test
	public void testDefaultFixForFunctionGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		Assert.assertTrue(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
	}

	@Test
	public void testDefaultFixForFunctionGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		Assert.assertFalse(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
	}

}
