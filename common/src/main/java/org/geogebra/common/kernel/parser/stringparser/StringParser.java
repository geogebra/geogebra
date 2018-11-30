package org.geogebra.common.kernel.parser.stringparser;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;

public class StringParser {

	protected Localization localization;
    private AlgebraProcessor algebraProcessor;

    public StringParser(AlgebraProcessor algebraProcessor) {
        this.algebraProcessor = algebraProcessor;
    }

	public double convertToDouble(String string) {
        try {
            return algebraProcessor.convertToDouble(string);
		} catch (MyError | ParseException e) {
            throw new NumberFormatException();
        }
    }
}
