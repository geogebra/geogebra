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

import java.util.List;

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
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
