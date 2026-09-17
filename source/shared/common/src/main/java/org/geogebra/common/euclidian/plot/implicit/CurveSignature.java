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

import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.implicit.GeoImplicit;

/**
 * Lightweight change detector for implicit curves used by the Bernstein plotter.
 * <p>
 * The signature combines the implicit border coefficients, the inequality operation
 * when present, and numeric input values referenced by the curve definition. This
 * lets callers detect coefficient changes without keeping or comparing the full
 * polynomial state.
 * </p>
 */
public final class CurveSignature {
	private static final long FNV_OFFSET = 0xcbf29ce484222325L;
	private static final long FNV_PRIME = 0x100000001b3L;
	private static final long NULL_COEFFS = 0x9e3779b97f4a7c15L;
	private final Inequality ineq;
	private final GeoElement[] input;
	private long signature;
	private GeoImplicit curve;

	/**
	 * Creates a signature for an implicit inequality border.
	 *
	 * @param ineq inequality whose implicit border should be tracked
	 */
	CurveSignature(Inequality ineq) {
		this.ineq = ineq;
		this.curve = ineq.getImplicitCurveBorder();
		input = createInput();
		update();
	}

	private GeoElement[] createInput() {
		return curve.getFunctionDefinition().getGeoElementVariables(SymbolicMode.NONE);
	}

	/**
	 * Creates a signature for a plain implicit curve.
	 *
	 * @param curve implicit curve to track
	 */
	public CurveSignature(GeoImplicit curve) {
		this.curve = curve;
		this.ineq = null;
		input = createInput();
		update();
	}

	/**
	 * Stores the current curve state as the reference signature.
	 */
	void update() {
		signature = compute();
	}

	/**
	 * @return whether the curve coefficients, inequality operation, or numeric inputs
	 * have changed since the last {@link #update()} call
	 */
	boolean isOutdated() {
		return signature != compute();
	}

	private long compute() {
		long hash = FNV_OFFSET;
		if (ineq != null) {
			hash = mix(hash, ineq.getOperation().ordinal());
			this.curve = ineq.getImplicitCurveBorder();
		}

		double[][] coeffs = curve.getCoeff();
		if (coeffs == null) {
			return mix(hash, NULL_COEFFS);
		}
		hash = mix(hash, coeffs.length);
		for (double[] row : coeffs) {
			if (row == null) {
				hash = mix(hash, -1);
				continue;
			}
			hash = mix(hash, row.length);
			for (double coeff : row) {
				hash = mix(hash, Double.doubleToLongBits(coeff));
			}
		}
		hash = mixInputValues(hash);
		return hash;
	}

	private long mixInputValues(long hash) {
		if (input == null) {
			return hash;
		}
		long mixedHash = mix(hash, input.length);
		for (GeoElement geo : input) {
			if (geo instanceof GeoNumeric) {
				mixedHash = mix(mixedHash, Double.doubleToLongBits(((GeoNumeric) geo).getValue()));
			}
		}
		return mixedHash;
	}

	private static long mix(long input, long value) {
		return (input ^ value) * FNV_PRIME;
	}

	@Override
	public String toString() {
		return "CurveSignature{signature=" + Long.toHexString(signature) + "}";
	}
}
