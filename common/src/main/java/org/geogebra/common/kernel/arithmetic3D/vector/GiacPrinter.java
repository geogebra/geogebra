package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class GiacPrinter implements Printer {

    private PrintableVector vector;

    GiacPrinter(PrintableVector vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return (vector.isCASVector() ? "ggbvect[" : "point(")
                + expressionPrinter.print(vector.getX(), tpl)
                + ','
                + expressionPrinter.print(vector.getY(), tpl)
                + ','
                + expressionPrinter.print(vector.getZ(), tpl)
                + (vector.isCASVector() ? "]" : ")");
    }
}
