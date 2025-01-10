package org.geogebra.common.kernel.printing.printer.expression;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public class ValueExpressionPrinter implements ExpressionPrinter {

    @Override
    public String print(ExpressionValue expression, StringTemplate tpl) {
        return expression.toValueString(tpl);
    }
}
