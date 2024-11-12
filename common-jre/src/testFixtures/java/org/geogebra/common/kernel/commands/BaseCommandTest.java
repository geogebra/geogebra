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

	static AppCommon3D app;
	static AlgebraProcessor ap;
	static List<Integer> signature;
	protected static int syntaxes = -1000;

	/**
	 * Create the app
	 */
	@BeforeClass
	public static void setupApp() {
		app = AppCommonFactory.create3D();
		ap = app.getKernel().getAlgebraProcessor();
		app.setRandomSeed(42);
	}

	protected static void testSyntax(String s, List<Matcher<String>> expected,
			App app1, AlgebraProcessor proc, StringTemplate tpl) {
		app1.getEuclidianView1().getEuclidianController().clearZoomerAnimationListeners();
		if (syntaxes == -1000) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			try {
				Commands.valueOf(cmdName);
			} catch (Exception e) {
				cmdName = t.getStackTrace()[3].getMethodName().substring(3);
			}

			signature = CommandSignatures.getSignature(cmdName, app1);
			if (signature != null) {
				syntaxes = signature.size();
			}
			Log.debug(cmdName);
		}
		syntaxes--;
		AlgebraTestHelper.checkSyntaxSingle(s, expected, proc, tpl);
	}

	protected static void tRound(String s, String... expected) {
		t(s, StringTemplate.editTemplate, expected);
	}

	protected static void t(String s, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	protected static void t(String s, StringTemplate tpl, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				tpl);
	}

	protected static void t(String s, Matcher<String> expected) {
		testSyntax(s, Collections.singletonList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	protected static void intersect(String arg1, String arg2, boolean num,
			String... results) {
		intersect(arg1, arg2, num, num, results);
	}

	protected static void intersect(String arg1, String arg2, boolean num,
			boolean closest, String... results) {
		app.getKernel().clearConstruction(true);
		app.getKernel().getConstruction().setSuppressLabelCreation(false);
		tRound("its:=Intersect(" + arg1 + "," + arg2 + ")", results);
		GeoElement geo = get("its") == null ? get("its_1") : get("its");
		boolean symmetric = geo != null
				&& !(geo.getParentAlgorithm() instanceof AlgoIntersectPolyLines
				&& geo.getParentAlgorithm().getOutput(0)
				.getGeoClassType() == geo.getParentAlgorithm()
				.getOutput(1).getGeoClassType());
		if (symmetric) {
			tRound("Intersect(" + arg2 + "," + arg1 + ")", results);
		}
		if (num) {
			tRound("Intersect(" + arg1 + "," + arg2 + ",1)", results[0]);
			if (symmetric) {
				tRound("Intersect(" + arg2 + "," + arg1 + ",1)", results[0]);
			}
		}
		if (closest) {
			tRound("Intersect(" + arg1 + "," + arg2 + "," + results[0] + ")",
					results[0]);
		}
	}

	protected static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}
}
