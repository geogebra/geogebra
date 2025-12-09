/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.dialog.options.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.gui.dialog.options.model.NameValueModel.INameValueListener;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.TestErrorHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NameModelTest {

	private static AppCommon app;
	private static NameValueModel model;

	/**
	 * Create app and model.
	 */
	@BeforeClass
	public static void setup() {
		app = AppCommonFactory.create3D();
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
	public void labelChangeShouldNotChangeCaption() {
		GeoPoint p = makePoint("P");
		model.setGeos(new Object[] { p });
		model.updateProperties();
		model.applyNameChange("Q", TestErrorHandler.INSTANCE);
		assertEquals("Q", p.getLabelSimple());
		assertNull(p.getCaptionSimple());
	}

	@Test
	public void labelChangeToSameShouldHaveNoEffect() {
		GeoPoint p = makePoint("P");
		model.setGeos(new Object[] { p });
		model.updateProperties();
		model.applyNameChange("P", TestErrorHandler.INSTANCE);
		assertNull(p.getCaptionSimple());
		model.applyNameChange("Q", TestErrorHandler.INSTANCE);
		assertEquals("Q", p.getLabelSimple());
	}

	@Test
	public void labelCollisionShouldChangeCaption() {
		makePoint("P");
		GeoPoint r = makePoint("R");

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("P", TestErrorHandler.INSTANCE);
		assertEquals("R", r.getLabelSimple());

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("T", TestErrorHandler.INSTANCE);
		assertEquals("R", r.getLabelSimple());
	}

	@Test
	public void invalidLabelShouldSetCaption() {
		GeoPoint r = makePoint("R");

		model.setGeos(new Object[] { r });
		model.updateProperties();
		model.applyNameChange("P Q", TestErrorHandler.INSTANCE);
		assertEquals("R", r.getLabelSimple());
		assertEquals("P Q",
				r.getCaption(StringTemplate.defaultTemplate));
		model.applyNameChange("!!!", TestErrorHandler.INSTANCE);
		assertEquals("R", r.getLabelSimple());
		assertEquals("!!!",
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
