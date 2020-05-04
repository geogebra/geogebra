package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AlgebraItemTest extends BaseUnitTest {

    @Test
    public void shouldShowBothRows() {
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);

        testShouldShowBothRowsInGraphing();
        testShouldShowBothRowsInGeometry();
    }

    private void testShouldShowBothRowsInGraphing() {
        getApp().setGraphingConfig();

        checkShouldShowBothRowsForLine(true);
        checkShouldShowBothRowsForFitLine(true);
    }

    private void checkShouldShowBothRowsForLine(boolean shouldShowBothRows) {
        GeoElement line = addAvInput("Line((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(line), is(shouldShowBothRows));
    }

    private void checkShouldShowBothRowsForFitLine(boolean shouldShowBothRows) {
        GeoElement fitLine = addAvInput("FitLine((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(fitLine), is(shouldShowBothRows));
    }

    private void testShouldShowBothRowsInGeometry() {
        getApp().setGeometryConfig();

        checkShouldShowBothRowsForLine(true);
        checkShouldShowBothRowsForFitLine(true);
        checkShouldShowBothRowsForCircle(true);
    }

    private void checkShouldShowBothRowsForCircle(boolean shouldShowBothRows) {
        GeoElement circle = addAvInput("Circle((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(circle), is(shouldShowBothRows));
    }

    @Test
    public void testShouldShowBothRowsForAngle() {
        getApp().setGeometryConfig();
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DESCRIPTION);

        addAvInput("A = (0, 0)");
        addAvInput("B = (1, 1)");
        addAvInput("C = (1, -1)");
        GeoAngle angle = addAvInput("a = Angle(A, B, C)");
        boolean shouldShowOutputRow = AlgebraItem.shouldShowBothRows(angle);
        assertThat(shouldShowOutputRow, is(true));
    }
}