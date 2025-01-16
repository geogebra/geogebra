package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

public class GeoPointTest3D extends BaseUnitTest {

    @Override
    public AppCommon createAppCommon() {
        return AppCommonFactory.create3D();
    }

    @Test
    public void testDefinitionForEdit3D() {
        getApp().set3dConfig();
        GeoPoint3D point = addAvInput("A=(1,2,3)");
        assertThat(point.getDefinitionForEditor(), equalTo("A=$point(1,2,3)"));
    }
}
