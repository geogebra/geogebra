package org.geogebra.common.gui.dialog.options.model;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.properties.impl.objects.IsFixedObjectProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Assert;
import org.junit.Test;

public class FixUnfixTest extends BaseUnitTest {

	@Test
	public void testDefaultFixForFunctionGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		Assert.assertTrue(getApp().getConfig().isObjectDraggingRestricted());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
		Assert.assertTrue(line.isLocked());
	}

	@Test
	public void testDefaultFixForFunctionGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		Assert.assertFalse(getApp().getSettings().getAlgebra().isEquationChangeByDragRestricted());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

		Assert.assertTrue(function.isLocked());
		Assert.assertTrue(conic.isLocked());
		Assert.assertTrue(line.isLocked());
	}

	@Test
	public void testUnfixForFunctionGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

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

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

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

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

		GeoElement[] geos = new GeoElement[]{function, conic, line};

		for (GeoElement geo : geos) {
			assertThrows(NotApplicablePropertyException.class,
					() -> new IsFixedObjectProperty(getLocalization(), geo));
		}
	}

	@Test
	public void testFixHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");

		try {
			new IsFixedObjectProperty(getLocalization(), function);
			new IsFixedObjectProperty(getLocalization(), conic);
			new IsFixedObjectProperty(getLocalization(), line);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFixedPropertyFunctionInGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		Assert.assertTrue(getApp().getSettings().getAlgebra().isEquationChangeByDragRestricted());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");
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
		Assert.assertFalse(getApp().getSettings().getAlgebra().isEquationChangeByDragRestricted());

		GeoFunction function = add("f(x) = x+1");
		GeoConic conic = add("x*x+y*y=5");
		GeoLine line = add("y=5");
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
				// stub
			}

			@Override
			public Object updatePanel(Object[] geos2) {
				return null;
			}
		}, getApp());
	}

}
