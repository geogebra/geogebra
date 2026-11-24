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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.Arrays;

import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.plugin.Operation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Lightweight change detector for implicit curves used by the Bernstein plotter.
 * <p>
 * Stores a snapshot of the implicit border coefficients and the inequality operation when
 * present. Checks compare coefficients exactly, without hashing or allocating another snapshot.
 * Numeric inputs are reflected through the refreshed coefficients, so input changes that leave
 * the polynomial unchanged do not invalidate the plot.
 * </p>
 */
public final class CurveSignature {
	private final @Nullable Inequality ineq;
	private @Nullable Operation operation;
	private double @Nullable [][] coefficients;
	private GeoImplicit curve;

	/**
	 * Creates a signature for an implicit inequality border.
	 *
	 * @param ineq inequality whose implicit border should be tracked
	 */
	CurveSignature(@NonNull Inequality ineq) {
		this.ineq = ineq;
		this.curve = ineq.getImplicitCurveBorder();
		update();
	}

	/**
	 * Creates a signature for a plain implicit curve.
	 *
	 * @param curve implicit curve to track
	 */
	public CurveSignature(@NonNull GeoImplicit curve) {
		this.curve = curve;
		this.ineq = null;
		update();
	}

	/**
	 * Stores the current curve state as the reference signature.
	 */
	void update() {
		operation = ineq == null ? null : ineq.getOperation();
		double[][] current = getCurve().getCoeff();
		if (current == null) {
			coefficients = null;
			return;
		}
		coefficients = new double[current.length][];
		for (int i = 0; i < current.length; i++) {
			coefficients[i] = current[i] == null ? null : Arrays.copyOf(current[i], current[i].length);
		}
	}

	/**
	 * @return whether the curve coefficients or inequality operation
	 * have changed since the last {@link #update()} call
	 */
	boolean isOutdated() {
		if (ineq != null && operation != ineq.getOperation()) {
			return true;
		}
		double[][] current = getCurve().getCoeff();
		if (current == null || coefficients == null) {
			return current != coefficients;
		}
		if (current.length != coefficients.length) {
			return true;
		}
		for (int i = 0; i < current.length; i++) {
			double[] row = current[i];
			double[] previous = coefficients[i];
			if (row == null || previous == null) {
				if (row != previous) {
					return true;
				}
				continue;
			}
			if (row.length != previous.length) {
				return true;
			}
			for (int j = 0; j < row.length; j++) {
				// Signed zeros are equal; unchanged NaNs must not cause repeated replots.
				if (row[j] != previous[j] && !(Double.isNaN(row[j]) && Double.isNaN(previous[j]))) {
					return true;
				}
			}
		}
		return false;
	}

	private @NonNull GeoImplicit getCurve() {
		if (ineq != null) {
			curve = ineq.getImplicitCurveBorder();
		}
		return curve;
	}

	@Override
	public String toString() {
		return "CurveSignature{operation=" + operation + ", coefficients="
				+ Arrays.deepToString(coefficients) + "}";
	}
}
