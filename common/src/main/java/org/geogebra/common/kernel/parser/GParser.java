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

	/**
	 * We want x_1y_2 and x8.1 to be a single token, but don't want users to
	 * create objects with these names, so we prefer to split it into product.
	 *
	 * @param name variable name
	 * @return whether we should force splitting it to product of two variables
	 */
	public static boolean shouldSplitLabel(String name) {
		int endIndex = name.indexOf('}');
		if (endIndex > 0) {
			return name.indexOf('_', endIndex) >= 0;
		}
		// x8.1 is bad, x_{8.1} is OK
		return name.contains(".");
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
