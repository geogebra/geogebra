package org.geogebra.common.gui.view.table.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.statistics.Stat;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class StatsBuilder {

	private final Kernel kernel;
	private final GeoEvaluatable[] lists;
	final static List<Stat> ONE_VAR_STATS = Arrays.asList(
			Stat.MEAN, Stat.SUM, Stat.SIGMAXX, Stat.SAMPLE_SD, Stat.SD);
	final static List<Stat> ONE_VAR_EXTRA = Arrays.asList(Stat.LENGTH,
			Stat.MIN, Stat.Q1, Stat.MEDIAN, Stat.Q3, Stat.MAX
	);
	final static List<Stat> MIN_MAX = Arrays.asList(Stat.MIN, Stat.MAX);
	final static List<Stat> TWO_VAR_STATS = Arrays.asList(
			Stat.SIGMAXY, Stat.PMCC, Stat.COVARIANCE
	);

	/**
	 * @param dataLists data lists
	 */
	public StatsBuilder(GeoEvaluatable... dataLists) {
		this.lists = dataLists;
		this.kernel = lists[0].getKernel();
	}

	/**
	 * @return single variable statistics
	 */
	public List<StatisticGroup> getStatistics1Var(String varName) {
		GeoList cleanList = getCleanList1Var(lists[0]);
		List<StatisticGroup> stats = new ArrayList<>();
		// use command strings, not algos, to make sure code splitting works in Web
		addStats(stats, ONE_VAR_STATS, varName, cleanList);
		addStats(stats, ONE_VAR_EXTRA, varName, cleanList);
		return stats;
	}

	private GeoList getCleanList1Var(GeoEvaluatable list) {
		GeoList cleanList = new GeoList(list.getKernel().getConstruction());
		if (list instanceof GeoList && ((GeoList) list).size() >= 2) {
			((GeoList) list).elements().filter(
					(GeoElementND geo) -> geo instanceof GeoNumeric).forEach(cleanList::add);
		}
		return cleanList;
	}

	/**
	 * @return two variable statistics
	 */
	public List<StatisticGroup> getStatistics2Var(String varName, String varName2) {
		Command cleanData = new Command(kernel, Commands.RemoveUndefined.getCommand(),
				false);
		MyVecNode points = new MyVecNode(kernel, this.lists[0], this.lists[1]);
		cleanData.addArgument(points.wrap());
		ExpressionNode xCoordExpr =
				new ExpressionNode(kernel, cleanData.wrap(), Operation.XCOORD, null);
		ExpressionNode yCoordExpr =
				new ExpressionNode(kernel, cleanData.wrap(), Operation.YCOORD, null);
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		List<StatisticGroup> stats = new ArrayList<>();
		try {
			GeoElementND resultX = algebraProcessor.processValidExpressionSilent(xCoordExpr)[0];
			GeoElementND resultY = algebraProcessor.processValidExpressionSilent(yCoordExpr)[0];
			// use command strings, not algos, to make sure code splitting works in Web
			addStats(stats, ONE_VAR_STATS, varName, resultX);
			addStats(stats, ONE_VAR_STATS, varName2, resultY);
			addStats(stats, TWO_VAR_STATS, varName + varName2, resultX, resultY);
			addStats(stats, Arrays.asList(Stat.LENGTH), varName, resultX);
			addStats(stats, MIN_MAX, varName, resultX);
			addStats(stats, MIN_MAX, varName2, resultY);
			return stats;
		} catch (Exception e) {
			return stats;
		}
	}

	private void addStats(List<StatisticGroup> stats, List<Stat> statAlgos, String varName,
			GeoElementND... lists) {
		if (lists[0].isGeoList() && ((GeoList) lists[0]).size() >= 2) {
			for (Stat cmd : statAlgos) {
				Command exec = new Command(kernel, cmd.getCommandName(), false);
				for (GeoElementND list : lists) {
					exec.addArgument(list.wrap());
				}

				try {
					AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
					GeoElementND r = algebraProcessor.processValidExpressionSilent(exec)[0];
					String heading =
							kernel.getLocalization().getMenu("Stats." + cmd.getCommandName());
					String lhs = cmd.getLHS(kernel.getLocalization(), varName);
					String formula = lhs + " = " + r.toValueString(StringTemplate.defaultTemplate);
					stats.add(new StatisticGroup(true, heading, formula));
				} catch (Exception e) {
					Log.debug(e);
				}
			}
		}
	}
}
