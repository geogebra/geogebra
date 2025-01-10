package org.geogebra.common.kernel.algos;

import java.util.Comparator;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Class to store the product and properties of
 * the product of the coefficients and expression for a polynomial.
 */
public final class CoeffPowerProduct {
	private final int primaryDegree;
	private final ExpressionValue powerExp;

	private final double coeffValue;
	private final int totalDegree;
	private static final Comparator<CoeffPowerProduct> comparator;

	static {
		Comparator<CoeffPowerProduct> byTotalDegree = Comparator.comparing(p -> -p.totalDegree);
		comparator = byTotalDegree.thenComparing(p -> -p.primaryDegree);
	}

	/**
	 * @param powerExp expression of the product
	 * @param coeffValue the coefficient
	 * @param totalDegree the degree of the expression.
	 * @param primaryDegree degree in first variable
	 */
	public CoeffPowerProduct(ExpressionValue powerExp, double coeffValue,
			int totalDegree, int primaryDegree) {
		this.powerExp = powerExp;
		this.coeffValue = coeffValue;
		this.totalDegree = totalDegree;
		this.primaryDegree = primaryDegree;
	}

	/**
	 * Creates a comparator for sorting CoeffProduct lists, such as
	 *  - first by total degree descending,
	 *  - then by degree in first variable descending x^2 &gt; xy &gt; y^2 &gt; x &gt; y &gt; 1
	 *   (assuming in this example that var1=x and var2=y)
	 *  - deals with two variables
	 * @return a comparator for polynomial products with two variables.
	 */
	public static Comparator<CoeffPowerProduct> getComparator() {
		return comparator;
	}

	/**
	 *
	 * @return the expression of the product.
	 */
	public ExpressionValue getExpression() {
		return powerExp;
	}

	/**
	 *
	 * @return the coefficient of the product.
	 */
	public double getCoeffValue() {
		return coeffValue;
	}
}
