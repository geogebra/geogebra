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
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
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
	private final AlgebraProcessor algebraProcessor;

	/**
	 * @param xVal list of x-values
	 * @param yVal list of y-values
	 */
	public RegressionBuilder(GeoEvaluatable xVal, GeoEvaluatable yVal) {
		this.xVal = xVal;
		this.yVal = yVal;
		this.kernel = xVal.getKernel();
		this.algebraProcessor = kernel.getAlgebraProcessor();
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
				if (regression.hasCorrelationCoefficient()) {
					addCorrelationCoefficient(stats, points);
				}
			} else {
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
		Command residualCmd = buildCommand(Statistic.RSQUARE, points, geo);
		GeoElementND residual = algebraProcessor.processValidExpressionSilent(residualCmd)[0];
		String lhs = lhsStat.getLHS(kernel.getLocalization(), "");
		String rSquareRow = kernel.format(transform.applyAsDouble(residual.evaluateDouble()),
				StringTemplate.defaultTemplate);
		stats.add(new StatisticGroup(coefficient,
				lhs + " = " + rSquareRow));
	}

	private Command buildCommand(Statistic statistic, ExpressionValue... args) {
		Command residualCmd = new Command(kernel, statistic.getCommandName(), false);
		for (ExpressionValue val: args) {
			residualCmd.addArgument(val.wrap());
		}
		residualCmd.setRespectingFilters(false);
		return residualCmd;
	}

	private void addCorrelationCoefficient(List<StatisticGroup> stats, MyVecNode points) {
		Command exec = buildCommand(Statistic.PMCC, points);
		String varName = xVal.getLabelSimple() + yVal.getLabelSimple();

		try {
			GeoElementND r = algebraProcessor.processValidExpressionSilent(exec)[0];
			String heading = kernel.getLocalization().getMenu(
					"Stats." + Statistic.PMCC.getCommandName());
			String lhs = Statistic.PMCC.getLHS(kernel.getLocalization(), varName);
			String formula = lhs + " = " + r.toValueString(StringTemplate.defaultTemplate);
			stats.add(new StatisticGroup(heading, formula));
		} catch (RuntimeException | CircularDefinitionException e) {
			Log.debug(e);
		}
	}
}
