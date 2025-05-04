package org.geogebra.common.gui.view.probcalculator;

/**
 * Results panel.
 */
public interface ResultPanel {
	void showInterval();

	void showTwoTailed();

	void showTwoTailedOnePoint();

	void showLeft();

	void showRight();

	/**
	 * @param value whether result field is editable
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
	 * @param lowProb probability of X < low
	 * @param highProb probability of X > high
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
	 * Change sign to >.
	 */
	void setGreaterThan();

	/**
	 * Change sign to >=.
	 */
	void setGreaterOrEqualThan();
}
