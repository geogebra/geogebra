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

package org.geogebra.common.kernel.parser;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

/**
 * Faster exceptions
 */
public class GParser extends Parser {
	public static class GParseException extends ParseException {
		private String details;

		public GParseException(String message) {
			super(message);
		}

		public String getDetails() {
			return details;
		}
	}

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
	public ParseException generateParseException(String ignored) {
		GParseException ex = new GParseException(getKernel().getLocalization()
				.getInvalidInputError());
		if (jj_nt != null && jj_nt.image != null) {
			ex.details = "Unexpected next token: " + jj_nt.image;
		} else if (token.image != null) {
			ex.details = "Unexpected token: " + token.image;
		} else {
			ex.details = "Generic parse error";
		}
		return ex;
	}

}
