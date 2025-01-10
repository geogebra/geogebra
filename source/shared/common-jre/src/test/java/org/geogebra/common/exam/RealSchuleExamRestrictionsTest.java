package org.geogebra.common.exam;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.FixObjectModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

public class RealSchuleExamRestrictionsTest extends BaseUnitTest {

	private Settings settings;
	private EuclidianSettings evSettings;
	private ExamController examController;

	@Before
	public void setupExam() {
		examController = new ExamController(new DefaultPropertiesRegistry(),
				new GeoElementPropertiesFactory(), new ContextMenuFactory());
		examController.setActiveContext(this, getKernel().getAlgebraProcessor()
						.getCommandDispatcher(), getKernel().getAlgebraProcessor(),
				getLocalization(), getSettings(), null, null, null);
		settings = getSettings();
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
		axisNumberDistanceShouldBe(1.5, 0);
		axisNumberDistanceShouldBe(4.1, 1);
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
		axisNumberDistanceShouldBe(0.5, 0);
		axisNumberDistanceShouldBe(0.5, 1);
		assertThat(createFixedEqnModel().isValidAt(0), equalTo(false));
	}

	@Test
	public void testExpressionRestrictions() {
		examController.startExam(ExamType.REALSCHULE, null);
		assertThrows(AssertionError.class, () -> add("abs((1,2))"));
		assertThrows(AssertionError.class, () -> add("3+abs((1,2))"));
		assertThat(add("2+abs(3)"), hasValue("5"));
	}

	private FixObjectModel createFixedEqnModel() {
		GeoConic circle = add("x^2+y^2=0");
		FixObjectModel model = new FixObjectModel(null, getApp());
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

	private void axisNumberDistanceShouldBe(double expected, int axisNumber) {
		assertEquals(expected, evSettings.getAxisNumberingDistance(axisNumber).evaluateDouble(),
				0);
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

	@Test
	public void testUnrestrictedVisibility() {
		examController.startExam(ExamType.REALSCHULE, null);
		// line
		assertThat(add("x=y"), isToggleable());
		assertThat(add("Line((1,2),(3,4))"), isToggleable());
		// Enabled conics
		assertThat(add("x^2=y^2"), isToggleable());
		// Implicit curves
		assertThat(add("x^3 = y^2"), isToggleable());
		assertThat(add("sin(x) = y^2"), isToggleable());
		// unrelated types
		assertThat(add("(1,2)"), isToggleable());
	}

	private Matcher<GeoElement> isToggleable() {
		return hasProperty("toggleable", GeoElement::isEuclidianToggleable, true);
	}

	@Test
	public void testRestrictedVisibility() {
		examController.startExam(ExamType.REALSCHULE, null);
		// line
		assertThat(add("x = 0"), not(isToggleable()));
		assertThat(add("y = 5"), not(isToggleable()));
		// conic
		assertThat(add("x^2 = 0"), not(isToggleable()));
		assertThat(add("y^2 = 0"), not(isToggleable()));
		// implicit curve
		assertThat(add("x^3 = 0"), not(isToggleable()));
		assertThat(add("sin(x) = 0"), not(isToggleable()));
	}
}
