package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.CoreMatchers.instanceOf;
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

        assertThat(pointProperties.get(0), instanceOf(ShowObjectProperty.class));
        assertThat(pointProperties.get(1), instanceOf(ColorProperty.class));
        assertThat(pointProperties.get(2), instanceOf(PointStyleProperty.class));
        assertThat(pointProperties.get(3), instanceOf(PointSizeProperty.class));
        assertThat(pointProperties.get(4), instanceOf(CaptionStyleProperty.class));
        assertThat(pointProperties.get(5), instanceOf(ShowTraceProperty.class));
        assertThat(pointProperties.get(6), instanceOf(FixObjectProperty.class));
        assertThat(pointProperties.get(7), instanceOf(ShowInAVProperty.class));
    }
}