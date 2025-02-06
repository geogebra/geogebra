package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter implements Printer {

    private final GeneralSettings settings;

    public CartesianPrinter(GeneralSettings settings) {
        this.settings = settings;
    }

    @Override
    public String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl) {
        if (tpl.getStringType().isGiac()) {
            return GiacPrinter.print(tpl, xCoord, yCoord, vector);
        }
        if (tpl.usePointTemplate()) {
            String fn = settings.getPointEditorTemplate();
            return fn + '('
                    + xCoord
                    + ','
                    + yCoord
                    + ')';
        }
        return printLeftParenthesis(tpl)
                + xCoord
                + tpl.getCartesianDelimiter(settings)
                + yCoord
                + printRightParenthesis(tpl);
    }

    private String printLeftParenthesis(StringTemplate tpl) {
        return tpl.leftBracket();
    }

    private String printRightParenthesis(StringTemplate tpl) {
        return tpl.rightBracket();
    }

}
