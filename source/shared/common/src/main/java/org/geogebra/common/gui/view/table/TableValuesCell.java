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

import java.util.Objects;

public class TableValuesCell {

	private final boolean erroneous;
	private final String input;

	/**
	 * Create a TableValuesCell model.
	 * @param input input string
	 * @param erroneous if the input has errors
	 */
	public TableValuesCell(String input, boolean erroneous) {
		this.input = input;
		this.erroneous = erroneous;
	}

	public boolean isErroneous() {
		return erroneous;
	}

	public String getInput() {
		return input;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TableValuesCell that = (TableValuesCell) o;
		return erroneous == that.erroneous && input.equals(that.input);
	}

	@Override
	public int hashCode() {
		return Objects.hash(erroneous, input);
	}
}
