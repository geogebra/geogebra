package org.geogebra.common.kernel.commands;

import static com.himamis.retex.editor.share.util.Unicode.DEGREE_STRING;
import static com.himamis.retex.editor.share.util.Unicode.IMAGINARY;
import static com.himamis.retex.editor.share.util.Unicode.PI_STRING;
import static com.himamis.retex.editor.share.util.Unicode.theta_STRING;
import static org.geogebra.common.BaseUnitTest.isDefined;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLines;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

@SuppressWarnings("javadoc")
public class CommandsTestCommon {
	static AppCommon3D app;
	static AlgebraProcessor ap;
	static List<Integer> signature;
	protected static int syntaxes = -1000;

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
				AlgebraTestHelper.dummySyntaxesShouldFail(cmdName, signature,
						app1);
			}
			Log.debug(cmdName);
		}
		syntaxes--;
		AlgebraTestHelper.checkSyntaxSingle(s, expected, proc, tpl);
	}

	@Before
	public void resetSyntaxes() {
		resetSyntaxCounter();
		app.getKernel().clearConstruction(true);
		app.setActiveView(App.VIEW_EUCLIDIAN);
		GeoImplicitCurve.setFastDrawThreshold(10000);
	}

	public static void resetSyntaxCounter() {
		syntaxes = -1000;
	}

	@After
	public void checkSyntaxes() {
		checkSyntaxesStatic();
	}

	/**
	 * Assert that there are no unchecked syntaxes left
	 */
	public static void checkSyntaxesStatic() {
		Assert.assertTrue("unchecked syntaxes: " + syntaxes + signature,
				syntaxes <= 0);
	}

	/**
	 * Create the app
	 */
	@BeforeClass
	public static void setupApp() {
		app = AppCommonFactory.create3D();
		ap = app.getKernel().getAlgebraProcessor();
		app.setRandomSeed(42);
	}

	protected static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}

	protected static String deg(String string) {
		return string + "*" + DEGREE_STRING;
	}


}
