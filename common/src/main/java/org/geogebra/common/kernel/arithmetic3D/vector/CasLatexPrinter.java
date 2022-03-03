package org.geogebra.common.kernel.arithmetic3D.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;

class CasLatexPrinter implements Printer {

    private PrintableVector vector;

    CasLatexPrinter(PrintableVector vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return "\\left( \\begin{tabular}{r}"
                + expressionPrinter.print(vector.getX(), tpl)
                + "\\\\"
                + expressionPrinter.print(vector.getY(), tpl)
                + "\\\\ "
                + expressionPrinter.print(vector.getZ(), tpl)
                + "\\\\ \\end{tabular} \\right) ";
    }
}
