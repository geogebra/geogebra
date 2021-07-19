package org.geogebra.common.gui.view.table.dialog;

public class StatisticGroup {
	private final String heading;
	private final String[] values;
	private boolean isLaTeX;

	/**
	 * @param heading heading row
	 * @param values values row
	 */
	public StatisticGroup(String heading, String... values) {
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
	 * @param heading heading row
	 * @param formula LaTeX formula (value row)
	 * @return stats dialog entry
	 */
	public static StatisticGroup withLaTeX(String heading, String formula) {
		StatisticGroup row = new StatisticGroup(heading, formula);
		row.isLaTeX = true;
		return row;
	}

	/**
	 * @return whether this needs LaTeX to render value
	 */
	public boolean isLaTeX() {
		return isLaTeX;
	}
}
