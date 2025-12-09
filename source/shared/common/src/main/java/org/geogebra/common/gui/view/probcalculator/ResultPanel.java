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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Results panel.
 */
public interface ResultPanel {
	@MissingDoc
	void showInterval();

	@MissingDoc
	void showTwoTailed();

	@MissingDoc
	void showTwoTailedOnePoint();

	@MissingDoc
	void showLeft();

	@MissingDoc
	void showRight();

	/**
	 * @param value whether the result field is editable
	 */
	void setResultEditable(boolean value);

	/**
	 * @param text result value
	 */
	void updateResult(String text);

	/**
	 * Update lower and upper interval bounds.
	 * @param low lower interval bound
	 * @param high upper interval bound
	 */
	void updateLowHigh(String low, String high);

	/**
	 * Update two-tailed result.
	 * @param lowProb probability of X &lt; low
	 * @param highProb probability of X &gt; high
	 */
	void updateTwoTailedResult(String lowProb, String highProb);

	/**
	 * @param source UI field
	 * @return whether it's the lower bound input field
	 */
	boolean isFieldLow(Object source);

	/**
	 * @param source UI field
	 * @return whether it's the upper bound input field
	 */
	boolean isFieldHigh(Object source);

	/**
	 * @param source UI field
	 * @return whether it's the probability result input field
	 */
	boolean isFieldResult(Object source);

	/**
	 * Change sign to &gt;.
	 */
	void setGreaterThan();

	/**
	 * Change sign to &gt;=.
	 */
	void setGreaterOrEqualThan();
}
