package org.geogebra.common;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.AppCommon;
import org.junit.Before;

/**
 * Base class for unit tests.
 */
public class BaseUnitTest {

    private Kernel kernel;
    private Construction construction;
    private AppCommon app;
    private GeoElementFactory elementFactory;

    @Before
    public void setup() {
        app = new AppCommon();
        kernel = app.getKernel();
        construction = kernel.getConstruction();
        elementFactory = new GeoElementFactory(this);
    }

    protected Kernel getKernel() {
        return kernel;
    }

    protected Construction getConstruction() {
        return construction;
    }

    protected AppCommon getApp() {
        return app;
    }

    protected GeoElementFactory getElementFactory() {
        return elementFactory;
    }
}
