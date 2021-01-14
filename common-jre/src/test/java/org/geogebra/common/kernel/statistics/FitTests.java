package org.geogebra.common.kernel.statistics;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.properties.impl.objects.EquationFormProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.junit.Assert;
import org.junit.Test;

public class FitTests extends BaseUnitTest {

    @Test
    public void testFitListOfPointsAndListOfFunction() {
        getApp().setGraphingConfig();
        GeoElement fitCommand =
                addAvInput("Fit({(-2, 3), (0, 1), (2, 1), (2, 3)}, {x^2, x})");
        String outputString = fitCommand.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("0.625x² - 0.25x"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitCommand.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitCommand.getToStringMode());
    }

    @Test
    public void testFitListOfPointsAndFunction() {
        getApp().setGraphingConfig();
        addAvInput("a = 0");
        GeoElement fitCommand =
                addAvInput("Fit({(-2, 3), (0, 1), (2, 1), (2, 3)}, a + x^2)");
        String outputString = fitCommand.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("-0.99999 + x²"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitCommand.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitCommand.getToStringMode());
    }

    @Test
    public void testFitExp() {
        getApp().setGraphingConfig();
        GeoElement fitExp =
                addAvInput("FitExp({(0, 1), (2, 4)})");
        String outputString = fitExp.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("1ℯ^(0.69315x)"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitExp.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitExp.getToStringMode());
    }

    @Test
    public void testFitGrowth() {
        getApp().setGraphingConfig();
        GeoElement fitGrowth =
                addAvInput("FitGrowth({(0, 1), (2, 3), (4, 3), (6, 4)})");
        String outputString = fitGrowth.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("1.31265 * 1.23114^x"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitGrowth.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitGrowth.getToStringMode());
    }

    @Test
    public void testFitLine() {
        getApp().setGraphingConfig();
        GeoElement fitLine =
                addAvInput("FitLine({(-2, 1), (1, 2), (2, 4), (4, 3), (5, 4)})");
        String outputString = fitLine.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("y = 0.4x + 2"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitLine.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, fitLine.getToStringMode());
    }

    @Test
    public void testFitLineY() {
        GeoElement fitLineY = addAvInput("FitLine((0,0),(1,1),(2,2))");
        String outputString = fitLineY.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("y = x"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitLineY.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, fitLineY.getToStringMode());
    }

    @Test
    public void testFitLineYLoadFromXML() {
        getApp().setGraphingConfig();
        addAvInput("f = FitLine((0,0),(1,1),(2,2))");
        getApp().setXML(getApp().getXML(), true);
        GeoElement loadedFitLine = lookup("f");

        String outputString = loadedFitLine.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("y = x"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, loadedFitLine.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, loadedFitLine.getToStringMode());
    }

    @Test
    public void testFitLineX() {
        getApp().setGraphingConfig();
        GeoElement fitLineX =
                addAvInput("FitLineX({(-2, 1), (1, 2), (2, 4), (4, 3), (5, 4)})");
        String outputString = fitLineX.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("y = 0.56667x + 1.66667"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitLineX.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, fitLineX.getToStringMode());
    }

    @Test
    public void testFitLineXLoadFromXML() {
        getApp().setGraphingConfig();
        addAvInput("f = FitLineX({(-2, 1), (1, 2), (2, 4), (4, 3), (5, 4)})");

        getApp().setXML(getApp().getXML(), true);
        GeoElement loadedFitLine = lookup("f");

        String outputString = loadedFitLine.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("y = 0.56667x + 1.66667"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, loadedFitLine.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_EXPLICIT, loadedFitLine.getToStringMode());
    }

    @Test
    public void testFitLog() {
        getApp().setGraphingConfig();
        GeoElement fitLog =
                addAvInput("FitLog({(ℯ, 1), (ℯ^2, 4)})");
        String outputString = fitLog.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("-2 + 3ln(x)"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitLog.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitLog.getToStringMode());
    }

    @Test
    public void testFitLogistic() {
        getApp().setGraphingConfig();
        GeoElement fitLogistic =
                addAvInput("FitLogistic({(-6, 2), (0, 2), (3, 4), (3.4, 8)})");
        String outputString = fitLogistic.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("1.97587 / (1 - 0.02551ℯ^(0.99561x))"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitLogistic.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitLogistic.getToStringMode());
    }

    @Test
    public void testFitPoly() {
        getApp().setGraphingConfig();
        GeoElement fitPoly =
                addAvInput("FitPoly({(-1, -1), (0, 1), (1, 1), (2, 5)}, 3)");
        String outputString = fitPoly.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("x³ - x² + 0x + 1"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitPoly.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitPoly.getToStringMode());
    }

    @Test
    public void testFitPw() {
        getApp().setGraphingConfig();
        GeoElement fitPow =
                addAvInput("FitPow({(1, 1), (3, 2), (7, 4)})");
        String outputString = fitPow.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("0.97449x^0.70848"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitPow.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitPow.getToStringMode());
    }

    @Test
    public void testFitSin() {
        getApp().setGraphingConfig();
        GeoElement fitSin =
                addAvInput("FitSin({(1, 1), (2, 2), (3, 1), (4, 0), (5, 1), (6, 2)})");
        String outputString = fitSin.toOutputValueString(StringTemplate.editTemplate);
        assertThat(outputString, equalTo("1 + 1sin(1.5708x - 1.5708)"));
        Assert.assertEquals(DescriptionMode.DEFINITION_VALUE, fitSin.getDescriptionMode());
        Assert.assertEquals(GeoLine.EQUATION_IMPLICIT_NON_CANONICAL, fitSin.getToStringMode());
    }

    @Test
    public void testEquationPropertyVisibilityGraphing() {
        getApp().setGraphingConfig();
        getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

        GeoElement[] geos = getFitLineGeoElements();

        for (GeoElement geo : geos) {
            assertThrows(NotApplicablePropertyException.class,
                    () -> new EquationFormProperty(getLocalization(), geo));
            Assert.assertTrue(LineEqnModel.forceInputForm(getApp(), geo));
        }
    }

    @Test
    public void testEquationPropertyVisibilityGeometry() {
        getApp().setGeometryConfig();
        getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

        GeoElement[] geos = getFitLineGeoElements();

        try {
            for (GeoElement geo : geos) {
                Assert.assertFalse(LineEqnModel.forceInputForm(getApp(), geo));
                new EquationFormProperty(getLocalization(), geo);
            }
        } catch (NotApplicablePropertyException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFitLineRectangleSelectionForTwoPoints() {
        EuclidianView view =  getApp().getActiveEuclidianView();
        EuclidianController controller = view.getEuclidianController();

        addAvInput("A = (1,1)");
        addAvInput("B = (2,1)");

        Rectangle rectangle = new Rectangle();
        rectangle.setRect(0, 0, view.getWidth(), view.getHeight());

        controller.setMode(EuclidianConstants.MODE_FITLINE, null);
        view.setSelectionRectangle(rectangle);
        controller.processSelectionRectangle(false, false, false);

        GeoElement geo = getConstruction().getLastGeoElement();
        Assert.assertTrue(geo instanceof GeoLine);
    }

    private GeoElement[] getFitLineGeoElements() {
        GeoElementFactory factory = getElementFactory();
        GeoLine fitLine = (GeoLine) factory.create("FitLine({(-1,-1),(0,1),(1,1),(2,5)})");
        GeoLine fitLineX = (GeoLine) factory.create("FitLineX({(-1,3),(2,1),(3,4),(5,3),(6,5)})");

        return new GeoElement[]{fitLine, fitLineX};
    }
}
