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
