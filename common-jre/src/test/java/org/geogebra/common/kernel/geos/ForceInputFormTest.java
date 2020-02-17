package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

public class ForceInputFormTest extends BaseUnitTest {

    @Test
    public void testLinesConicsRaysToStringModeInGraphing() {
        getApp().setConfig(new AppConfigGraphing());

        GeoElementFactory factory = getElementFactory();
        GeoLine geoLine = factory.createGeoLine();
        GeoConic parabola = (GeoConic) factory.create("y=xx");
        GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
        GeoRay geoRay = factory.createGeoRay();

        Assert.assertEquals(GeoLine.EQUATION_USER, geoLine.getToStringMode());
        Assert.assertEquals(GeoConic.EQUATION_USER, parabola.getToStringMode());
        Assert.assertEquals(GeoConic.EQUATION_USER, hyperbola.getToStringMode());
        Assert.assertEquals(GeoRay.EQUATION_USER, geoRay.getToStringMode());
    }

    @Test
    public void testLinesConicsRaysToStringModeInGeometry() {
        getApp().setConfig(new AppConfigGeometry());

        GeoElementFactory factory = getElementFactory();
        GeoLine geoLine = factory.createGeoLine();
        GeoConic parabola = (GeoConic) factory.create("y=xx");
        GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
        GeoRay geoRay = factory.createGeoRay();

        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, geoLine.getToStringMode());
        Assert.assertEquals(GeoConic.EQUATION_EXPLICIT, parabola.getToStringMode());
        Assert.assertEquals(GeoConic.EQUATION_IMPLICIT, hyperbola.getToStringMode());
        Assert.assertEquals(GeoRay.EQUATION_IMPLICIT_NON_CANONICAL, geoRay.getToStringMode());
    }

    @Test
    public void testHideOutputRowGraphing() {
    	getApp().setGraphingConfig();
        GeoRay ray = getElementFactory().createGeoRay();

        Assert.assertFalse(ray.isAllowedToShowValue());
        Assert.assertEquals(DescriptionMode.DEFINITION, ray.getDescriptionMode());
    }

    @Test
    public void testShowOutputRowGeometry() {
        getApp().setConfig(new AppConfigGeometry());
		GeoRay ray = getElementFactory().createGeoRay();

        Assert.assertTrue(ray.isAllowedToShowValue());
        Assert.assertEquals(ray.getDescriptionMode(), DescriptionMode.DEFINITION_VALUE);
    }

	@Test
	public void testEquationPropertyIsHiddenGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = factory.createGeoLine();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
		GeoRay ray = getElementFactory().createGeoRay();

		GeoElement[] geos = new GeoElement[]{geoLine, parabola, hyperbola, ray};

		for (GeoElement geo : geos) {
			ObjectSettingsModel objectSettingsModel = asList(geo);

			Assert.assertFalse(objectSettingsModel.hasEquationModeSetting());
			Assert.assertTrue(LineEqnModel.forceInputForm(getApp(), geo));
		}
	}

	@Test
	public void testEquationPropertyIsHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = (GeoLine) factory.create("Line((0,0),(1,1))");
		GeoRay ray = getElementFactory().createGeoRay();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		ObjectSettingsModel objectSettingsModel = asList(geoLine);
		Assert.assertTrue(objectSettingsModel.hasEquationModeSetting());

		objectSettingsModel = asList(ray);
		Assert.assertTrue(objectSettingsModel.hasEquationModeSetting());

		GeoElement[] geos = new GeoElement[]{geoLine, ray, parabola, hyperbola};

		for (GeoElement geo : geos) {
			Assert.assertFalse(LineEqnModel.forceInputForm(getApp(), geo));
		}
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
