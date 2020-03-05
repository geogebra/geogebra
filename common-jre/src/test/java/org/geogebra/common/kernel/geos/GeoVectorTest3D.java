package org.geogebra.common.kernel.geos;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GeoVectorTest3D extends BaseUnitTest {

    @Override
    public AppCommon createAppCommon() {
        return AppCommonFactory.create3D();
    }

    @Test
    public void testDefinitionForEdit() {
        getApp().set3dConfig();
        GeoVector3D vector = addAvInput("u=(1,2,3)");
        assertThat(vector.getDefinitionForEditor(), equalTo("u = {{1}, {2}, {3}}"));
    }
}
