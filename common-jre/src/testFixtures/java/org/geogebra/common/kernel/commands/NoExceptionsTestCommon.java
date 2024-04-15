package org.geogebra.common.kernel.commands;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public class NoExceptionsTestCommon {
	static AppCommon app;
	static AlgebraProcessor ap;
	private static int syntaxes;

	/**
	 * Create app + basic test objects
	 */
	@BeforeClass
	public static void setupApp() {
		app = AppCommonFactory.create3D();
		app.setLanguage("en_US");
		ap = app.getKernel().getAlgebraProcessor();
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
	}

	@Before
	public void resetSyntaxes() {
		syntaxes = -1000;
	}

	@After
	public void checkSyntaxes() {
		Assert.assertTrue("unchecked syntaxes: " + syntaxes, syntaxes <= 0);
	}

	protected static void t(String s) {
		testSyntax(s, app, ap);
	}

	private static void testSyntax(String s, App app, AlgebraProcessor ap) {
		app.getEuclidianView1().getEuclidianController().clearZoomerAnimationListeners();
		if (syntaxes == -1000) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			List<Integer> signature = CommandSignatures.getSignature(cmdName, app);
			syntaxes = 0;
			if (signature != null) {
				syntaxes = signature.size();
				AlgebraTestHelper.dummySyntaxesShouldFail(cmdName, signature,
						app);
			}

			Log.debug(cmdName + " ");
		}
		try {
			Assert.assertNotNull(ap.processAlgebraCommandNoExceptionHandling(s,
					false, TestErrorHandler.INSTANCE, false, null));
			syntaxes--;
			Log.debug("+");
		} catch (final Throwable e) {
			Log.error("error occured:" + e.getClass().getName());
			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}

			syntaxes--;
			Assert.assertNull(e.getMessage() + "," + e.getClass(), e);
		}
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

	/**
	 * Check that all objects can be saved and reloaded.
	 */
	@AfterClass
	public static void checkSaving() {
		XmlTestUtil.checkCurrentXML(app);

		Construction cons = app.getKernel().getConstruction();
		cons.initUndoInfo();
		cons.getUndoManager().undo();
		cons.getUndoManager().redo();
	}
}