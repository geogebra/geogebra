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

package org.geogebra.common.gui.view.table.dialog;

public class StatisticGroup {

	private final String heading;
	private final String[] values;
	private boolean isLaTeX;

	/**
	 * Creates a non-latex StatisticsGroup
	 * @param heading heading row
	 * @param values values row
	 */
	public StatisticGroup(String heading, String... values) {
		this(false, heading, values);
	}

	/**
	 * Creates a statistics group
	 * @param isLaTeX is latex
	 * @param heading heading row
	 * @param values values row
	 */
	public StatisticGroup(boolean isLaTeX, String heading, String... values) {
		this.isLaTeX = isLaTeX;
		this.values = values;
		this.heading = heading;
	}

	/**
	 * @return heading row
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * @return value row
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * @return whether this needs LaTeX to render value
	 */
	public boolean isLaTeX() {
		return isLaTeX;
	}
}
