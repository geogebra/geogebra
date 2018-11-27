package org.geogebra.common.kernel.parser.stringparser;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyParseError;

public class StringParser {

    private Localization localization;
    private AlgebraProcessor algebraProcessor;

    public StringParser(App app) {
        localization = app.getLocalization();
        algebraProcessor = app.getKernel().getAlgebraProcessor();
    }

    public double convertToDouble(String string) {
        try {
            return algebraProcessor.convertToDouble(string);
        } catch (MyParseError | ParseException e) {
            throw new NumberFormatException(
                    localization.getError("InputError.Enter_a_number"));
        }
    }

    public double convertToPositiveDouble(String string) {
        double positiveDouble = convertToDouble(string);
        if (positiveDouble < 0) {
            throw new NumberFormatException(
                    localization.getError("InputError.Enter_a_number_greater_than_0"));
        }
        return positiveDouble;
    }
}
