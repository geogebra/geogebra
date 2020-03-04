package org.geogebra.common.kernel.arithmetic.printer.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.printer.expression.ExpressionPrinter;

class PolarPrinter implements Printer {

    private MyVecNDNode vector;

    PolarPrinter(MyVecNDNode vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return "point(("
                + expressionPrinter.print(vector.getX(), tpl)
                + ")*cos(" +
                expressionPrinter.print(vector.getY(), tpl)
                + "),("
                + expressionPrinter.print(vector.getX(), tpl)
                + ")*sin("
                + expressionPrinter.print(vector.getY(), tpl)
                + "))";
    }
}
