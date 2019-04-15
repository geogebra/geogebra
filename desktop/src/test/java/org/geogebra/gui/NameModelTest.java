package org.geogebra.gui;

import org.geogebra.common.gui.dialog.options.model.NameValueModel;
import org.geogebra.common.gui.dialog.options.model.NameValueModel.INameValueListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.commands.TestErrorHandler;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NameModelTest {

	private static AppDNoGui app;
	private static NameValueModel model;

	/**
	 * Create app and model.
	 */
	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		model = new NameValueModel(app, new INameValueListener() {

			@Override
			public void setNameText(String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setDefinitionText(String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setCaptionText(String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void updateGUI(boolean showDefinition, boolean showCaption) {
				// TODO Auto-generated method stub

			}

			@Override
			public void updateDefLabel() {
				// TODO Auto-generated method stub

			}

			@Override
			public void updateCaption(String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void updateName(String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object updatePanel(Object[] geos2) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void update(boolean isEqualVal, boolean isEqualMode,
					int mode) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Test
	public void labelChangeShoundNotChangeCaption() {
		GeoPoint p = makePoint("P");
		model.setGeos(new Object[] { p });
		model.updateProperties();
		model.applyNameChange("Q", new TestErrorHandler());
		Assert.assertEquals("Q", p.getLabelSimple());
		Assert.assertEquals(null, p.getCaptionSimple());
	}

	@Test
	public void labelChangeToSameShouldHaveNoEffect() {
		GeoPoint p = makePoint("P");
		model.setGeos(new Object[] { p });
		model.updateProperties();
		model.applyNameChange("P", new TestErrorHandler());
		Assert.assertEquals(null, p.getCaptionSimple());
		model.applyNameChange("Q", new TestErrorHandler());
		Assert.assertEquals("Q", p.getLabelSimple());
	}

	@Test
	public void labelCollisionShoundChangeCaption() {
		makePoint("P");
		GeoPoint r = makePoint("R");

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("P", new TestErrorHandler());
		Assert.assertEquals("R", r.getLabelSimple());

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("T", new TestErrorHandler());
		Assert.assertEquals("R", r.getLabelSimple());
	}

	@Test
	public void invalidLabelShouldSetCaption() {
		GeoPoint r = makePoint("R");

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("P Q", new TestErrorHandler());
		Assert.assertEquals("R", r.getLabelSimple());
		Assert.assertEquals("P Q",
				r.getCaption(StringTemplate.defaultTemplate));
		model.applyNameChange("!!!", new TestErrorHandler());
		Assert.assertEquals("R", r.getLabelSimple());
		Assert.assertEquals("!!!",
				r.getCaption(StringTemplate.defaultTemplate));

	}

	@Before
	public void cleanup() {
		app.getKernel().clearConstruction(true);

	}

	private static GeoPoint makePoint(String string) {
		GeoPoint p = new GeoPoint(app.getKernel().getConstruction());
		p.setCoords(1, 0, 1);
		p.setLabel(string);
		return p;
	}

}
