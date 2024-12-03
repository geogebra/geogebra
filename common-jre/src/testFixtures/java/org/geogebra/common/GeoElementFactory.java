package org.geogebra.common;

import static org.junit.Assert.assertFalse;

import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.scientific.LabelController;

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
		return (GeoLine) create("x=y");
	}

	/**
	 * Create a GeoLine with Command
	 *
	 * @return line
	 */
	public GeoLine createGeoLineWithCommand() {
		return (GeoLine) create("Line((1,1),(2,2))");
	}

	/**
	 * Create a GeoRay.
	 *
	 * @return ray
	 */
	public GeoRay createGeoRayWithCommand() {
		return (GeoRay) create("Ray((1,1),(2,2))");
	}

	/**
	 * @param definition
	 *            definition
	 * @return element added to construction
	 */
	public GeoElementND create(String definition) {
		AlgebraProcessor processor = unitTest.getKernel().getAlgebraProcessor();
		return processor.processAlgebraCommand(definition, false)[0];
	}

    /**
     * Create a GeoFunction based on function definition.
     *
     * @param definition function definition
     * @return function
     */
    public GeoFunction createFunction(String definition) {
		return (GeoFunction) create(definition);
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

	/**
	 * @return line with a hidden label
	 */
	public GeoElement createLineNoLabel() {
		GeoElement line = createGeoLine();
		LabelController labelController = new LabelController();
		labelController.hideLabel(line);
		assertFalse(line.isAlgebraLabelVisible());
		return line;
	}

	/**
	 * @param number number of lines
	 * @return multiple default lines
	 */
	public GeoLine[] createLines(int number) {
		GeoLine[] lines = new GeoLine[number];
		for (int i = 0; i < number; i++) {
			lines[i] = createGeoLine();
		}
		return lines;
	}
}
