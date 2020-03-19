package org.geogebra.common.kernel.statistics;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.junit.Assert;
import org.junit.Test;

public class FitTest extends BaseUnitTest {

    @Test
    public void testFitLineRectangleSelectionForTwoPoints() {
        EuclidianView view =  getApp().getActiveEuclidianView();
        EuclidianController controller = view.getEuclidianController();

        addAvInput("A = (1,1)");
        addAvInput("B = (2,1)");

        Rectangle rectangle = new Rectangle();
        rectangle.setRect(0,0, view.getWidth(), view.getHeight());

        controller.setMode(EuclidianConstants.MODE_FITLINE, null);
        view.setSelectionRectangle(rectangle);
        controller.processSelectionRectangle(false, false, false);

        GeoElement geo = getConstruction().getLastGeoElement();
        Assert.assertTrue(geo instanceof GeoLine);
    }
}

