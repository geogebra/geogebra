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

package org.geogebra.common.move.ggtapi.models;

/**
 * Actions that can be performed with a resource.
 */
public enum ResourceAction {
	EDIT("Edit"),
	VIEW("ViewMaterial"),
	INSERT_ACTIVITY("insert_worksheet"), // old key, translated as Insert Activity
	COPY("makeACopy"),
	SHARE("Share"),
	DELETE("Delete"),
	RENAME("Rename");

	private final String translationKey;

	ResourceAction(String key) {
		this.translationKey = key;
	}
	
	public String getTranslationKey() {
		return this.translationKey;
	}
}
