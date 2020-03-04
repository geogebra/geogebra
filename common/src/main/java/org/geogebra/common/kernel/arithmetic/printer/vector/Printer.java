package org.geogebra.common.kernel.arithmetic.printer.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.printer.expression.ExpressionPrinter;

public interface Printer {

    String print(StringTemplate tpl, ExpressionPrinter expressionPrinter);
}
