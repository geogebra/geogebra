package org.geogebra.common.gui.view.algebra;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Objects;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.test.EventAccumulator;
import org.geogebra.test.annotation.Issue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgebraItemTest extends BaseUnitTest {

    private static final int LATEX_MAX_EDIT_LENGTH = 1500;
    private static final String line = "Line((0,0), (1,1))";
    private static final String fitLine = "FitLine((0,0), (1,1))";
    private static final String circle = "Circle((0,0), (1,1))";

    @BeforeClass
    public static void enablePreviewFeatures() {
        PreviewFeature.setPreviewFeaturesEnabled(true);
    }

    @AfterClass
    public static void disablePreviewFeatures() {
        PreviewFeature.setPreviewFeaturesEnabled(false);
    }

    @Override
    public AppCommon createAppCommon() {
        return AppCommonFactory.create3D();
    }

    @Test
    public void testShouldShowBothRowsInGraphing() {
        getApp().setGraphingConfig();

        checkShouldShowBothRowsFor(line);
        checkShouldShowBothRowsFor(fitLine);
        checkShouldShowBothRowsFor("0.6");
        checkShouldShowBothRowsFor("0.6+2");
    }

    @Test
    public void testTwoRowsFrenchCoords() {
        getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_FRENCH);
        GeoPoint point = add("(1,2)");
        assertThat(AlgebraItem.shouldShowBothRows(point, getSettings().getAlgebra()), is(false));
    }

    private void checkShouldShowBothRowsFor(String definition) {
        GeoElement line = addAvInput(definition);
        assertThat(AlgebraItem.shouldShowBothRows(line, getSettings().getAlgebra()), is(true));
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
        boolean shouldShowOutputRow = AlgebraItem.shouldShowBothRows(angle,
                getSettings().getAlgebra());
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
    @Issue("APPS-6269")
    public void getLatexStringConic() {
        GeoElement conic = addAvInput("x^2/sqrt(2)=1");
        GeoElement quadric = addAvInput("x^2/sqrt(2)=z");
        String latexStringConic =
                AlgebraItem.getLatexString(conic, LATEX_MAX_EDIT_LENGTH, false);
        String latexStringQuadric =
                AlgebraItem.getLatexString(quadric, LATEX_MAX_EDIT_LENGTH, false);
        assertThat(latexStringConic, equalTo("eq1: \\,\\frac{x^{2}}{\\sqrt{2}}\\, = \\,1"));
        assertThat(latexStringQuadric, equalTo("eq2: \\,\\frac{x^{2}}{\\sqrt{2}}\\, = \\,z"));
    }

    @Test
    public void shouldShowBothRowsForMinusOneCalc() {
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
        getApp().setGraphingConfig();
        GeoElement geo = addAvInput("(-1)(9)");
        assertThat(AlgebraItem.shouldShowBothRows(geo, getSettings().getAlgebra()), is(true));
    }

    @Test
    public void shouldShowBothRowsForMinusTwoCalc() {
        getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.DEFINITION_AND_VALUE);
        getApp().setGraphingConfig();
        GeoElement geo = addAvInput("(-2)(9)");
        assertThat(AlgebraItem.shouldShowBothRows(geo, getSettings().getAlgebra()), is(true));
    }

    @Test
    public void shouldNotShowBothRowsForText() {
        GeoElement geoElement = addAvInput("text1 = \"my text\"");
        assertThat(AlgebraItem.shouldShowBothRows(geoElement, getSettings().getAlgebra()),
                is(false));
    }

    @Test
    public void shouldShowBothRowsForTakeStringAlgorithm() {
        GeoElement geoElement = addAvInput("Take(\"hello\", 2, 4)");
        assertThat(AlgebraItem.shouldShowBothRows(geoElement, getSettings().getAlgebra()),
                is(true));
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
    @Issue("APPS-6636")
    public void testFractionPreviewKeepsOriginalInput() {
        getApp().setGraphingConfig();
        GeoNumeric numeric = addAvInput("0.5");
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(numeric),
                equalTo("a\\, = \\,0.5"));
    }

    @Test
    @Issue("APPS-4059")
    public void testCoordStyleAustrianPreview() {
        getApp().setGraphingConfig();
        getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_AUSTRIAN);
        GeoPoint point = addAvInput("A=(1,2)");
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(point),
                endsWith("\\left(1\\;|\\;2 \\right)"));
    }

    @Test
    @Issue("APPS-6704")
    public void testCoordStyleAustrianForSpecialPoints() {
        getKernel().setPrintDecimals(2);
        getApp().setGraphingConfig();
        getSettings().getGeneral().setCoordFormat(CoordinatesFormat.COORD_FORMAT_AUSTRIAN);

        // Add function and create special points
        GeoFunction function = addAvInput("f(x) = x^2-3x");
        Objects.requireNonNull(SuggestionIntersectExtremum.get(function)).execute(function);

        // Assert that input row shows the definition
        // and the output row shows the coordinates in austrian format
        GeoElement intersectionPoint = lookup("A");
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(intersectionPoint),
                equalTo("Intersect\\left(f, xAxis \\right)"));
        assertThat(AlgebraItem.getOutputTextForGeoElement(intersectionPoint),
                equalTo("A\\left(0 | 0 \\right)"));

        GeoElement extremumPoint = lookup("C");
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(extremumPoint),
                equalTo("C\\, = \\,Extremum\\left(f \\right)"));
        assertThat(AlgebraItem.getOutputTextForGeoElement(extremumPoint),
                equalTo("\\left(1.5 | -2.25 \\right)"));
    }

    @Test
    public void testPreviewLatexForAngleWithGivenSize() {
        getSettings().getAlgebra().setStyle(AlgebraStyle.DESCRIPTION);
        add("A=(1, 2)");
        add("B=(3, 0)");
        GeoElement angle = add("Angle(A,B,45)");
        assertThat(AlgebraItem.getPreviewLatexForGeoElement(angle),
                equalTo("\\text{α = Angle between A, B, A'}"));
    }

    @Test
    public void testIsGeoFraction() {
        GeoElement fraction1 = add("1+1/3");
        GeoElement fraction2 = add("-5/3");
        GeoElement solve = add("Solve(2x=3)");
		assertThat(AlgebraItem.evaluatesToFraction(fraction1), is(true));
		assertThat(AlgebraItem.evaluatesToFraction(fraction2), is(true));
		assertThat(AlgebraItem.evaluatesToFraction(solve), is(false));
    }

    @Test
    public void testGetDefinitionLabeled() {
        GeoElement element = add("A=(1,2)");
        String definition = AlgebraItem.getDefinitionLatexForGeoElement(element);
        assertThat(definition, is("A=$point(1,2)"));
    }

    @Test
    public void testGetDefinitionUnlabeled() {
        GeoElement element = add("A=(1,2)");
        new LabelController().hideLabel(element);
        String definition = AlgebraItem.getDefinitionLatexForGeoElement(element);
        assertThat(definition, is("$point(1,2)"));
    }

    @Test
    public void testGetDefinitionVector() {
        GeoElement element = add("v=(1,2)");
        String definition = AlgebraItem.getDefinitionLatexForGeoElement(element);
        assertThat(definition, is("v=$vector(1,2)"));
    }

    @Test
    public void limitNMultiplyPiForm() {
        GeoElement element = add("17!");
        String definition = element.getAlgebraDescriptionForPreviewOutput();
        assertThat(definition, is("355687428096000"));
    }

    @Issue({"APPS-6267", "APPS-6353"})
    @Test
    public void testIsRationalizableFraction() {
        assertThat(AlgebraItem.isRationalizableFraction(add("1/3")), equalTo(false));
        assertThat(AlgebraItem.isRationalizableFraction(add("1 + 3")), equalTo(false));
        assertThat(AlgebraItem.isRationalizableFraction(add("-3 + 3")), equalTo(false));
        assertThat(AlgebraItem.isRationalizableFraction(add("1/(3 + 2)")), equalTo(false));
        assertThat(AlgebraItem.isRationalizableFraction(add("1/sqrt(3)")), equalTo(true));
        assertThat(AlgebraItem.isRationalizableFraction(add("(1 + sqrt(2))/sqrt(3)")),
                equalTo(true));
        assertThat(AlgebraItem.isRationalizableFraction(add("(1 + sqrt(2))/(7 - sqrt(3))")),
                equalTo(true));
        assertThat(AlgebraItem.isRationalizableFraction(add("(1 + sqrt(2))/(sqrt(7) - sqrt(3))")),
                equalTo(false));
    }

    @Test
    @Issue("APPS-6366")
    public void testBuildPlainTextItemSimple() {
        GeoElement point = add("A_{1}=1+i");
        IndexHTMLBuilder builder = new IndexHTMLBuilder(false);
        AlgebraItem.buildPlainTextItemSimple(point, builder);
        assertEquals("A<sub><font size=\"-1\">1</font></sub> = 1 + &#943;",
                builder.toString());
    }
}
