package org.geogebra.common.kernel.parser.stringparser;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.ParseException;

/**
 * String to number converter based on GGB parser
 */
public class StringParser {

    private AlgebraProcessor algebraProcessor;

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 */
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
            return algebraProcessor.convertToDouble(string);
		} catch (ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }
}
