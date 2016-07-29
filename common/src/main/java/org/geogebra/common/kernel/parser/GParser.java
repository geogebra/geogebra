package org.geogebra.common.kernel.parser;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

/**
 * Faster exceptions
 */
public class GParser extends Parser {
	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public GParser(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	public ParseException generateParseException() {
		if (jj_nt != null && jj_nt.image != null) {
			return new ParseException("Unexpected next token: " + jj_nt.image);
		}
		if (token.image != null) {
			return new ParseException("Unexpected token: " + token.image);
		}

		return new ParseException("Generic parse error.");

	}

}
