package org.geogebra.common.gui.view.table;

import java.util.List;

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;

/** Interface for statistics operation */
public interface StatisticsView {

	/**
	 * @param column column
	 * @return one variable stats
	 */
	public List<StatisticGroup> getStatistics1Var(int column);

	/**
	 * @param column column
	 * @return two variable stats for first and given column
	 */
	public List<StatisticGroup> getStatistics2Var(int column);

	/**
	 * @param column column
	 * @param regression regression type + degree
	 * @return regression parameters for first and given column
	 */
	public List<StatisticGroup> getRegression(int column, RegressionSpecification regression);
}
