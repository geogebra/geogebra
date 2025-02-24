package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MmsExamTests extends BaseExamTests {
	private static final Set<VisibilityRestriction> visibilityRestrictions =
			MmsExamRestrictions.createVisibilityRestrictions();

	@BeforeEach
	public void setupMmsExam() {
		setInitialApp(SuiteSubApp.CAS);
		examController.startExam(ExamType.MMS, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Restricted inequalities
			"x > 0",
			"y <= 1",
			"x < y",
			"x - y > 2",
			"x^2 + 2y^2 < 1",
			"f: x > 0",
			"f(x) = x > 2",
			// Restricted integrals
			"Integral(g, -5, 5)",
			"Integral(g, x, -5, 5)",
			"NIntegral(g, -5, 5)",
			// Restricted vectors
			"a = (1, 2)",
			"b = (1, 2) + 0",
	})
	public void testRestrictedVisibility(String expression) {
		evaluateGeoElement("g(x) = x"); // For integrals
		assertTrue(VisibilityRestriction.isVisibilityRestricted(evaluateGeoElement(expression),
				visibilityRestrictions));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Unrestricted integrals
			"Integral(f)",
			"Integral(f, x)",
	})
	public void testUnrestrictedVisibility(String expression) {
		evaluateGeoElement("f(x) = x");
		assertFalse(VisibilityRestriction.isVisibilityRestricted(evaluateGeoElement(expression),
				visibilityRestrictions));
	}

	@Test
	public void testRestrictedStatisticsContextMenuItems() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						algebraProcessor, GeoGebraConstants.CAS_APPCODE));
	}

	@ParameterizedTest
	@CsvSource({
			"1 + i",
			"(1 + i) * (2 - 3i)",
			"5 - i + 2",
	})
	public void testRestrictedComplexNumberInputs(String expression) {
		assertNull(evaluate(expression));
	}

	@Test
	public void testRestrictedComplexNumberOutput() {
		assertNull(evaluate("sqrt(-5)", "ί*√5"));
	}

	@Test
	public void testRestrictedSpecialPointsContextMenuItem() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("f(x)=xx"),
						algebraProcessor, GeoGebraConstants.CAS_APPCODE));
	}

	@Test
	public void testRestrictedSpecialPoints() {
		GeoElement f = evaluateGeoElement("xx");
		app.getSpecialPointsManager().updateSpecialPoints(f);
		assertNull(app.getSpecialPointsManager().getSelectedPreviewPoints());
	}
}
