package org.geogebra.common;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;

/**
 * Class for creating geo elements.
 */
public class GeoElementFactory {

    private BaseUnitTest unitTest;

    public GeoElementFactory(BaseUnitTest unitTest) {
        this.unitTest = unitTest;
    }

    public GeoLine createGeoLine() {
        return new GeoLine(unitTest.getConstruction());
    }

    public GeoFunction createFunction(String definition) {
        Kernel kernel = unitTest.getKernel();
        AlgebraProcessor processor = kernel.getAlgebraProcessor();
        return processor.evaluateToFunction(definition, true);
    }
}
