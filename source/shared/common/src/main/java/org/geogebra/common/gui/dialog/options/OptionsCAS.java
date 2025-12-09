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

package org.geogebra.common.gui.dialog.options;

import java.util.Arrays;

public final class OptionsCAS {
	/** available CAS timeout options (will be reused in OptionsCAS) */
	final private static Integer[] cbTimeoutOptions = { 5, 10, 20, 30, 60 };

	private OptionsCAS() {
		// no instances
	}

	/**
	 * @param integer
	 *            option index
	 * @return timeout in seconds
	 */
	public static Integer getTimeoutOption(long integer) {
		for (int i = 0; i < cbTimeoutOptions.length; i++) {
			if (cbTimeoutOptions[i] == integer) {
				return cbTimeoutOptions[i];
			}
		}
		return cbTimeoutOptions[0];
	}

	public static Integer[] getTimeoutOptions() {
		return Arrays.copyOf(cbTimeoutOptions, cbTimeoutOptions.length);
	}

}
