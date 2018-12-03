package org.geogebra.common.kernel.parser.stringparser;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.ParseException;

public class StringParser {

    private AlgebraProcessor algebraProcessor;

    public StringParser(AlgebraProcessor algebraProcessor) {
        this.algebraProcessor = algebraProcessor;
    }

    /**
     * @param string The text that contains a number.
     * @return If the string can be converted into a double (other than NaN) then returns the double value,
     *      otherwise throws a ParseException.
     */
	public double convertToDouble(String string) {
        try {
            double doubleValue = algebraProcessor.convertToDouble(string);
            if (Double.isNaN(doubleValue)) {
                throw new NumberFormatException("The value is NaN");
            }
            return doubleValue;
		} catch (ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }
}
