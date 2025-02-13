package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.exam.restrictions.MmsExamRestrictions.isVisibilityEnabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MmsExamTests extends BaseExamTests {
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
		evaluateGeoElement("g(x) = x");
		assertFalse(isVisibilityEnabled(evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Unrestricted integrals
			"Integral(f)",
			"Integral(f, x)",
	})
	public void testUnrestrictedVisibility(String expression) {
		evaluateGeoElement("f(x) = x");
		assertTrue(isVisibilityEnabled(evaluateGeoElement(expression)));
	}

	@Test
	public void testRestrictedStatisticsContextMenuItems() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						algebraProcessor, GeoGebraConstants.CAS_APPCODE));
	}
}
