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

	@Test
	public void testFixHiddenGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");

		ObjectSettingsModel model = new ObjectSettingsModel(getApp()) {
		};

		model.setGeoElement(function);
		Assert.assertFalse(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(conic);
		Assert.assertFalse(model.hasFixUnfixFunctionProperty());
	}

	@Test
	public void testFixHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");

		ObjectSettingsModel model = new ObjectSettingsModel(getApp()) {
		};

		model.setGeoElement(function);
		Assert.assertTrue(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(conic);
		Assert.assertTrue(model.hasFixUnfixFunctionProperty());
	}

	@Test
	public void testFixedPropertyFunctionInGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		Assert.assertTrue(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		FixObjectModel fixObjectModel = getModel();

		fixObjectModel.setGeos(new Object[]{function, conic});
		fixObjectModel.updateProperties();

		Assert.assertTrue(fixObjectModel.getValueAt(0));
		Assert.assertFalse(fixObjectModel.isValidAt(0));
		Assert.assertTrue(fixObjectModel.getValueAt(1));
		Assert.assertFalse(fixObjectModel.isValidAt(1));
	}

	@Test
	public void testFixedPropertyFunctionInGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		Assert.assertFalse(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		FixObjectModel fixObjectModel = getModel();

		fixObjectModel.setGeos(new Object[]{function, conic});
		fixObjectModel.updateProperties();

		Assert.assertTrue(fixObjectModel.getValueAt(0));
		Assert.assertTrue(fixObjectModel.isValidAt(0));
		Assert.assertTrue(fixObjectModel.getValueAt(1));
		Assert.assertTrue(fixObjectModel.isValidAt(1));
	}

	private FixObjectModel getModel() {
		return new FixObjectModel(new BooleanOptionModel.IBooleanOptionListener() {
			@Override
			public void updateCheckbox(boolean isEqual) {

			}

			@Override
			public Object updatePanel(Object[] geos2) {
				return null;
			}
		}, getApp());
	}

}
