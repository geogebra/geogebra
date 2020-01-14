package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
    public void testEquationPropertyIsHidden() {
        getApp().setConfig(new AppConfigGraphing());

        GeoElementFactory factory = getElementFactory();
        GeoLine geoLine = factory.createGeoLine();
        GeoConic parabola = (GeoConic) factory.create("y=xx");
        GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

        ObjectSettingsModel objectSettingsModel = asList(geoLine);
        Assert.assertFalse(objectSettingsModel.hasEquationModeSetting());

        objectSettingsModel = asList(parabola);
        Assert.assertFalse(objectSettingsModel.hasEquationModeSetting());

        objectSettingsModel = asList(hyperbola);
        Assert.assertFalse(objectSettingsModel.hasEquationModeSetting());
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
