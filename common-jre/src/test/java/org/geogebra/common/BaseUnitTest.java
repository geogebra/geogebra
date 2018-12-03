package org.geogebra.common;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.AppCommon;
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
    public void setup() {
        app = new AppCommon();
        kernel = app.getKernel();
        construction = kernel.getConstruction();
        elementFactory = new GeoElementFactory(this);
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
}
