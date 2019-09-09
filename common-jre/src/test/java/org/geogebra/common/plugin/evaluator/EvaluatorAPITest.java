package org.geogebra.common.plugin.evaluator;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.EditorTyper;
import org.geogebra.common.io.MathFieldCommon;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the EvaluatorAPI
 */
public class EvaluatorAPITest extends BaseUnitTest {

	private EditorTyper typer;
	private EvaluatorAPI api;

	@Before
	public void setupTest() {
		MathFieldCommon mathField = new MathFieldCommon();
		api = new EvaluatorAPI(getKernel(), mathField.getInternal());
		typer = new EditorTyper(mathField);
	}

	@Test
	public void testGetEvaluatorValue() {
		typer.type("1/2");
		String value = api.getEvaluatorValue();
		assertEquals(
				"{\"latex\":\"{\\\\frac{1}{2}}\",\"content\":\"(1)\\/(2)\",\"eval\":\"0.5\"}",
				value);
	}

	@Test
	public void testGetEvaluatorValueNonNumeric() {
		typer.type("GeoGebra");
		String value = api.getEvaluatorValue();
		assertEquals(
				"{\"latex\":\"GeoGebra\",\"content\":\"GeoGebra\",\"eval\":\"NaN\"}",
				value);
	}

	@Test
	public void testEmptyInput() {
		String value = api.getEvaluatorValue();
		assertEquals(
				"{\"latex\":\"\\\\nbsp \",\"content\":\"\",\"eval\":\"NaN\"}",
				value);
	}

	@Test
	public void testInvalidInput() {
		typer.type("1/");
		String value = api.getEvaluatorValue();
		assertEquals(
				"{\"latex\":\"{\\\\frac{1}{\\\\nbsp }}\",\"content\":\"(1)\\/()\",\"eval\":\"NaN\"}",
				value);
	}
}
