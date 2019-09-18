package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Compares two objects geometrically by using real quantifier elimination.
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoCompare extends AlgoElement {

    private GeoElement inputElement1; // input
    private GeoElement inputElement2; // input

    private GeoText outputText; // output

    /**
     * Compares two objects
     *
     * @param cons          The construction the objects depend on
     * @param inputElement1 the first object
     * @param inputElement2 the second object
     */
    public AlgoCompare(Construction cons, GeoElement inputElement1,
                       GeoElement inputElement2) {
        super(cons);
        this.inputElement1 = inputElement1;
        this.inputElement2 = inputElement2;

        outputText = new GeoText(cons);

        setInputOutput();
        compute();

    }

    /**
     * Compares two objects
     *
     * @param cons          The construction the objects depend on
     * @param label         the label for the AlgoAreCompare object
     * @param inputElement1 the first object
     * @param inputElement2 the second object
     */
    public AlgoCompare(Construction cons, String label,
                       GeoElement inputElement1, GeoElement inputElement2) {
        this(cons, inputElement1, inputElement2);
        outputText.setLabel(label);
    }

    @Override
    public Commands getClassName() {
        return Commands.Compare;
    }

    @Override
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inputElement1;
        input[1] = inputElement2;

        super.setOutputLength(1);
        super.setOutput(0, outputText);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Gets the result of the test
     *
     * @return the result of comparison
     */

    public GeoText getResult() {
        return outputText;
    }

    @Override
    public final void compute() {
        outputText.setTextString("not implemented");
    }

}
