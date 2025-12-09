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

package org.geogebra.common.gui.view.table;

import org.geogebra.common.main.Localization;

/**
 * Thrown when the values set in table view are invalid.
 */
public class InvalidValuesException extends Exception {

	private static final long serialVersionUID = 1L;
	private String key;

	/**
	 * Construct an InvalidValuesException.
	 * 
	 * @param key
	 *            trans key
	 */
	InvalidValuesException(String key) {
		super();
		this.key = key;
	}

	/**
	 * 
	 * @param loc
	 *            {@link Localization}
	 * @return the localized error message.
	 */
	public String getLocalizedMessage(Localization loc) {
		return loc.getError(key);
	}
}
