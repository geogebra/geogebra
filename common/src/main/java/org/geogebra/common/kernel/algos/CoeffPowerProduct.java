package org.geogebra.common.kernel.algos;

import java.util.Comparator;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;

/**
 * Class to store the product and properties of
 * the product of the coefficent and expression for a polynomial.
 */
public final class CoeffPowerProduct {
	private ExpressionValue powerExp;

	private double coeffValue;
	private int degree;

	/**
	 *
	 * @param powerExp expression of the product
	 * @param coeffValue the coefficenrt
	 * @param degree the degree of thr expression.
	 */
	public CoeffPowerProduct(ExpressionValue powerExp, double coeffValue, int degree) {
		this.powerExp = powerExp;
		this.coeffValue = coeffValue;
		this.degree = degree;
	}

	/**
	 * Creates a comparator for sorting CoeffProduct lists, such as
	 *  - first by total degree descending,
	 *  - then by degree in x descending x^2 > xy > y^2 > x > y > 1
	 *   (assuming in this example that var1=x and var2=y)
	 *  - deals with two variables
	 *
	 * @param var1 the first possible variable of the product.
	 * @param var2 the first possible variable of the product.
	 * @return a comparator for polynomial products with two variables.
	 */
	public static Comparator newComparator(FunctionVariable var1, FunctionVariable var2) {
		return new ProductComparator(var1, var2);
	}

	private static final class ProductComparator implements Comparator<CoeffPowerProduct> {
		private ExpressionValue var1;

		private ExpressionValue var2;

		private ProductComparator(FunctionVariable var1, FunctionVariable var2) {
			this.var1 = var1;
			this.var2 = var2;
		}

		@Override
		public int compare(CoeffPowerProduct p1, CoeffPowerProduct p2) {
			if (p1.isInvalid()) {
				return 1;
			}

			if (p2.isInvalid()) {
				return -1;
			}

			if (p1.degree > 1 || p2.degree > 1) {
				if (p1.degree == p2.degree) {
					return p1.has(var1) && p1.has(var2) ? 1 : -1;
				}
				return 1;
			}

			if (p1.degree == p2.degree) {
				return p1.compareByName(p2);
			}

			return p1.degree > p2.degree ? 1 : -1;
		}

	}

	private boolean isInvalid() {
		return powerExp == null;
	}

	private boolean has(ExpressionValue expression) {
		return powerExp.contains(expression);
	}

	private int compareByName(CoeffPowerProduct p2) {
		return powerExp.toString(StringTemplate.defaultTemplate).compareTo(
				p2.powerExp.toString(StringTemplate.defaultTemplate));
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
	 * @return the coefficent of the product.
	 */
	public double getCoeffValue() {
		return coeffValue;
	}
}
