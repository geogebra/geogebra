package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

public class ForceInputFormTest extends BaseUnitTest {

    @Test
    public void testLinesAndConicsToStringMode() {
        getApp().setConfig(new AppConfigGraphing());

        GeoLine geoLine = getElementFactory().createGeoLine();
        GeoElementFactory factory = getElementFactory();
        GeoConic parabola = (GeoConic) factory.create("y=xx");
        GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

        Assert.assertEquals(geoLine.getToStringMode(), GeoLine.EQUATION_USER);
        Assert.assertEquals(parabola.getToStringMode(), GeoConic.EQUATION_USER);
        Assert.assertEquals(hyperbola.getToStringMode(), GeoConic.EQUATION_USER);
    }

    @Test
    public void testEquationPropertyIsHiddenGraphing() {
        getApp().setConfig(new AppConfigGraphing());
        getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

        GeoElementFactory factory = getElementFactory();
        GeoLine geoLine = factory.createGeoLine();
        GeoConic parabola = (GeoConic) factory.create("y=xx");
        GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
        GeoRay ray = (GeoRay) factory.create("g:Ray((0,0),(1,1))");

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
        GeoLine geoLine = (GeoLine) factory.create("f:Line((0,0),(1,1))");
        GeoRay ray = (GeoRay) factory.create("g:Ray((0,0),(1,1))");
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
