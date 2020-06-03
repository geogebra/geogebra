package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.GeoElementProperty;
import org.junit.Test;

public class GeoElementPropertiesTest extends BaseUnitTest {

    @Test
    public void testPoint() {
        GeoPoint zeroPoint = addAvInput("(0,0)");
        GeoPoint onePoint = addAvInput("(1,1)");
        List<GeoElement> points = new ArrayList<>();
        points.add(zeroPoint);
        points.add(onePoint);
        List<GeoElementProperty> pointProperties = new GeoElementProperties(points).getProperties();

        assertThat(pointProperties.get(0).getName(), equalTo("Show"));
        assertThat(pointProperties.get(1).getName(), equalTo("stylebar.Color"));
        assertThat(pointProperties.get(2).getName(), equalTo("Properties.Style"));
        assertThat(pointProperties.get(3).getName(), equalTo("Size"));
        assertThat(pointProperties.get(4).getName(), equalTo("stylebar.Caption"));
        assertThat(pointProperties.get(5).getName(), equalTo("ShowTrace"));
        assertThat(pointProperties.get(6).getName(), equalTo("fixed"));
        assertThat(pointProperties.get(7).getName(), equalTo("ShowInAlgebraView"));
    }
}