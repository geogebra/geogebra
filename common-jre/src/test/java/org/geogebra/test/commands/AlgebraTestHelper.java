package org.geogebra.test.commands;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.test.TestErrorHandler;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;

public class AlgebraTestHelper {

	public static void shouldFail(String string, String errorMsg, App app) {
		shouldFail(string, errorMsg, null, app);
	}

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
			fail(string + ":" + errorStore.getErrors());
		}
	}

	public static void dummySyntaxesShouldFail(String cmdName,
			String[] syntaxLines, App app) {
		for (String line : syntaxLines) {
			int args = line.split(",").length;
			StringBuilder withArgs = new StringBuilder(cmdName).append("(");
			for (int i = 0; i < args - 1; i++) {
				withArgs.append("space,");
			}
			withArgs.append("space)");
			if (args > 0 && !"Delete".equals(cmdName)
					&& !"ConstructionStep".equals(cmdName)
					&& !"Text".equals(cmdName) && !"LaTeX".equals(cmdName)
					&& !"RunClickScript".equals(cmdName)
					&& !"RunUpdateScript".equals(cmdName)
					&& !"Defined".equals(cmdName)
					&& !"StopLogging".equals(cmdName)
					&& !"AreEqual".equals(cmdName)
					&& !"AreCongruent".equals(cmdName)
					&& !"Textfield".equals(cmdName)
					&& !"SetViewDirection".equals(cmdName)
					&& !"GetTime".equals(cmdName)
					&& !"CopyFreeObject".equals(cmdName)
					&& !"SetActiveView".equals(cmdName)
					&& !"Name".equals(cmdName)
					&& !"SelectObjects".equals(cmdName)
					&& !"Dot".equals(cmdName) && !"Cross".equals(cmdName)
					&& !"SetConstructionStep".equals(cmdName)
					&& !"TableText".equals(cmdName) && !"Q1".equals(cmdName)
					&& !"Q3".equals(cmdName) && !"SetValue".equals(cmdName)) {

				shouldFail(withArgs.toString(), "arg", app);
			}
		}
		if (syntaxLines.length > 0 && !mayHaveZeroArgs(cmdName)) {
			shouldFail(cmdName + "()", "Illegal number of arguments: 0", app);
		}
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
	public static void testSyntaxSingle(String s,
			List<Matcher<String>> expected, AlgebraProcessor proc,
			StringTemplate tpl) {
		Throwable t = null;
		GeoElementND[] result = null;
		try {
			result = proc.processAlgebraCommandNoExceptionHandling(s, false,
					TestErrorHandler.INSTANCE, false, null);
		} catch (Throwable e) {
			t = e;
		}

		if (t != null) {
			t.printStackTrace();
		}
		if (t instanceof AssertionError) {
			throw (AssertionError) t;
		}
		assertNull(t);
		Assert.assertNotNull(s, result);
		// for (int i = 0; i < result.length; i++) {
		// String actual = result[i].toValueString(tpl);
		// System.out.println("\"" + actual + "\",");
		// }
		Assert.assertEquals(s + " count:", expected.size(), result.length);

		for (int i = 0; i < expected.size(); i++) {
			String actual = result[i].toValueString(tpl);
			MatcherAssert.assertThat(s + ":" + actual, actual, expected.get(i));
		}
		System.out.print("+");
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
	public static void testSyntaxSingle(String s, String[] expected,
			AlgebraProcessor proc, StringTemplate tpl) {
		testSyntaxSingle(s, getMatchers(expected), proc, tpl);
	}

	public static List<Matcher<String>> getMatchers(String[] expected) {
		ArrayList<Matcher<String>> matchers = new ArrayList<>();
		for (String exp : expected) {
			matchers.add(IsEqual.equalTo(exp));
		}
		return matchers;
	}

	public static boolean mayHaveZeroArgs(String cmdName) {
		return Arrays.asList(new String[] { "DataFunction", "AxisStepX",
				"AxisStepY", "Button", "StartLogging", "StopLogging",
				"StartRecord", "ConstructionStep", "StartAnimation", "ShowAxes",
				"ShowGrid", "SetActiveView", "ZoomIn", "SetViewDirection",
				"ExportImage", "Random", "Textfield", "GetTime",
				"UpdateConstruction", "SelectObjects", "Turtle", "Function",
				"Checkbox", "InputBox", "RandomBetween" }).contains(cmdName);
	}

	/**
	 * @param cmd0
	 *            command
	 * @return whether command is in CAS but is internal
	 */
	public static boolean internalCAScommand(Commands cmd0) {
		return cmd0 == Commands.SolveQuartic || cmd0 == Commands.Evaluate;
	}

}
