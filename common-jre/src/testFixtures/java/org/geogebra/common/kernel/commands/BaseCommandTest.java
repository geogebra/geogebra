package org.geogebra.common.kernel.commands;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLines;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;

public class BaseCommandTest {

	private static final int UNINITIALIZED = -1000;
	static AppCommon3D app;
	static AlgebraProcessor ap;
	static List<Integer> signature;
	protected static int uncheckedSyntaxesCount = UNINITIALIZED;

	/**
	 * Create the app
	 */
	@BeforeClass
	public static void setupApp() {
		app = AppCommonFactory.create3D();
		ap = app.getKernel().getAlgebraProcessor();
		app.setRandomSeed(42);
	}

	/**
	 * Checks that given input expression produces expected results.
	 * Also counts calls to this method within a single test to make sure
	 * we test each command at least as many times as it has syntaxes.
	 * @param input input expression
	 * @param expected matchers for expected results (can be empty list if 0 results expected)
	 * @param app1 application
	 * @param processor algebra processor
	 * @param tpl serialization template
	 */
	protected static void testSyntax(String input, List<Matcher<String>> expected,
			App app1, AlgebraProcessor processor, StringTemplate tpl) {
		app1.getEuclidianView1().getEuclidianController().clearZoomerAnimationListeners();
		if (uncheckedSyntaxesCount == UNINITIALIZED) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			try {
				Commands.valueOf(cmdName);
			} catch (Exception e) {
				cmdName = t.getStackTrace()[3].getMethodName().substring(3);
			}

			signature = CommandSignatures.getSignature(cmdName, app1);
			if (signature != null) {
				uncheckedSyntaxesCount = signature.size();
			}
			Log.debug(cmdName);
		}
		uncheckedSyntaxesCount--;
		AlgebraTestHelper.checkSyntaxSingle(input, expected, processor, tpl);
	}

	public static void resetSyntaxCounter() {
		uncheckedSyntaxesCount = UNINITIALIZED;
	}

	/**
	 * Check that processing of input rounded to 5 decimal places produces expected output.
	 * @param input input expression
	 * @param expected expected results (can be empty if command has no results)
	 */
	protected static void tRound(String input, String... expected) {
		t(input, StringTemplate.editTemplate, expected);
	}

	/**
	 * Check that processing of input produces expected output.
	 * @param input input expression
	 * @param expected expected results (can be empty if command has no results)
	 */
	protected static void t(String input, String... expected) {
		testSyntax(input, AlgebraTestHelper.getMatchers(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	/**
	 * Check that processing of input produces expected output
	 * when serialized using a specific template.
	 * @param input input expression
	 * @param tpl serialization template
	 * @param expected expected results (can be empty if command has no results)
	 */
	protected static void t(String input, StringTemplate tpl, String... expected) {
		testSyntax(input, AlgebraTestHelper.getMatchers(expected), app, ap,
				tpl);
	}

	/**
	 * Check that processing of input produces (exactly one) output which matches
	 * a given matcher.
	 * @param input input expression
	 * @param matcher expected results (can be empty if command has no results)
	 */
	protected static void t(String input, Matcher<String> matcher) {
		testSyntax(input, Collections.singletonList(matcher), app, ap,
				StringTemplate.xmlTemplate);
	}

	/**
	 * @see #intersect(String, String, boolean, boolean, String...)
	 */
	protected static void intersect(String arg1, String arg2, boolean checkNumbered,
			String... results) {
		intersect(arg1, arg2, checkNumbered, checkNumbered, results);
	}

	/**
	 * Checks that multiple syntaxes of the Intersect command give the same expected results.
	 * In general these should be equivalent
	 * - Intersect[arg1,arg2]
	 * - Intersect[arg2,arg1]
	 * - Intersect[arg1,arg2,1]
	 * - Intersect[arg2,arg1,1]
	 * - Intersect[arg1,arg2,Intersect[arg1,arg2]]
	 * @param arg1 first object
	 * @param arg2 second object
	 * @param checkNumbered whether to test the [argX,argY,1] syntaxes
	 * @param checkClosest whether to test the [argX,argY,Intersect[...]] syntax
	 * @param results expected result
	 */
	protected static void intersect(String arg1, String arg2, boolean checkNumbered,
			boolean checkClosest, String... results) {
		app.getKernel().clearConstruction(true);
		app.getKernel().getConstruction().setSuppressLabelCreation(false);
		tRound("its:=Intersect(" + arg1 + "," + arg2 + ")", results);
		GeoElement geo = lookup("its") == null ? lookup("its_1") : lookup("its");
		boolean symmetric = geo != null
				&& !(geo.getParentAlgorithm() instanceof AlgoIntersectPolyLines
				&& geo.getParentAlgorithm().getOutput(0)
				.getGeoClassType() == geo.getParentAlgorithm()
				.getOutput(1).getGeoClassType());
		if (symmetric) {
			tRound("Intersect(" + arg2 + "," + arg1 + ")", results);
		}
		if (checkNumbered) {
			tRound("Intersect(" + arg1 + "," + arg2 + ",1)", results[0]);
			if (symmetric) {
				tRound("Intersect(" + arg2 + "," + arg1 + ",1)", results[0]);
			}
		}
		if (checkClosest) {
			tRound("Intersect(" + arg1 + "," + arg2 + "," + results[0] + ")",
					results[0]);
		}
	}

	protected static GeoElement lookup(String label) {
		return app.getKernel().lookupLabel(label);
	}
}
