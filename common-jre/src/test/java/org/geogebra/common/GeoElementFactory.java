package org.geogebra.common;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;

/**
 * Class for creating geo elements.
 */
public class GeoElementFactory {

    private BaseUnitTest unitTest;

    /**
     * Constructs a GeoElementFactory.
     *
     * @param unitTest the test class
     */
    public GeoElementFactory(BaseUnitTest unitTest) {
        this.unitTest = unitTest;
    }

    /**
     * Create a GeoLine.
     *
     * @return line
     */
    public GeoLine createGeoLine() {
        return new GeoLine(unitTest.getConstruction());
    }

    /**
     * Create a GeoFunction based on function definition.
     *
     * @param definition function definition
     * @return function
     */
    public GeoFunction createFunction(String definition) {
        Kernel kernel = unitTest.getKernel();
        AlgebraProcessor processor = kernel.getAlgebraProcessor();
        return processor.evaluateToFunction(definition, true);
    }

    /**
     * Create a function based on a Function object.
     *
     * @param function function object
     * @return function
     */
    public GeoFunction createFunction(Function function) {
        return new GeoFunction(unitTest.getConstruction(), function);
    }
}
