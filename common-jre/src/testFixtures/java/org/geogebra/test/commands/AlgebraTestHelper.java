package org.geogebra.test.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.matcher.MultipleResultsMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;

public class AlgebraTestHelper {

	public static void shouldFail(String string, String errorMsg, App app) {
		shouldFail(string, errorMsg, null, app);
	}

	/**
	 * @param string
	 *            command
	 * @param errorMsg
	 *            expected error message
	 * @param altErrorMsg
	 *            alternative error message
	 * @param app
	 *            application
	 */
	public static void shouldFail(String string, String errorMsg,
			String altErrorMsg, App app) {
		ErrorAccumulator errorStore = new ErrorAccumulator();
		AlgebraProcessor algebraProcessor = app.getKernel()
				.getAlgebraProcessor();
		EvalInfo info = algebraProcessor.getEvalInfo(false, false)
				.withNoRedefinitionAllowed();
		algebraProcessor.processAlgebraCommandNoExceptionHandling(string, false,
				errorStore, info, null);
		if (!errorStore.getErrors().contains(errorMsg) && (altErrorMsg == null
				|| !errorStore.getErrors().contains(altErrorMsg))) {
			fail(string + ":" + errorStore.getErrors() + "," + errorMsg
					+ " expected");
		}
	}

	/**
	 * Checks that command does not fail
	 * @param cmd command
	 * @param app app
	 */
	public static void shouldPass(String cmd, App app) {
		ErrorAccumulator errorStore = new ErrorAccumulator();
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		EvalInfo info = algebraProcessor
				.getEvalInfo(false, false)
				.withNoRedefinitionAllowed();
		GeoElementND[] elements = algebraProcessor
				.processAlgebraCommandNoExceptionHandling(
						cmd, false, errorStore, info, null
				);
		assertNotNull(elements);
		assertEquals("", errorStore.getErrors());
	}

	/**
	 * @param s
	 *            input
	 * @param expected
	 *            expected output
	 * @param proc
	 *            algebra processor
	 * @param tpl
	 *            template
	 */
	public static void checkSyntaxSingle(String s,
			List<Matcher<String>> expected, AlgebraProcessor proc,
			StringTemplate tpl) {
		Throwable t = null;
		GeoElementND[] result = null;
		try {
			result = getResult(s, proc);
		} catch (Throwable e) {
			t = e;
		}

		if (t != null) {
			Log.debug(t);
		}
		if (t instanceof AssertionError) {
			throw (AssertionError) t;
		}
		assertNull(t);
		Assert.assertNotNull(s, result);
		Assert.assertEquals(s + " count:", expected.size(), result.length);

		for (int i = 0; i < expected.size(); i++) {
			String actual = result[i].toValueString(tpl);
			assertThat(s + ":" + actual, actual, expected.get(i));
		}
		Log.debug("+");
	}

	private static GeoElementND[] getResult(String input, AlgebraProcessor algebraProcessor) {
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				input,
				false,
				TestErrorHandler.INSTANCE,
				false,
				null);
	}

	/**
	 * Compares the actual result with the combinations of the one valid result.
	 * For example, when the input is an equation to be solved, then the combinations of the
	 * one valid result can be listing the solutions of the equation in different orders.
	 * If the actual result matches with one of the combinations then the test passes.
	 * @param input the input to be solved (not to be confused with the result of this input)
	 * @param validResultCombinations the possible combinations of the result
	 *                                   that can be accepted as valid
	 * @param algebraProcessor algebra processor
	 * @param template string template
	 */
	public static void checkValidResultCombinations(
			String input,
			String[] validResultCombinations,
			AlgebraProcessor algebraProcessor,
			StringTemplate template) {
		GeoElementND[] actualResults = getResult(input, algebraProcessor);
		assertOneOf(actualResults[0], validResultCombinations, template);
	}

	/**
	 * @param actualResult
	 *            computation result
	 * @param validResultCombinations
	 *            valid results
	 * @param template
	 *            serialization template
	 */
	public static void assertOneOf(GeoElementND actualResult,
			String[] validResultCombinations, StringTemplate template) {
		String actualResultString = actualResult.toValueString(template);
		MultipleResultsMatcher validResultsMatcher =
				new MultipleResultsMatcher(validResultCombinations);
		assertThat(actualResultString, validResultsMatcher);
	}

	/**
	 * @param input
	 *            input
	 * @param expected
	 *            expected output
	 * @param proc
	 *            algebra processor
	 * @param tpl
	 *            template
	 */
	public static void checkSyntaxSingle(String input, String[] expected,
			AlgebraProcessor proc, StringTemplate tpl) {
		checkSyntaxSingle(input, getMatchers(expected), proc, tpl);
	}

	/**
	 * @param expected
	 *            list of expected strings
	 * @return list of corresponding matchers
	 */
	public static List<Matcher<String>> getMatchers(String... expected) {
		ArrayList<Matcher<String>> matchers = new ArrayList<>();
		for (String exp : expected) {
			matchers.add(IsEqual.equalTo(exp));
		}
		return matchers;
	}

	public static void enableCAS(App app, boolean enabled) {
		app.getSettings().getCasSettings().setEnabled(enabled);
	}

	/**
	 * @param cmd0
	 *            command
	 * @return whether command is in CAS but is internal
	 */
	public static boolean internalCAScommand(Commands cmd0) {
		return cmd0 == Commands.SolveQuartic || cmd0 == Commands.Evaluate;
	}

	/**
	 * @param a
	 *            command
	 * @return whether only is in beta
	 */
	public static boolean betaCommand(Commands a, App app) {
		return a == Commands.MatrixPlot || a == Commands.DensityPlot
				|| a == Commands.Polyhedron
				|| (a == Commands.ImplicitSurface
				&& !app.has(Feature.IMPLICIT_SURFACES));
	}

}
