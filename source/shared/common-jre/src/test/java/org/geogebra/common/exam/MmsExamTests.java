package org.geogebra.common.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.mms.MmsValueConverter;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.GeoElementValueConverter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
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
						algebraProcessor, CAS_APPCODE, app.getSettings().getAlgebra()));
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
				contextMenuFactory.makeAlgebraContextMenu(
						evaluateGeoElement("f(x)=xx", "x^2"),
						algebraProcessor, CAS_APPCODE, app.getSettings().getAlgebra()));
	}

	@Test
	public void testRestrictedSpecialPoints() {
		GeoElement f = evaluateGeoElement("xx");
		app.getSpecialPointsManager().updateSpecialPoints(f);
		assertNull(app.getSpecialPointsManager().getSelectedPreviewPoints());
	}

	@Test
	public void testRestrictedChartOutput() {
		String definition = "BarChart({10, 11, 12}, {5, 8, 12})";
		GeoElement barchart = evaluateGeoElement(definition);
		MmsValueConverter converter = new MmsValueConverter(new GeoElementValueConverter());
		assertEquals(definition,
				converter.toValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals(definition,
				converter.toOutputValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals("a = " + definition,
				converter.toLabelAndDescription(barchart, StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Union({1}, {2})",
			"Quartile1({1, 2, 3})",
			"Q1({1, 2, 3})",
	})
	public void testRestrictedCommands(String expression) {
		assertNull(evaluate(expression, "1"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"BinomialDist(1, 0.5)",
			"BinomialDist(1, 0.5, false)",
			"Invert(sin(x))",
			"Length((1, 2))",
			"Product(a^2, a, 0, 5)",
			"SampleSD({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3})",
			"SigmaXX({(1, 2), (3, 4)})",
			"SigmaXY({(1, 2), (3, 4)})",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3})",
			"Sum(a^2, a, 0, 5)",
	})
	public void testRestrictedArguments(String expression) {
		// Specify a valid output, so that we know that the input has been filtered or not.
		assertNull(evaluate(expression, "1"));
	}

	@Test
	public void testRestrictedSumSyntax() {
		String sumSyntax = app.getLocalization().getCommandSyntax(Commands.Sum.getCommand());
		assertFalse(sumSyntax.toLowerCase().contains("end value"));
	}
}
