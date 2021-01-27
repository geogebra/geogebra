package org.geogebra.common.gui.view.algebra;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.junit.Test;

public class AlgebraItemTest extends BaseUnitTest {

    private static final int LATEX_MAX_EDIT_LENGHT = 1500;

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

    @Test
    public void getLatexString() {
        addAvInput("a = ?");
        GeoVector vector = addAvInput("v = (a, 1)");
        String latexString =
                AlgebraItem.getLatexString(vector, LATEX_MAX_EDIT_LENGHT, false);
        assertThat(latexString, equalTo("v\\, = \\,?"));
    }

    @Test
    public void shouldShowBothRowsForMinusOneCalc() {
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
        getApp().setGraphingConfig();
        GeoElement geo = addAvInput("(-1)(9)");
        assertThat(AlgebraItem.shouldShowBothRows(geo), is(true));
    }

    @Test
    public void shouldShowBothRowsForMinusTwoCalc() {
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
        getApp().setGraphingConfig();
        GeoElement geo = addAvInput("(-2)(9)");
        assertThat(AlgebraItem.shouldShowBothRows(geo), is(true));
    }

    @Test
    public void testMinusPiForm() {
        getApp().setGraphingConfig();
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
        GeoNumeric minusPi = addAvInput("-pi");
        minusPi.setSymbolicMode(true, true);
        assertThat(
                minusPi.getLaTeXDescriptionRHS(true, StringTemplate.latexTemplate),
                equalTo("-\\pi "));
    }
}