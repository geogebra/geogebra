package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FixObjectModelTest {

	private static AppDNoGui app;
	private static FixObjectModel model;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		model = new FixObjectModel(new BooleanOptionModel.IBooleanOptionListener() {
			@Override
			public void updateCheckbox(boolean isEqual) {

			}

			@Override
			public Object updatePanel(Object[] geos2) {
				return null;
			}
		}, app);
	}

	@Test
	public void testFixedPropertyFunctionInGraphing() {
		app.setConfig(new AppConfigGraphing());
		Assert.assertTrue(app.getConfig().isObjectDraggingRestricted());

		GeoFunction function = makeFunction();

		model.setGeos(new Object[]{function});
		model.updateProperties();

		Assert.assertTrue(model.getValueAt(0));
		Assert.assertFalse(model.isValidAt(0));
	}

	@Test
	public void testFixedPropertyFunctionInGeometry() {
		app.setConfig(new AppConfigGeometry());
		Assert.assertFalse(app.getConfig().isObjectDraggingRestricted());

		GeoFunction function = makeFunction();

		model.setGeos(new Object[]{function});
		model.updateProperties();

		Assert.assertTrue(model.getValueAt(0));
		Assert.assertTrue(model.isValidAt(0));
	}

	private GeoFunction makeFunction() {
		return new GeoFunction(app.getKernel().getConstruction());
	}
}
