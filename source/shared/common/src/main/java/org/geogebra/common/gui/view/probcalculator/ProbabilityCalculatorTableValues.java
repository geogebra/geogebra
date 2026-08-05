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

package org.geogebra.common.gui.view.probcalculator;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** UI-ready table data for the probability calculator table values view. */
public record ProbabilityCalculatorTableValues(@NonNull Row header, @NonNull List<Row> rows) {
	private static final double NEARLY_ONE = 1 - 1E-6;

	/** UI-ready row data for the probability calculator table values view. */
	public record Row(@NonNull String k, @NonNull String probability, boolean highlighted) {}

	/**
	 * Calculates the probabilities and constructs the table values view state
	 * based on the current state of the {@link ProbabilityCalculatorView}.
	 * @param view the probability calculator view
	 * @return the table values, or {@code null} if table values are
	 * unavailable for the current state of the probability calculator view
	 */
	public static @Nullable ProbabilityCalculatorTableValues from(
			@NonNull ProbabilityCalculatorView view) {
		if (!view.isDiscreteProbability()) {
			return null;
		}
		return new ProbabilityCalculatorTableValues(
				new Row("k", view.getProbabilityExpression(), false),
				switch (view.getSelectedDist()) {
					case BINOMIAL -> createBinomialRows(view, view.getParameters());
					case PASCAL -> createPascalRows(view, view.getParameters());
					case POISSON -> createPoissonRows(view, view.getParameters());
					case HYPERGEOMETRIC -> createHyperGeometricRows(view, view.getParameters());
					default -> throw new IllegalStateException();
				});
	}

	private static boolean isHighlighted(ProbabilityCalculatorView view, int k) {
		return view.getProbMode() == ProbabilityCalculatorView.PROB_TWO_TAILED
				? k <= view.getLow() || k >= view.getHigh()
				: view.getLow() <= k && k <= view.getHigh();
	}

	private static List<Row> createBinomialRows(ProbabilityCalculatorView view,
			GeoNumberValue[] parameters) {
		try {
			int numberOfTrials = integerParameter(parameters, 0);
			double probabilityOfSuccess = parameters[1].getDouble();
			return createRows(view, 0, numberOfTrials,
					new BinomialDistribution(numberOfTrials, probabilityOfSuccess));
		} catch (RuntimeException exception) {
			return List.of();
		}
	}

	private static List<Row> createPascalRows(ProbabilityCalculatorView view,
			GeoNumberValue[] parameters) {
		try {
			int numberOfSuccess = integerParameter(parameters, 0);
			double probabilityOfSuccess = parameters[1].getDouble();
			PascalDistribution distribution =
					new PascalDistribution(numberOfSuccess, probabilityOfSuccess);
			int upperBound = distribution.inverseCumulativeProbability(NEARLY_ONE);
			return createRows(view, 0, upperBound, distribution);
		} catch (RuntimeException exception) {
			return List.of();
		}
	}

	private static List<Row> createPoissonRows(ProbabilityCalculatorView view,
			GeoNumberValue[] parameters) {
		try {
			double poissonMean = parameters[0].getDouble();
			PoissonDistribution distribution = new PoissonDistribution(poissonMean);
			return createRows(view, 0, distribution.inverseCumulativeProbability(NEARLY_ONE),
					distribution);
		} catch (RuntimeException exception) {
			return List.of();
		}
	}

	private static List<Row> createHyperGeometricRows(ProbabilityCalculatorView view,
			GeoNumberValue[] parameters) {
		try {
			int populationSize = integerParameter(parameters, 0);
			int numberOfSuccesses = integerParameter(parameters, 1);
			int sampleSize = integerParameter(parameters, 2);
			int lowerBound = Math.max(0, numberOfSuccesses + sampleSize - populationSize);
			int upperBound = Math.min(numberOfSuccesses, sampleSize);
			return createRows(view, lowerBound, upperBound,
					new HypergeometricDistribution(populationSize, numberOfSuccesses, sampleSize));
		} catch (RuntimeException exception) {
			return List.of();
		}
	}

	private static List<Row> createRows(ProbabilityCalculatorView view, int lowerBound,
			int upperBound, IntegerDistribution distribution) {
		return IntStream.rangeClosed(lowerBound, upperBound).mapToObj(k -> new Row(
				String.valueOf(k),
				view.format(view.isCumulative()
						? distribution.cumulativeProbability(k) : distribution.probability(k)),
				isHighlighted(view, k))
		).toList();
	}

	private static int integerParameter(GeoNumberValue[] parameters, int index) {
		return (int) Math.round(parameters[index].getDouble());
	}
}
