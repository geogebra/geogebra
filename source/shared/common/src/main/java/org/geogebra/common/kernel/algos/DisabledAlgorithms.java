package org.geogebra.common.kernel.algos;

/**
 * Algorithms that can be disabled in {@link AlgoDispatcher}.
 */
public enum DisabledAlgorithms {
	/** {@link AlgoTangentPoint} */
	TangentPointConic,
	/** {@link AlgoTangentLine} */
	TangentLineConic,
	/** {@link AlgoCommonTangents} */
	TangentConicConic,
	/** {@link org.geogebra.common.kernel.implicit.AlgoTangentImplicitpoly} */
	TangentPointImplicitCurve
}
