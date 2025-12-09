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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Before;
import org.junit.Test;

public class GeoVector3DTest extends BaseUnitTest {

    @Before
    public void setUp() {
        getApp().set3dConfig();
    }

    @Override
    public AppCommon createAppCommon() {
        return AppCommonFactory.create3D();
    }

    @Test
    public void testDefinitionForIndependent() {
        GeoVector3D vector = addAvInput("v = (1, 2, 3)");
        assertThat(
                vector.getDefinition(StringTemplate.editorTemplate),
                is("$vector(1,2,3)"));
        assertThat(
                vector.getDefinition(StringTemplate.latexTemplate),
                is("\\left( \\begin{align}1 \\\\ 2 \\\\ 3 \\end{align} \\right)"));
    }

    @Test
    public void testDefinitionForDependent() {
        addAvInput("a = 1");
        GeoVector3D vector = addAvInput("v = (a, 2, 3)");
        assertThat(
                vector.getDefinition(StringTemplate.editorTemplate),
                is("$vector(a,2,3)"));
        assertThat(
                vector.getDefinition(StringTemplate.latexTemplate),
                is("\\left( \\begin{align}a \\\\ 2 \\\\ 3 \\end{align} \\right)"));
    }

    @Test
    public void testWIsZeroInUnitVector() {
        GeoVector3D u = addAvInput("u=UnitVector((0,1,sqrt(3)))");
        assertThat(u.getW(), is(0.0));
    }
}
