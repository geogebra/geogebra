package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

class PolarPrinter implements Printer {

    @Override
    public String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl) {
        if (tpl.getStringType().isGiac()) {
            return "point(("
                    + xCoord
                    + ")*exp(i*("
                    + yCoord
                    + ")))";
        }
        return printLeftParenthesis(tpl)
                + xCoord
                + printDelimiter()
                + yCoord
                + printRightParenthesis(tpl);
    }

    private String printLeftParenthesis(StringTemplate tpl) {
        return tpl.leftBracket();
    }

    private String printRightParenthesis(StringTemplate tpl) {
        return tpl.rightBracket();
    }

    private String printDelimiter() {
        return "; ";
    }
}
