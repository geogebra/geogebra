package org.geogebra.common.kernel.printing.printer.expression;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public interface ExpressionPrinter {

    String print(ExpressionValue expression, StringTemplate tpl);
}
