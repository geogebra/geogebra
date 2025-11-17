package org.geogebra.common.gui.view.table.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.statistics.FitAlgo;
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
			double[] coeffs = Objects.requireNonNull(fitAlgo).getCoeffs();
			String formula = regression.getFormula();
			if (formula != null) {
				stats.add(new StatisticGroup(true,
						kernel.getLocalization().getMenu("Stats.Formula"), formula));
			}
			String[] parameters = new String[coeffs.length];
			for (int i = 0; i < coeffs.length; i++) {
				char coeffName = regression.getCoeffName(i);
				int index = regression.getCoeffOrdering().indexOf(coeffName);
				parameters[i] = coeffName + " = "
						+ kernel.format(coeffs[index], StringTemplate.defaultTemplate);
			}
			stats.add(new StatisticGroup(loc.getMenu("Parameters"), parameters));
			if (regression.hasCoefficientOfDetermination()) {
				addResidual(loc.getMenu("CoefficientOfDetermination"), x -> x,
						Statistic.RSQUARE, geo, points, stats);
			}
			if (regression.hasCorrelationCoefficient()) {
				addResidual(kernel.getLocalization().getMenu("Stats.PMCC"), Math::sqrt,
						Statistic.PMCC, geo, points, stats);
			}
		} catch (CommandNotLoadedError e) {
			throw e; // commands not loaded => throw so that we can retry on UI level
		} catch (Throwable t) {
			Log.debug(t);
		}
		return stats;
	}

	private void addResidual(String coefficient, DoubleUnaryOperator transform, Statistic lhsStat,
			GeoElementND geo, MyVecNode points, List<StatisticGroup> stats)
			throws CircularDefinitionException {
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		Command residualCmd = new Command(kernel, Statistic.RSQUARE.getCommandName(),
				false);
		residualCmd.addArgument(points.wrap());
		residualCmd.addArgument(geo.wrap());
		residualCmd.setRespectingFilters(false);
		GeoElementND residual = algebraProcessor.processValidExpressionSilent(
				residualCmd)[0];
		String lhs = lhsStat.getLHS(kernel.getLocalization(), "");
		String rSquareRow = kernel.format(transform.applyAsDouble(residual.evaluateDouble()),
				StringTemplate.defaultTemplate);
		stats.add(new StatisticGroup(coefficient,
				lhs + " = " + rSquareRow));
	}
}
