/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic.vector;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter implements Printer {

    private final @CheckForNull GeneralSettings settings;

    CartesianPrinter(@CheckForNull GeneralSettings settings) {
        this.settings = settings;
    }

    @Override
    public String print(String xCoord, String yCoord, String zCoord,
            PrintableVector vector, StringTemplate tpl) {
        if (tpl.getStringType().isGiac()) {
            return GiacPrinter.print(tpl, xCoord, yCoord, vector);
        }
        if (tpl.usePointTemplate() && settings != null) {
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
