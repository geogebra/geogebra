package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter implements Printer {

    private final GeneralSettings settings;

    public CartesianPrinter(GeneralSettings settings) {
        this.settings = settings;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter,
            PrintableVector vector) {
        if (tpl.getStringType().isGiac()) {
            return GiacPrinter.print(tpl, expressionPrinter, vector);
        }
        return printLeftParenthesis(tpl)
                + expressionPrinter.print(vector.getX(), tpl)
                + tpl.getCartesianDelimiter(settings)
                + expressionPrinter.print(vector.getY(), tpl)
                + printRightParenthesis(tpl);
    }

    private String printLeftParenthesis(StringTemplate tpl) {
        return tpl.leftBracket();
    }

    private String printRightParenthesis(StringTemplate tpl) {
        return tpl.rightBracket();
    }

}
