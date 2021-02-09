package org.geogebra.common;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
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
	private TypeSafeMatcher<GeoElementND> isDefined;

    /**
     * Setup test class before every test.
     */
    @Before
	public final void setup() {
		app = createAppCommon();
        kernel = app.getKernel();
        construction = kernel.getConstruction();
        elementFactory = new GeoElementFactory(this);
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

	/**
	 * Use this method when you want to test the commands as if those were read from file.
	 *
	 * @param command
	 *            algebra input to be processed
	 * @return resulting element
	 */
	protected <T extends GeoElementND> T add(String command) {
		GeoElementND[] geoElements =
				getAlgebraProcessor().processAlgebraCommand(command, false);
		return getFirstElement(geoElements);
	}

	private AlgebraProcessor getAlgebraProcessor() {
		return getApp().getKernel().getAlgebraProcessor();
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
	 * @param command
	 * 			algebra input to be processed
	 * @param info
	 * 			EvalInfo to pass to the AlgebraProcessor.processAlgebraCommandNoExceptionHandling
	 * 			method.
	 * @return resulting element
	 */
	protected <T extends GeoElement> T add(String command, EvalInfo info) {
		GeoElementND[] geoElements = getElements(command, info);
		return getFirstElement(geoElements);
	}

	protected <T extends GeoElementND> T[] getElements(String command) {
		return getElements(command, EvalInfoFactory.getEvalInfoForAV(app, false));
	}

	private <T extends GeoElementND> T[] getElements(String command, EvalInfo info) {
		return (T[]) getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(
						command,
						false,
						app.getErrorHandler(),
						info,
						null);
	}

	/**
	 * @param label
	 *            label
	 * @return object with given label
	 */
	protected GeoElement lookup(String label) {
		return kernel.lookupLabel(label);
	}

	protected TypeSafeMatcher<GeoElementND> isDefined() {
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
}
