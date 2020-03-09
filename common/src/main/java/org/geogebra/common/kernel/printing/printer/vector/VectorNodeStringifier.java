package org.geogebra.common.kernel.printing.printer.vector;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.DefaultExpressionPrinter;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.kernel.printing.printer.expression.ValueExpressionPrinter;

import java.util.Map;

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
        activePrinter = printerMap.get(VectorPrintingMode.Default);
    }

    private void initExpressionPrinters() {
        defaultExpressionPrinter = new DefaultExpressionPrinter();
        valueExpressionPrinter = new ValueExpressionPrinter();
    }

    public String toString(StringTemplate tpl) {
        return print(tpl, defaultExpressionPrinter);
    }

    public String toValueString(StringTemplate tpl) {
        return print(tpl, valueExpressionPrinter);
    }

    private String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        if (tpl.getStringType() == ExpressionNodeConstants.StringType.GIAC) {
            int coordinateSystem = vector.getCoordinateSystem();
            return coordinateSystem == Kernel.COORD_POLAR
                    || coordinateSystem == Kernel.COORD_SPHERICAL
                    ? printerMap.get(VectorPrintingMode.Spherical).print(tpl, expressionPrinter)
                    : printerMap.get(VectorPrintingMode.Giac).print(tpl, expressionPrinter);
        } else {
            return vector.isCASVector()
                    && tpl.getStringType() == ExpressionNodeConstants.StringType.LATEX
                    ? printerMap.get(VectorPrintingMode.CasLatex).print(tpl, expressionPrinter)
                    : activePrinter.print(tpl, expressionPrinter);
        }
    }

    public void setPrintingMode(VectorPrintingMode printingMode) {
        activePrinter = printerMap.get(printingMode);
    }
}
