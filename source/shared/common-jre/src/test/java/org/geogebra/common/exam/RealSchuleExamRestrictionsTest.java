package org.geogebra.common.exam;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.ENGINEERING;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.EXACT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RealSchuleExamRestrictionsTest extends BaseExamTestSetup {
	@BeforeEach
	public void setupExam() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testSettingsRestrictions() {
		createDefaultSetting();
		examController.startExam(ExamType.BAYERN_GR, null);
		realSchuleRestrictionsShouldBeApplied();
		finishExam();
		defaultSettingsShouldBeRestored();
	}

	private void defaultSettingsShouldBeRestored() {
		coordFormatShouldBe(Kernel.COORD_STYLE_DEFAULT);
		axisLabelsShouldBe("xAxis", "yAxis");
		gridShouldBe(EuclidianView.GRID_ISOMETRIC);
		GeoPoint point = new GeoPoint(getKernel().getConstruction());
		assertEquals(point.getPointStyle(), EuclidianStyleConstants.POINT_STYLE_DOT);
	}

	private void createDefaultSetting() {
		getApp().getSettings().getGeneral().setCoordFormat(Kernel.COORD_STYLE_DEFAULT);
		getApp().getSettings().getEuclidian(1).setAxisLabel(0, "xAxis");
		getApp().getSettings().getEuclidian(1).setAxisLabel(1, "yAxis");
		getApp().getSettings().getEuclidian(1).setAxisNumberingDistance(0, 1.5);
		getApp().getSettings().getEuclidian(1).setAxisNumberingDistance(1, 4.1);
		getApp().getSettings().getEuclidian(1).setGridType(EuclidianView.GRID_ISOMETRIC);
	}

	private void realSchuleRestrictionsShouldBeApplied() {
		coordFormatShouldBe(Kernel.COORD_STYLE_AUSTRIAN);
		axisLabelsShouldBe("x", "y");
		gridShouldBe(EuclidianView.GRID_CARTESIAN);
		GeoPoint point = new GeoPoint(getKernel().getConstruction());
		assertEquals(point.getPointStyle(), EuclidianStyleConstants.POINT_STYLE_CROSS);
		assertFalse(createFixedEqnModel().isValidAt(0));
	}

	@Test
	public void testExpressionRestrictions() {
		examController.startExam(ExamType.BAYERN_GR, null);
		assertNull(evaluate("abs((1,2))"));
		assertNull(evaluate("3+abs((1,2))"));
		assertThat(evaluateGeoElement("2+abs(3)"), hasValue("5"));
	}

	private FixObjectModel createFixedEqnModel() {
		GeoConic circle = evaluateGeoElement("x^2+y^2=0");
		FixObjectModel model = new FixObjectModel(null, getApp());
		model.setGeos(new Object[]{circle});
		return model;
	}

	private void finishExam() {
		examController.finishExam();
		examController.exitExam();
	}

	private void gridShouldBe(int expected) {
		assertEquals(expected, getApp().getSettings().getEuclidian(1).getGridType());
	}

	private void axisLabelsShouldBe(String... labels) {
		if (labels.length != 2) {
			fail();
		}
		String[] axesLabels = getApp().getSettings().getEuclidian(1).getAxesLabels();
		assertEquals(labels[0], axesLabels[0]);
		assertEquals(labels[1], axesLabels[1]);
	}

	private void coordFormatShouldBe(int expected) {
		assertEquals(expected, getApp().getSettings().getGeneral().getCoordFormat());
	}

	@Test
	public void testSettingsRestrictionsAfterFileNew() {
		examController.startExam(ExamType.BAYERN_GR, null);
		realSchuleRestrictionsShouldBeApplied();

		// emulating AppW.fileNew(), which is not reachable from common.
		getApp().getSettings().getEuclidian(1).reset();
		examController.reapplySettingsRestrictions();

		realSchuleRestrictionsShouldBeApplied();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Line
			"x = y",
			"Line((1, 2), (3, 4))",
			"y = 5",
			// Conics
			"x^2 = y^2",
			"y^2 = 0",
			// Implicit curves
			"x^3 = y^2",
			"sin(x) = y^2",
			// Unrelated types
			"(1, 2)",
	})
	public void testUnrestrictedVisibility(String expression) {
		examController.startExam(ExamType.BAYERN_GR, null);
		GeoElement geoElement = evaluateGeoElement(expression);
		assertTrue(geoElement.isEuclidianToggleable());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Lines
			"x = 0",
			// Conics
			"x^2 = 0",
			// Implicit curves,
			"x^2 = 0",
			"sin(x) = 0",
	})
	public void testRestrictedVisibility(String expression) {
		examController.startExam(ExamType.BAYERN_GR, null);
		GeoElement geoElement = evaluateGeoElement(expression);
		assertFalse(geoElement.isEuclidianToggleable());
	}

	@Test
	public void testEnabledEngineeringNotation() {
		PreviewFeature.enableFeaturePreviews = true;
		examController.startExam(ExamType.BAYERN_GR, null);
		boolean enableEngineeringNotation = getAlgebraSettings().isEngineeringNotationEnabled();
		Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters = getAlgebraSettings()
				.getAlgebraOutputFormatFilters();
		assertTrue(AlgebraOutputFormat.getPossibleFormats(evaluateGeoElement("1.234"),
				enableEngineeringNotation, algebraOutputFormatFilters).contains(ENGINEERING));
		PreviewFeature.enableFeaturePreviews = false;
	}

	@Issue("APPS-6634")
	@Test
	public void testDefaultFormatForMinusOne() {
		GeoElement geoElement = evaluateGeoElement("-1");
		assertEquals(
				List.of(EXACT, ENGINEERING),
				AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));
		assertEquals(EXACT, AlgebraOutputFormat.getActiveFormat(geoElement));
		assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
	}
}
