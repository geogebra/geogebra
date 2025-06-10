package org.geogebra.common;

import javax.annotation.Nonnull;

import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.test.commands.ErrorAccumulator;

/**
 * Base test class for initializing any app,
 * providing convenience getters for accessing the most commonly used objects owned by the app,
 * as well as basic functionality for evaluating expressions.
 * @apiNote The purpose of this class is to provide basic setup to avoid repetition and boilerplate
 * in test files. This makes the related tests somewhat dependent, which is why this class should be
 * kept as simple and "thin" as possible. Logic should only be extracted if it is used frequently
 * and/or requires extensive setup to achieve basic functionality, while keeping it as
 * {@code private} and {@code final} as possible. This class is designed to be inherited by test
 * files or other basic test setup classes, but only if absolutely necessary.
 */
public class BaseAppTestSetup {
	private AppCommon app;
	protected final ErrorAccumulator errorAccumulator = new ErrorAccumulator();
	protected final MockedCasGiac mockedCasGiac = new MockedCasGiac();

	// Initial app setup

	protected void setupApp(SuiteSubApp subApp) {
		app = AppCommonFactory.create(createConfig(subApp));
		if (subApp == SuiteSubApp.CAS) {
			mockedCasGiac.applyTo(app);
		}
		app.getSettingsUpdater().resetSettingsOnAppStart();
	}

	private static AppConfig createConfig(SuiteSubApp subApp) {
		switch (subApp) {
		case CAS:
			return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
		case GRAPHING:
			return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
		case GEOMETRY:
			return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
		case SCIENTIFIC:
			return new AppConfigScientific(GeoGebraConstants.SUITE_APPCODE);
		case G3D:
			return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
		case PROBABILITY:
			return new AppConfigProbability(GeoGebraConstants.SUITE_APPCODE);
		}
		return null;
	}

	// Convenience getters for the most used app owned objects

	protected final @Nonnull AppCommon getApp() {
		if (app == null) {
			throw new Error("App is not initialized, \"setupApp\" should be called first.");
		}
		return app;
	}

	protected final @Nonnull Kernel getKernel() {
		return getApp().getKernel();
	}

	protected final @Nonnull AlgebraProcessor getAlgebraProcessor() {
		return getApp().getKernel().getAlgebraProcessor();
	}

	protected final @Nonnull CommandDispatcher getCommandDispatcher() {
		return getApp().getKernel().getAlgebraProcessor().getCommandDispatcher();
	}

	protected final @Nonnull AlgebraSettings getAlgebraSettings() {
		return getApp().getSettings().getAlgebra();
	}

	// Basic functionalities for evaluating expressions

	protected final GeoElementND[] evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return app.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				expression, false, errorAccumulator, evalInfo, null);
	}

	protected final <T extends GeoElementND> T evaluateGeoElement(String expression) {
		return (T) evaluate(expression)[0];
	}

	protected final void editGeoElement(GeoElement geoElement, String newExpression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForRedefinition(
				app.getKernel(), geoElement, true);
		app.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(
				geoElement, newExpression, evalInfo, false, null, errorAccumulator);
	}
}
