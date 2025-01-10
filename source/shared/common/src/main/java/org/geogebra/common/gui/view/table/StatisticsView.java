package org.geogebra.common.gui.view.table;

import java.util.List;

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.geos.GeoElement;

/** Interface for statistics operation */
public interface StatisticsView {

	/**
	 * @param column column
	 * @return one variable stats. The list if empty, if there is not enough data.
	 */
	List<StatisticGroup> getStatistics1Var(int column);

	/**
	 * @param column column
	 * @return two variable stats for first and given column. The list if empty, if there is not enough data.
	 */
	List<StatisticGroup> getStatistics2Var(int column);

	/**
	 * @param column column
	 * @return list of regression specifications. The list if empty, if there is not enough data.
	 */
	List<RegressionSpecification> getRegressionSpecifications(int column);

	/**
	 * @param column column
	 * @param regression regression type + degree
	 * @return regression parameters for first and given column
	 */
	List<StatisticGroup> getRegression(int column, RegressionSpecification regression);

	/**
	 * @param column column
	 * @param regression regression type + degree
	 * @return plot element
	 */
	GeoElement plotRegression(int column, RegressionSpecification regression);
}
