package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class CartesianPrinter implements Printer {

    private PrintableVector vector;

    CartesianPrinter(PrintableVector vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return tpl.leftBracket()
                + expressionPrinter.print(vector.getX(), tpl)
                + ","
                + tpl.getOptionalSpace()
                + expressionPrinter.print(vector.getY(), tpl)
                + ","
                + tpl.getOptionalSpace()
                + expressionPrinter.print(vector.getZ(), tpl)
                + tpl.rightBracket();
    }
}
