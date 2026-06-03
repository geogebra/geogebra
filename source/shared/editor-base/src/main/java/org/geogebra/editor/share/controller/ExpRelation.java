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

package org.geogebra.editor.share.controller;

public enum ExpRelation {
	EMPTY("ABlank", "%0 blank"),
	END_OF("EndOfA", "end of %0"),
	START_OF("StartOfA", "start of %0"),
	AFTER("AfterA", "after %0"),
	BEFORE("BeforeA", "before %0"),
	START_FORMULA("StartOfFormulaA", "start of formula %0"),
	END_FORMULA("EndOfFormulaA", "end of formula %0");

	private final String key;
	private final String pattern;

	ExpRelation(String key, String pattern) {
		this.key = key;
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return pattern;
	}

	public String getKey() {
		return key;
	}
}
