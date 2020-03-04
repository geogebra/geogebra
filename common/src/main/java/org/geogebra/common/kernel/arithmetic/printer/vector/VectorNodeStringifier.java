package org.geogebra.common.kernel.arithmetic.printer.vector;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.printer.expression.DefaultExpressionPrinter;
import org.geogebra.common.kernel.arithmetic.printer.expression.ExpressionPrinter;
import org.geogebra.common.kernel.arithmetic.printer.expression.ValueExpressionPrinter;

import java.util.EnumMap;
import java.util.Map;

public class VectorNodeStringifier {

    public enum PrintingMode {Default, Cartesian, Polar, Giac, CasLatex, Vector}

    private MyVecNode vector;

    private Map<PrintingMode, Printer> printerMap;
    private Printer activePrinter;

    private ExpressionPrinter defaultExpressionPrinter;
    private ExpressionPrinter valueExpressionPrinter;

    public VectorNodeStringifier(MyVecNode vector) {
        this.vector = vector;
        initStructurePrinters();
        initExpressionPrinters();
    }

    private void initStructurePrinters() {
        printerMap = new EnumMap<>(PrintingMode.class);
        printerMap.put(PrintingMode.Default, new DefaultPrinter(vector));
        printerMap.put(PrintingMode.Cartesian, new CartesianPrinter(vector));
        printerMap.put(PrintingMode.Vector, new VectorPrinter(vector));
        printerMap.put(PrintingMode.Polar, new PolarPrinter(vector));
        printerMap.put(PrintingMode.Giac, new GiacPrinter(vector));
        printerMap.put(PrintingMode.CasLatex, new CasLatexPrinter(vector));

        activePrinter = printerMap.get(PrintingMode.Default);
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
            return vector.getCoordinationSystem() == Kernel.COORD_POLAR
                    ? printerMap.get(PrintingMode.Polar).print(tpl, expressionPrinter)
                    : printerMap.get(PrintingMode.Giac).print(tpl, expressionPrinter);
        } else {
            return vector.isCASVector()
                    && tpl.getStringType() == ExpressionNodeConstants.StringType.LATEX
                    ? printerMap.get(PrintingMode.CasLatex).print(tpl, expressionPrinter)
                    : activePrinter.print(tpl, expressionPrinter);
        }
    }

    public void setPrintingMode(PrintingMode printingMode) {
        activePrinter = printerMap.get(printingMode);
    }
}
