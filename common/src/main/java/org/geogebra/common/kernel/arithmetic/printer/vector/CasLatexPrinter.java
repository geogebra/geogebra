package org.geogebra.common.kernel.arithmetic.printer.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.printer.expression.ExpressionPrinter;

class CasLatexPrinter implements Printer {

    private MyVecNDNode vector;

    CasLatexPrinter(MyVecNDNode vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return " \\binom{"
                + expressionPrinter.print(vector.getX(), tpl)
                + "}{"
                + expressionPrinter.print(vector.getY(), tpl)
                + "}";
    }
}
