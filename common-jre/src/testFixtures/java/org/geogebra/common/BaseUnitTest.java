package org.geogebra.common;

import static org.junit.Assert.fail;

import java.util.function.Function;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for unit tests.
 */
public class BaseUnitTest {

	/** allowed error for double comparison */
	protected static final double DELTA = 1E-15;

	private Kernel kernel;
	private Construction construction;
	private AppCommon app;
	private GeoElementFactory elementFactory;
	private ErrorHandler errorHandler = TestErrorHandler.INSTANCE;
	private static TypeSafeMatcher<GeoElementND> isDefined;

    /**
     * Set up test class before every test.
     */
    @Before
	public void setup() {
		app = createAppCommon();
        kernel = app.getKernel();
        construction = kernel.getConstruction();
        elementFactory = new GeoElementFactory(this);
    }

	/**
	 * Clean up after every test.
	 */
	@After
	public void teardown() {
	}

	/**
	 * @return app instance for 2D testing
	 */
	public AppCommon createAppCommon() {
		return AppCommonFactory.create();
	}

	/**
	 * Get the kernel.
	 *
	 * @return kernel
	 */
    protected Kernel getKernel() {
        return kernel;
    }

    /**
     * Get the construction.
     *
     * @return construction
     */
    protected Construction getConstruction() {
        return construction;
    }

    /**
     * Get the app.
     *
     * @return app
     */
    protected AppCommon getApp() {
        return app;
    }

	/**
	 * Get the algebra processor.
	 *
	 * @return algebra processor
	 */
	protected AlgebraProcessor getAlgebraProcessor() {
		return kernel.getAlgebraProcessor();
	}

	protected Settings getSettings() {
		return app.getSettings();
	}

    /**
     * Get the geo element factory. Use this class to create GeoElements.
     *
     * @return geo element factory
     */
    protected GeoElementFactory getElementFactory() {
        return elementFactory;
    }

	/**
	 * Get the localization.
	 *
	 * @return localization
	 */
	protected Localization getLocalization() {
		return app.getLocalization();
	}

	/** Set the error handler used for processing algebra input */
	protected void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/** Resets the error handler to the original instance */
	protected void resetErrorHandler() {
		errorHandler = TestErrorHandler.INSTANCE;
	}

	/**
	 * Use this method when you want to test the commands as if those were read from file.
	 *
	 * @param command
	 *            algebra input to be processed
	 * @return resulting element
	 */
	protected <T extends GeoElementND> T add(String command) {
		GeoElementND[] geoElements =
				getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(command, false,
						errorHandler, false, null);
		return getFirstElement(geoElements);
	}

	private <T extends GeoElementND> T getFirstElement(GeoElementND[] geoElements) {
		if (geoElements != null) {
			return geoElements.length == 0 ? null : (T) geoElements[0];
		} else {
			return null;
		}
	}

	/**
	 * Use this method when you want to test the commands as if those were inserted in AV.
	 *
	 * @param command
	 *            algebra input to be processed
	 * @return resulting element
	 */

	protected <T extends GeoElement> T addAvInput(String command) {
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(app, false);
		return add(command, info);
	}

	/**
	 * Use this method when you want to test the commands with a specific EvalInfo.
	 *
	 * @param command algebra input to be processed
	 * @param info EvalInfo to pass
	 *         to the AlgebraProcessor.processAlgebraCommandNoExceptionHandling method.
	 * @return resulting element
	 */
	protected <T extends GeoElement> T add(String command, EvalInfo info) {
		GeoElementND[] geoElements = getElements(command, info);
		return getFirstElement(geoElements);
	}

	protected void t(String input, String... expected) {
		AlgebraTestHelper.checkSyntaxSingle(input, expected,
				getApp().getKernel().getAlgebraProcessor(),
				StringTemplate.xmlTemplate);
	}

	protected <T extends GeoElementND> T[] getElements(String command) {
		return getElements(command, EvalInfoFactory.getEvalInfoForAV(app, false));
	}

	private <T extends GeoElementND> T[] getElements(String command, EvalInfo info) {
		return (T[]) getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(
						command,
						false,
						getSliderErrorHandler(info),
						info,
						null);
	}

	private ErrorHandler getSliderErrorHandler(EvalInfo info) {
		if (!info.isAutocreateSliders()) {
			return app.getErrorHandler();
		}
		return new ErrorHandler() {
			@Override
			public void showError(String msg) {
				fail(msg);
			}

			@Override
			public void showCommandError(String command, String message) {
				fail(message);
			}

			@Override
			public String getCurrentCommand() {
				return null;
			}

			@Override
			public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
				return true;
			}

			@Override
			public void resetError() {
				// nothing to do
			}
		};
	}

	/**
	 * @param label
	 *            label
	 * @return object with given label
	 */
	protected GeoElement lookup(String label) {
		return kernel.lookupLabel(label);
	}

	/**
	 * @return matcher for defined elements
	 */
	public static TypeSafeMatcher<GeoElementND> isDefined() {
		if (isDefined == null) {
			isDefined = new TypeSafeMatcher<GeoElementND>() {
				@Override
				protected boolean matchesSafely(GeoElementND item) {
					return item.isDefined();
				}

				@Override
				public void describeTo(Description description) {
					description.appendText("defined");
				}
			};
		}
		return isDefined;
	}

	/**
	 * @param val expected value (for default template)
	 * @return construction element matcher
	 */
	public static TypeSafeMatcher<ExpressionValue> hasValue(String val) {
		return hasProperty("value", geo -> geo.toValueString(StringTemplate.defaultTemplate), val);
	}

	/**
	 * @param propName property name
	 * @param prop property getter
	 * @param val expected value
	 * @return matcher for generic property
	 * @param <A> object type
	 * @param <B> property type
	 */
	public static <A, B> TypeSafeMatcher<A> hasProperty(String propName,
			Function<A, B> prop, B val) {
		return new TypeSafeMatcher<A>() {
			@Override
			protected boolean matchesSafely(A item) {
				return val.equals(prop.apply(item));
			}

			@Override
			public void describeMismatchSafely(A item, Description description) {
				description.appendText("had " + propName + " ").appendValue(prop.apply(item));
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("has " + propName + " ").appendValue(val);
			}
		};
	}

	protected void reload() {
		app.setXML(app.getXML(), true);
	}

	protected void activateUndo() {
		app.setUndoRedoMode(UndoRedoMode.GUI);
		app.setUndoActive(true);
	}

	protected Drawable getDrawable(GeoElementND geo) {
		return (Drawable) app.getActiveEuclidianView().getDrawableFor(geo);
	}
}
