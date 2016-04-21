package org.geogebra.common.kernel.parser;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

public class GParser extends Parser {

	public GParser(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	public ParseException generateParseException() {
		return new ParseException();

	}

}
