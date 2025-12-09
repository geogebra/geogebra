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

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public final class IntersectLineSpline {
	private final Coords coeffs;
	private final Spline spline;
	private final EquationSolver solver;

	/**
	 * @param spline the Spline.
	 * @param coeffs the line coefficients.
	 * @param solver the equation solver.
	 */
	public IntersectLineSpline(Spline spline, Coords coeffs, EquationSolver solver) {
		this.spline = spline;
		this.coeffs = coeffs;
		this.solver = solver;
	}

	/**
	 *
	 * @return roots of the intersection
	 */
	public List<Double> compute() {
		ArrayList<Double> roots = new ArrayList<>();
		for (int i = 0; i < spline.size(); i++) {
			ExpressionNode enx = AlgoIntersectLineCurve.getMultiplyExpression(spline.getFuncX(i),
					spline.getFuncY(i), coeffs);
			GeoFunction functionX = enx.buildFunction(spline.getFunctionVariable());
			Solution solution = solve(functionX);
			if (solution.curRoots != null) {
				filterRoots(solution, i, roots);
			}
		}
		return roots;
	}

	private void filterRoots(Solution solution, int i, ArrayList<Double> roots) {
		for (int j = 0; j < solution.curRealRoots; j++) {
			double root = solution.curRoots[j];
			if (DoubleUtil.isZero(root)) {
				root = 0;
			}

			if (spline.isInInterval(root, i) && !roots.contains(root)) {
				roots.add(root);
			}
		}
	}

	private Solution solve(GeoFunction fX) {
		Solution solution = new Solution();
		AlgoRootsPolynomial.calcRootsMultiple(fX.getFunction(),
				0, solution, solver);

		solution.sortAndMakeUnique();
		return solution;
	}
}