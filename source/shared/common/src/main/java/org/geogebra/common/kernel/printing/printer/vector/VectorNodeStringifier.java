/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.printing.printer.vector;

import java.util.Map;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.DefaultExpressionPrinter;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.kernel.printing.printer.expression.ValueExpressionPrinter;

/**
 * Delegate for printing the MyVecNode and MyVec3DNode objects.
 */
public class VectorNodeStringifier {

    private final PrintableVector vector;

    private final Map<VectorPrintingMode, ? extends Printer> printerMap;
    private Printer activePrinter;

    private ExpressionPrinter defaultExpressionPrinter;
    private ExpressionPrinter valueExpressionPrinter;

    /**
     * @param vector vector
     * @param printerMap map of the printers
     */
    public VectorNodeStringifier(PrintableVector vector,
                                 Map<VectorPrintingMode, ? extends Printer> printerMap) {
        this.vector = vector;
        this.printerMap = printerMap;
        initPrinters();
        initExpressionPrinters();
    }

    private void initPrinters() {
        activePrinter = printerMap.get(VectorPrintingMode.Polar);
    }

    private void initExpressionPrinters() {
        defaultExpressionPrinter = new DefaultExpressionPrinter();
        valueExpressionPrinter = new ValueExpressionPrinter();
    }

    /**
     * Get definition string representation.
     * @param tpl template
     * @return definition string
     */
    public String toString(StringTemplate tpl) {
        return printVector(activePrinter, defaultExpressionPrinter, vector, tpl);
    }

    private String printVector(Printer activePrinter, ExpressionPrinter coordPrinter,
            PrintableVector vector, StringTemplate tpl) {
        return activePrinter.print(coordPrinter.print(vector.getX(), tpl),
                coordPrinter.print(vector.getY(), tpl),
                vector.getZ() == null ? "0" : coordPrinter.print(vector.getZ(), tpl),
                vector, tpl);
    }

    /**
     * Get definition string representation.
     * @param tpl template
     * @param mode printing mode
     * @return definition string
     */
    public String toString(StringTemplate tpl, VectorPrintingMode mode) {
        return printVector(printerMap.get(mode), defaultExpressionPrinter, vector, tpl);
    }

    /**
     * Get vector's value string representation.
     * @param tpl string template
     * @return value string
     */
    public String toValueString(StringTemplate tpl) {
        return printVector(activePrinter, valueExpressionPrinter, vector, tpl);
    }

    public void setPrintingMode(VectorPrintingMode printingMode) {
        activePrinter = printerMap.get(printingMode);
    }
}
