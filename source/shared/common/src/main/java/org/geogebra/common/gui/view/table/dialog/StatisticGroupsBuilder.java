package org.geogebra.common.gui.view.table.dialog;

import static org.geogebra.common.kernel.statistics.Statistic.COVARIANCE;
import static org.geogebra.common.kernel.statistics.Statistic.LENGTH;
import static org.geogebra.common.kernel.statistics.Statistic.MAX;
import static org.geogebra.common.kernel.statistics.Statistic.MEAN;
import static org.geogebra.common.kernel.statistics.Statistic.MEDIAN;
import static org.geogebra.common.kernel.statistics.Statistic.MIN;
import static org.geogebra.common.kernel.statistics.Statistic.PMCC;
import static org.geogebra.common.kernel.statistics.Statistic.Q1;
import static org.geogebra.common.kernel.statistics.Statistic.Q3;
import static org.geogebra.common.kernel.statistics.Statistic.SAMPLE_SD;
import static org.geogebra.common.kernel.statistics.Statistic.SD;
import static org.geogebra.common.kernel.statistics.Statistic.SIGMAXX;
import static org.geogebra.common.kernel.statistics.Statistic.SIGMAXY;
import static org.geogebra.common.kernel.statistics.Statistic.SUM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Builds {@link StatisticGroup}s to be displayed by client implementations.
 */
public class StatisticGroupsBuilder {

	private final static List<Statistic> ONE_VAR_STATISTICS = Arrays.asList(
			MEAN, SUM, SIGMAXX, SAMPLE_SD, SD
	);
	private final static List<Statistic> ONE_VAR_STATISTICS2 = Arrays.asList(
			LENGTH, MIN, Q1, MEDIAN, Q3, MAX
	);
	private final static List<Statistic> MIN_MAX = Arrays.asList(MIN, MAX);
	private final static List<Statistic> TWO_VAR_STATISTICS = Arrays.asList(
			SIGMAXY, PMCC, COVARIANCE
	);

	@CheckForNull
	private StatisticsFilter statisticsFilter = null;

	/**
	 * Sets a filter to restrict certain statistics from being built.
	 * @param statisticsFilter a statistics filter to be set
	 */
	public void setStatisticsFilter(@Nullable StatisticsFilter statisticsFilter) {
		this.statisticsFilter = statisticsFilter;
	}

	/**
	 * @param variable variable list to build statistics on
	 * @param variableName name of the variable
	 * @return one variable statistics
	 */
	public List<StatisticGroup> buildOneVariableStatistics(GeoEvaluatable variable,
			String variableName) {
		GeoList cleanVariable = removeUndefinedValues(variable);
		List<StatisticGroup> statisticGroups = new ArrayList<>();
		// use command strings, not algos, to make sure code splitting works in Web
		addStatistics(statisticGroups, ONE_VAR_STATISTICS, variableName, cleanVariable);
		addStatistics(statisticGroups, ONE_VAR_STATISTICS2, variableName, cleanVariable);
		return statisticGroups;
	}

	private GeoList removeUndefinedValues(GeoEvaluatable list) {
		GeoList cleanList = new GeoList(list.getKernel().getConstruction());
		if (list instanceof GeoList && ((GeoList) list).size() >= 2) {
			((GeoList) list).elements().filter(GeoElement::isDefined).forEach(cleanList::add);
		}
		return cleanList;
	}

	/**
	 * @param variable1 first variable
	 * @param variableName1 name of the first variable
	 * @param variable2 second variable
	 * @param variableName2 name of the second variable
	 * @return two variable statistic groups
	 */
	public List<StatisticGroup> buildTwoVariableStatistics(GeoEvaluatable variable1,
			String variableName1, GeoEvaluatable variable2, String variableName2) {
		List<StatisticGroup> statisticGroups = new ArrayList<>();
		GeoList[] cleanLists = getCleanListsTwoVariable(variable1, variable2);
		addStatistics(statisticGroups, ONE_VAR_STATISTICS, variableName1, cleanLists[0]);
		addStatistics(statisticGroups, ONE_VAR_STATISTICS, variableName2, cleanLists[1]);
		addStatistics(statisticGroups, TWO_VAR_STATISTICS, variableName1 + variableName2,
				cleanLists);
		addStatistics(statisticGroups, List.of(LENGTH), variableName1, cleanLists[0]);
		addStatistics(statisticGroups, MIN_MAX, variableName1, cleanLists[0]);
		addStatistics(statisticGroups, MIN_MAX, variableName2, cleanLists[1]);
		return statisticGroups;
	}

	/**
	 * Filter both lists, keep only items where both lists have a number at given index.
	 * @param variable1 first variable
	 * @param variable2 second variable
	 * @return filtered lists
	 */
	public GeoList[] getCleanListsTwoVariable(GeoEvaluatable variable1, GeoEvaluatable variable2) {
		Kernel kernel = variable1.getKernel();
		Command cleanData = new Command(kernel, Commands.RemoveUndefined.getCommand(), false);
		MyVecNode points = new MyVecNode(kernel, variable1, variable2);
		cleanData.addArgument(points.wrap());
		ExpressionNode xCoordExpr =
				new ExpressionNode(kernel, cleanData.wrap(), Operation.XCOORD, null);
		ExpressionNode yCoordExpr =
				new ExpressionNode(kernel, cleanData.wrap(), Operation.YCOORD, null);
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();

		try {
			GeoElementND resultX = algebraProcessor.processValidExpressionSilent(xCoordExpr)[0];
			GeoElementND resultY = algebraProcessor.processValidExpressionSilent(yCoordExpr)[0];
			// use command strings, not algos, to make sure code splitting works in Web
			return new GeoList[]{(GeoList) resultX, (GeoList) resultY};
		} catch (RuntimeException | CircularDefinitionException e) {
			return new GeoList[]{new GeoList(kernel.getConstruction()),
					new GeoList(kernel.getConstruction())};
		}
	}

	private void addStatistics(List<StatisticGroup> statisticGroups, List<Statistic> statistics,
			String variableName, GeoList... variables) {
		if (variables.length == 0 || variables[0].size() < 2) {
			return;
		}
		Kernel kernel = variables[0].getKernel();
		for (Statistic statistic : statistics) {
			if (statisticsFilter != null && !statisticsFilter.isAllowed(statistic)) {
				continue;
			}
			Command command = new Command(kernel, statistic.getCommandName(), false);
			for (GeoElementND list : variables) {
				command.addArgument(list.wrap());
			}

			try {
				AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
				GeoElementND result = algebraProcessor.processValidExpressionSilent(command)[0];
				String heading =
						kernel.getLocalization().getMenu(statistic.getMenuLocalizationKey());
				String lhs = statistic.getLHS(kernel.getLocalization(), variableName);
				String formula =
						lhs + " = " + result.toValueString(StringTemplate.defaultTemplate);
				statisticGroups.add(new StatisticGroup(true, heading, formula));
			} catch (CommandNotLoadedError err) {
				throw err;
			} catch (Throwable e) {
				Log.debug(e);
			}
		}
	}
}
