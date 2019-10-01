package org.geogebra.common.kernel.parser;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.debug.Log;

/**
 * Faster exceptions
 */
public class GParser extends Parser {
	private boolean silent;

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public GParser(Kernel kernel, Construction cons) {
		super(kernel);
	}

	@Override
	public ParseException generateParseException() {
		if (!silent) {
			if (jj_nt != null && jj_nt.image != null) {
				Log.error("Unexpected next token: " + jj_nt.image);
			} else if (token.image != null) {
				Log.error("Unexpected token: " + token.image);
			} else {
				Log.error("Generic parse error");
			}
		}
		return new ParseException(getKernel().getLocalization()
                .getInvalidInputError());

	}

	/**
	 * @param silent
	 *            whether to suppress console errors
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
