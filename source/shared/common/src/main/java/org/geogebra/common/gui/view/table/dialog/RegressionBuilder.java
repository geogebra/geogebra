package org.geogebra.common.gui.view.table.dialog;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.table.RegressionSpecification;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.statistics.FitAlgo;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class RegressionBuilder {
	private final GeoEvaluatable xVal;
	private final GeoEvaluatable yVal;
	private final Kernel kernel;

	/**
	 * @param xVal list of x-values
	 * @param yVal list of y-values
	 */
	public RegressionBuilder(GeoEvaluatable xVal, GeoEvaluatable yVal) {
		this.xVal = xVal;
		this.yVal = yVal;
		this.kernel = xVal.getKernel();
	}

	/**
	 * @param regression regression type + degree
	 * @return regression parameters + coefficient of determination
	 */
	public List<StatisticGroup> getRegression(RegressionSpecification regression) {
		MyVecNode points = new MyVecNode(kernel, xVal, yVal);
		Command cmd = regression.buildCommand(kernel, points);
		List<StatisticGroup> stats = new ArrayList<>();
		Localization loc = kernel.getLocalization();
		try {
			AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
			GeoElementND geo = algebraProcessor.processValidExpressionSilent(cmd)[0];
			FitAlgo fitAlgo = (FitAlgo) geo.getParentAlgorithm();
			double[] coeffs = fitAlgo.getCoeffs();
			stats.add(new StatisticGroup(true, kernel.getLocalization().getMenu("Stats.Formula"),
					regression.getFormula()));
			String[] parameters = new String[coeffs.length];
			for (int i = 0; i < coeffs.length; i++) {
				char coeffName = (char) ('a' + i);
				int index = regression.getCoeffOrdering().indexOf(coeffName);
				parameters[i] = coeffName + " = "
						+ kernel.format(coeffs[index], StringTemplate.defaultTemplate);
			}
			stats.add(new StatisticGroup(loc.getMenu("Parameters"), parameters));
			Command residualCmd = new Command(kernel, Statistic.RSQUARE.getCommandName(), false);
			residualCmd.addArgument(points.wrap());
			residualCmd.addArgument(geo.wrap());
			GeoElementND residual = algebraProcessor.processValidExpressionSilent(residualCmd)[0];
			String lhs = Statistic.RSQUARE.getLHS(kernel.getLocalization(), "");
			String rSquareRow = kernel.format(residual.evaluateDouble(),
					StringTemplate.defaultTemplate);
			stats.add(new StatisticGroup(loc.getMenu("CoefficientOfDetermination"),
					lhs + " = " + rSquareRow));
			addCorrelationCoefficient(stats, points, regression);
		} catch (Throwable e) {
			Log.error(e);
		}
		return stats;
	}

	private void addCorrelationCoefficient(List<StatisticGroup> stats, MyVecNode points,
			RegressionSpecification regression) {
		if (regression.getRegression() == Regression.LINEAR) {
			Command exec = new Command(kernel, Statistic.PMCC.getCommandName(), false);
			exec.addArgument(points.wrap());
			String varName = xVal.getLabelSimple() + yVal.getLabelSimple();

			try {
				AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
				GeoElementND r = algebraProcessor.processValidExpressionSilent(exec)[0];
				String heading = kernel.getLocalization().getMenu(
						"Stats." + Statistic.PMCC.getCommandName());
				String lhs = Statistic.PMCC.getLHS(kernel.getLocalization(), varName);
				String formula = lhs + " = " + r.toValueString(StringTemplate.defaultTemplate);
				stats.add(new StatisticGroup(false, heading, formula));
			} catch (RuntimeException | CircularDefinitionException e) {
				Log.debug(e);
			}
		}
	}
}
