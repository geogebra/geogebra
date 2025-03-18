package org.geogebra.common.gui.view.table.dialog;

import org.geogebra.common.kernel.statistics.Statistic;

/**
 * Filters statistics, used in
 * {@link org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder}.
 */
public interface StatisticsFilter {

	/**
	 * @param statistic statistic to be evaluated
	 * @return {@code true} if statistic should be allowed, {@code false} otherwise
	 */
	boolean isAllowed(Statistic statistic);
}