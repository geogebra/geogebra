package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

public class FitCommandTests extends BaseUnitTest {

	@Test
	public void testEquationPropertyVisibilityGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElement[] geos = getFitCommandGeoElements();

		for (GeoElement geo : geos) {
			ObjectSettingsModel objectSettingsModel = asList(geo);

			Assert.assertFalse(objectSettingsModel.hasEquationModeSetting());
			Assert.assertTrue(LineEqnModel.forceInputForm(getApp(), geo));
		}
	}

	@Test
	public void testEquationPropertyVisibilityGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElement[] geos = getFitCommandGeoElements();

		for (GeoElement geo : geos) {
			ObjectSettingsModel objectSettingsModel = asList(geo);

			Assert.assertTrue(objectSettingsModel.hasEquationModeSetting());
			Assert.assertFalse(LineEqnModel.forceInputForm(getApp(), geo));
		}
	}

	private GeoElement[] getFitCommandGeoElements() {
		GeoElementFactory factory = getElementFactory();
		GeoLine fitLine = (GeoLine) factory.create("FitLine({(-1,-1),(0,1),(1,1),(2,5)})");
		GeoLine fitLineX = (GeoLine) factory.create("FitLineX({(-1,3),(2,1),(3,4),(5,3),(6,5)})");

		return new GeoElement[]{fitLine, fitLineX};
	}

	private ObjectSettingsModel asList(GeoElement f) {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(f);
		ObjectSettingsModel model = new ObjectSettingsModel(getApp()) {
		};
		model.setGeoElement(f);
		model.setGeoElementsList(list);
		return model;
	}
}
