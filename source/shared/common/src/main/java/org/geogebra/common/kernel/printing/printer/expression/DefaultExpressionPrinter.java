package org.geogebra.common.kernel.printing.printer.expression;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoElement;

public class DefaultExpressionPrinter implements ExpressionPrinter {

    @Override
    public String print(ExpressionValue expression, StringTemplate tpl) {
        return expression.isGeoElement()
                ? ((GeoElement) expression).getLabel(tpl)
                : expression.toString(tpl);
    }
}
