package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
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
    public void testHideOutputRowGraphing() {
        getApp().setConfig(new AppConfigGraphing());
        GeoRay ray = (GeoRay) getElementFactory().create("g:Ray((0,0),(1,1))");

        Assert.assertTrue(AlgebraItem.shouldShowOnlyDefinitionForGeo(ray));
        Assert.assertEquals(ray.needToShowBothRowsInAV(), DescriptionMode.DEFINITION);
    }

    @Test
    public void testShowOutputRowGeometry() {
        getApp().setConfig(new AppConfigGeometry());
        GeoRay ray = (GeoRay) getElementFactory().create("g:Ray((0,0),(1,1))");

        Assert.assertFalse(AlgebraItem.shouldShowOnlyDefinitionForGeo(ray));
        Assert.assertEquals(ray.needToShowBothRowsInAV(), DescriptionMode.DEFINITION_VALUE);
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
