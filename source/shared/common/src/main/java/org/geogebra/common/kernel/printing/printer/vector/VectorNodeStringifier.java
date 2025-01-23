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

    private PrintableVector vector;

    private Map<VectorPrintingMode, ? extends Printer> printerMap;
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

    public String toString(StringTemplate tpl) {
        return activePrinter.print(tpl, defaultExpressionPrinter, vector);
    }

    public String toString(StringTemplate tpl, VectorPrintingMode mode) {
        return printerMap.get(mode).print(tpl, defaultExpressionPrinter, vector);
    }

    public String toValueString(StringTemplate tpl) {
        return activePrinter.print(tpl, valueExpressionPrinter, vector);
    }

    public void setPrintingMode(VectorPrintingMode printingMode) {
        activePrinter = printerMap.get(printingMode);
    }
}
