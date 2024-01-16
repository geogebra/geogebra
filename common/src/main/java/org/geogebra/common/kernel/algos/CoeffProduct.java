package org.geogebra.common.kernel.algos;

import java.util.Comparator;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;

public class CoeffProduct {
	private ExpressionValue exp;
	private double coeffValue;
	private int degree;

	public CoeffProduct(ExpressionValue exp, double coeffValue, int degree) {
		this.exp = exp;
		this.coeffValue = coeffValue;
		this.degree = degree;
	}

	private boolean isInvalid() {
		return exp == null;
	}

	public boolean has(ExpressionValue expression) {
		return exp.contains(expression);
	}

	public int compareByame(CoeffProduct p2) {
		return exp.toString(StringTemplate.defaultTemplate).compareTo(
				p2.exp.toString(StringTemplate.defaultTemplate));
	}

	public ExpressionValue getExpression() {
		return exp;
	}

	public double getCoeffValue() {
		return coeffValue;
	}

	private static class ProductComparator implements Comparator<CoeffProduct> {
		private ExpressionValue var1;
		private ExpressionValue var2;
		public ProductComparator(FunctionVariable var1, FunctionVariable var2) {
			this.var1 = var1;
			this.var2 = var2;
		}

		@Override
		public int compare(CoeffProduct p1, CoeffProduct p2) {
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
				return p1.compareByame(p2);
			}

			return p1.degree > p2.degree ? 1: -1;
		}
	}

	public static Comparator newComparator(FunctionVariable var1, FunctionVariable var2) {
		return new ProductComparator(var1, var2);
	}
}
