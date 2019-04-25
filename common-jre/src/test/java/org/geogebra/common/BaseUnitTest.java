package org.geogebra.common;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Before;

/**
 * Base class for unit tests.
 */
public class BaseUnitTest {

    protected static final double DELTA = 1E-15;

    private Kernel kernel;
    private Construction construction;
    private AppCommon app;
    private GeoElementFactory elementFactory;

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
	public static AppCommon createAppCommon() {
		return new AppCommon(new LocalizationCommon(2), new AwtFactoryCommon());
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

	protected GeoElement add(String string) {
		GeoElementND[] ret = getApp().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(string, false);
		return ret[0].toGeoElement();
	}
}
