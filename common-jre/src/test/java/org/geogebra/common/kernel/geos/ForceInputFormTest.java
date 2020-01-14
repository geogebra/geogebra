package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
    public void testEquationPropertyIsHidden() {
        getApp().setConfig(new AppConfigGraphing());

        GeoLine geoLine = getElementFactory().createGeoLine();
        GeoElementFactory factory = getElementFactory();
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
