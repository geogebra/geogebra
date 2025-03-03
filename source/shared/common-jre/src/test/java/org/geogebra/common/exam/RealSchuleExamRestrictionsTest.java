package org.geogebra.common.exam;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.ENGINEERING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RealSchuleExamRestrictionsTest extends BaseExamTests {

	private Settings settings;
	private EuclidianSettings evSettings;

	@BeforeEach
	public void setupExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		settings = app.getSettings();
		evSettings = settings.getEuclidian(1);
	}

	@Test
	public void testSettingsRestrictions() {
		createDefaultSetting();
		examController.startExam(ExamType.REALSCHULE, null);
		realSchuleRestrictionsShouldBeApplied();
		finishExam();
		defaultSettingsShouldBeRestored();
	}

	private void defaultSettingsShouldBeRestored() {
		coordFormatShouldBe(Kernel.COORD_STYLE_DEFAULT);
		axisLabelsShouldBe("xAxis", "yAxis");
		gridShouldBe(EuclidianView.GRID_ISOMETRIC);
		GeoPoint point = new GeoPoint(app.getKernel().getConstruction());
		assertEquals(point.getPointStyle(), EuclidianStyleConstants.POINT_STYLE_DOT);
	}

	private void createDefaultSetting() {
		settings.getGeneral().setCoordFormat(Kernel.COORD_STYLE_DEFAULT);
		evSettings.setAxisLabel(0, "xAxis");
		evSettings.setAxisLabel(1, "yAxis");
		evSettings.setAxisNumberingDistance(0, 1.5);
		evSettings.setAxisNumberingDistance(1, 4.1);
		evSettings.setGridType(EuclidianView.GRID_ISOMETRIC);
	}

	private void realSchuleRestrictionsShouldBeApplied() {
		coordFormatShouldBe(Kernel.COORD_STYLE_AUSTRIAN);
		axisLabelsShouldBe("x", "y");
		gridShouldBe(EuclidianView.GRID_CARTESIAN);
		GeoPoint point = new GeoPoint(app.getKernel().getConstruction());
		assertEquals(point.getPointStyle(), EuclidianStyleConstants.POINT_STYLE_CROSS);
		assertFalse(createFixedEqnModel().isValidAt(0));
	}

	@Test
	public void testExpressionRestrictions() {
		examController.startExam(ExamType.REALSCHULE, null);
		assertNull(evaluate("abs((1,2))"));
		assertNull(evaluate("3+abs((1,2))"));
		assertThat(evaluateGeoElement("2+abs(3)"), hasValue("5"));
	}

	private FixObjectModel createFixedEqnModel() {
		GeoConic circle = (GeoConic) evaluateGeoElement("x^2+y^2=0");
		FixObjectModel model = new FixObjectModel(null, app);
		model.setGeos(new Object[]{circle});
		return model;
	}

	private void finishExam() {
		examController.finishExam();
		examController.exitExam();
	}

	private void gridShouldBe(int expected) {
		assertEquals(expected, evSettings.getGridType());
	}

	private void axisLabelsShouldBe(String... labels) {
		if (labels.length != 2) {
			fail();
		}
		String[] axesLabels = evSettings.getAxesLabels();
		assertEquals(labels[0], axesLabels[0]);
		assertEquals(labels[1], axesLabels[1]);
	}

	private void coordFormatShouldBe(int expected) {
		assertEquals(expected, settings.getGeneral().getCoordFormat());
	}

	@Test
	public void testSettingsRestrictionsAfterFileNew() {
		examController.startExam(ExamType.REALSCHULE, null);
		realSchuleRestrictionsShouldBeApplied();

		// emulating AppW.fileNew(), which is not reachable from common.
		evSettings.reset();
		examController.reapplySettingsRestrictions();

		realSchuleRestrictionsShouldBeApplied();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Line
			"x = y",
			"Line((1, 2), (3, 4))",
			// Conics
			"x^2 = y^2",
			// Implicit curves
			"x^3 = y^2",
			"sin(x) = y^2",
			// Unrelated types
			"(1, 2)",
	})
	public void testUnrestrictedVisibility(String expression) {
		examController.startExam(ExamType.REALSCHULE, null);
		assertTrue(evaluateGeoElement(expression).isEuclidianToggleable());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Lines
			"x = 0",
			"y = 5",
			// Conics
			"x^2 = 0",
			"y^2 = 0",
			// Implicit curves,
			"x^2 = 0",
			"sin(x) = 0",
	})
	public void testRestrictedVisibility(String expression) {
		examController.startExam(ExamType.REALSCHULE, null);
		assertFalse(evaluateGeoElement(expression).isEuclidianToggleable());
	}

	@Test
	public void testEnabledEngineeringNotation() {
		PreviewFeature.enableFeaturePreviews = true;
		examController.startExam(ExamType.REALSCHULE, null);
		GeoElement geoElement = evaluateGeoElement("1.234");
		assertTrue(settings.getAlgebra().isEngineeringNotationEnabled());
		assertTrue(AlgebraOutputFormat.getPossibleFormats(geoElement, true).contains(ENGINEERING));
		PreviewFeature.enableFeaturePreviews = false;
	}
}
