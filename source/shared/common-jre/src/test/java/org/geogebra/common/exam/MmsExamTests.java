package org.geogebra.common.exam;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.exam.restrictions.MmsExamRestrictions.isVisibilityEnabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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

	@Test
	public void testRestrictedStatisticsContextMenuItem() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						algebraProcessor, GeoGebraConstants.CAS_APPCODE));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Restricted vectors
			"a = (1, 2)",
			"b = (1, 2) + 0",
	})
	public void testRestrictedVisibility(String expression) {
		assertFalse(isVisibilityEnabled(evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + i",
			"(1 + i) * (2 - 3i)",
			"sqrt(-1)",
			"5 - i + 2"
	})
	public void testRestrictedComplexNumbers(String expression) {
		assertNull(evaluate(expression));
	}
}
