/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
