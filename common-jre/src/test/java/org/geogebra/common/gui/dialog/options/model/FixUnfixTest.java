package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
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
		GeoLine line = (GeoLine) add("y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
		Assert.assertTrue(line.isLocked());
	}

	@Test
	public void testDefaultFixForFunctionGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		Assert.assertFalse(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
		Assert.assertTrue(line.isLocked());
	}

	@Test
	public void testUnfixForFunctionGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");

		function.setFixed(false);
		conic.setFixed(false);
		line.setFixed(false);

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
		Assert.assertTrue(line.isLocked());
	}

	@Test
	public void testUnfixForFunctionGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");

		function.setFixed(false);
		conic.setFixed(false);
		line.setFixed(false);

		Assert.assertFalse(function.isLocked());
		Assert.assertFalse(conic.isLocked());
		Assert.assertFalse(line.isLocked());
	}

	@Test
	public void testFixHiddenGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");

		ObjectSettingsModel model = new ObjectSettingsModel(getApp()) {
		};

		model.setGeoElement(function);
		Assert.assertFalse(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(conic);
		Assert.assertFalse(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(line);
		Assert.assertFalse(model.hasFixUnfixFunctionProperty());
	}

	@Test
	public void testFixHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");

		ObjectSettingsModel model = new ObjectSettingsModel(getApp()) {
		};

		model.setGeoElement(function);
		Assert.assertTrue(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(conic);
		Assert.assertTrue(model.hasFixUnfixFunctionProperty());

		model.setGeoElement(line);
		Assert.assertTrue(model.hasFixUnfixFunctionProperty());
	}

	@Test
	public void testFixedPropertyFunctionInGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		Assert.assertTrue(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");
		FixObjectModel fixObjectModel = getModel();
		Object[] geos = new Object[]{function, conic, line};

		fixObjectModel.setGeos(geos);
		fixObjectModel.updateProperties();

		for (int i = 0; i < geos.length; ++i) {
			Assert.assertTrue(fixObjectModel.getValueAt(i));
			Assert.assertFalse(fixObjectModel.isValidAt(i));
		}
	}

	@Test
	public void testFixedPropertyFunctionInGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		Assert.assertFalse(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = (GeoFunction) add("f(x) = x+1");
		GeoConic conic = (GeoConic) add("x*x+y*y=5");
		GeoLine line = (GeoLine) add("y=5");
		FixObjectModel fixObjectModel = getModel();
		Object[] geos = new Object[]{function, conic, line};

		fixObjectModel.setGeos(geos);
		fixObjectModel.updateProperties();

		for (int i = 0; i < geos.length; ++i) {
			Assert.assertTrue(fixObjectModel.getValueAt(i));
			Assert.assertTrue(fixObjectModel.isValidAt(i));
		}
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
