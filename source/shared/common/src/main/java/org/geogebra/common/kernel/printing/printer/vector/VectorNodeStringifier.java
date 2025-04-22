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
