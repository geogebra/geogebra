package org.geogebra.common.kernel.arithmetic.printer.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.printer.expression.ExpressionPrinter;

class VectorPrinter implements Printer {

    private MyVecNDNode vector;

    VectorPrinter(MyVecNDNode vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        return printLeftParenthesis(tpl)
                + expressionPrinter.print(vector.getX(), tpl)
                + printDelimiter(tpl)
                + expressionPrinter.print(vector.getY(), tpl)
                + printRightParenthesis(tpl);
    }

    private String printLeftParenthesis(StringTemplate tpl) {
        return tpl == StringTemplate.editorTemplate ? "{{" : tpl.leftBracket();
    }

    private String printRightParenthesis(StringTemplate tpl) {
        return tpl == StringTemplate.editorTemplate ? "}}" : tpl.leftBracket();
    }

    private String printDelimiter(StringTemplate tpl) {
        return tpl == StringTemplate.editorTemplate ? "}, {" : "; ";
    }
}
