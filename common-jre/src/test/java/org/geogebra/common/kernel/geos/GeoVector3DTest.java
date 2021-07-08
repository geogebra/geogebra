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
                is("{{1}, {2}, {3}}"));
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
                is("{{a}, {2}, {3}}"));
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
