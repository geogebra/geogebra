package org.geogebra.common.gui.view.algebra;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.test.EventAccumulator;
import org.junit.Test;

public class AlgebraItemTest extends BaseUnitTest {

    private static final int LATEX_MAX_EDIT_LENGTH = 1500;
    private static final String line = "Line((0,0), (1,1))";
    private static final String fitLine = "FitLine((0,0), (1,1))";
    private static final String circle = "Circle((0,0), (1,1))";

    @Test
    public void testShouldShowBothRowsInGraphing() {
        getApp().setGraphingConfig();

        checkShouldShowBothRowsFor(line);
        checkShouldShowBothRowsFor(fitLine);
        checkShouldShowBothRowsFor("0.6");
        checkShouldShowBothRowsFor("0.6+2");
    }

    private void checkShouldShowBothRowsFor(String definition) {
        GeoElement line = addAvInput(definition);
        assertThat(AlgebraItem.shouldShowBothRows(line), is(true));
    }

    @Test
    public void testShouldShowBothRowsInGeometry() {
        getApp().setGeometryConfig();
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);

        checkShouldShowBothRowsFor(line);
        checkShouldShowBothRowsFor(fitLine);
        checkShouldShowBothRowsFor(circle);
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
                AlgebraItem.getLatexString(vector, LATEX_MAX_EDIT_LENGTH, false);
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

    @Test
    public void percentageDefinition() {
        GeoElement geo = addAvInput("5%*5+5");
        assertThat(
                geo.getNameAndDefinition(StringTemplate.latexTemplate),
                is("a\\, = \\,5\\% \\cdot 5 + 5"));
    }

    @Test
    public void addingToAVShouldNotCallUpdate() {
        EventAccumulator eventAccumulator = new EventAccumulator();
        getApp().getEventDispatcher().addEventListener(eventAccumulator);
        GeoElement geo = addAvInput("a=1+3");
        InputHelper.updateProperties(new GeoElement[]{geo}, getApp().getActiveEuclidianView(),
                getKernel().getConstructionStep());
        assertThat(Collections.singletonList("ADD a"), is(eventAccumulator.getEvents()));
    }

    @Test
    public void testCoordStyleAustrianPreview() {
        getApp().setGraphingConfig();
        getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_AUSTRIAN);
        GeoPoint point = getKernel().getAlgoDispatcher().point(1, 2, false);
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(point),
                endsWith("\\left(1 | 2 \\right)"));
    }

    @Test
    public void testIsGeoFraction() {
        GeoElement fraction1 = add("1+1/3");
        GeoElement fraction2 = add("-5/3");
        GeoElement solve = add("Solve(2x=3)");
        assertThat(AlgebraItem.isGeoFraction(fraction1), is(true));
        assertThat(AlgebraItem.isGeoFraction(fraction2), is(true));
        assertThat(AlgebraItem.isGeoFraction(solve), is(false));
    }

    @Test
    public void testGetDefinitionLabeled() {
        GeoElement element = add("A=(1,2)");
        String definition = AlgebraItem.getDefinitionLatexForGeoElement(element);
        assertThat(definition, is("A=(1,2)"));
    }

    @Test
    public void testGetDefinitionUnlabeled() {
        GeoElement element = add("A=(1,2)");
        new LabelController().hideLabel(element);
        String definition = AlgebraItem.getDefinitionLatexForGeoElement(element);
        assertThat(definition, is("(1,2)"));
    }

    @Test
    public void limitNMultiplyPiForm() {
        GeoElement element = add("17!");
        String definition = element.getAlgebraDescriptionForPreviewOutput();
        assertThat(definition, is("355687428096000"));
    }

    @Test
    public void shouldShowEqualSignPrefixTest() {
        assertThat(AlgebraItem.shouldShowEqualSignPrefix(add("1/2")), equalTo(true));
        assertThat(AlgebraItem.shouldShowEqualSignPrefix(add("1/3")), equalTo(false));
        assertThat(AlgebraItem.shouldShowEqualSignPrefix(add("sqrt(3)+1")), equalTo(true));
    }
}