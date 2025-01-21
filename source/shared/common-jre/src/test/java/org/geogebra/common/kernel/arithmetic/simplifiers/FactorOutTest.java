package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class FactorOutTest extends BaseSimplifyTest {

	@Test
	public void testFactorAdditionOfPositives() {
		shouldSimplify("2 + 2sqrt(2)", "2 (1 + sqrt(2))");
		shouldSimplify("2sqrt(2) + 2", "2 (sqrt(2) + 1)");

		shouldSimplify("2 + 6sqrt(2)", "2 (1 + 3sqrt(2))");
		shouldSimplify("6sqrt(2) + 2", "2 (3sqrt(2) + 1)");
	}

	@Test
	public void testFactorSubtractionOfPositives() {
		shouldSimplify("2 - 2sqrt(2)", "2 (1 - sqrt(2))");
		shouldSimplify("2sqrt(2) - 2", "2 (sqrt(2) - 1)");

		shouldSimplify("12 - 4sqrt(2)", "4 (3 - sqrt(2))");
		shouldSimplify("4 - 12sqrt(2)", "4 (1 - 3sqrt(2))");
		shouldSimplify("4sqrt(2) - 12", "4 (sqrt(2) - 3)");
		shouldSimplify("12sqrt(2) - 4", "4 (3sqrt(2) - 1)");
	}

	@Test
	public void testFactorSubtractionOfNegatives() {
		shouldSimplify("(-2 - 2sqrt(2))", "-2 (1 + sqrt(2))");
		shouldSimplify("(-2sqrt(2) - 2)", "-2 (sqrt(2) + 1)");
		shouldSimplify("-2 - 10sqrt(2)", "-2 (1 + 5sqrt(2))");
		shouldSimplify("-10 - 2sqrt(2)", "-2 (5 + sqrt(2))");
	}

	@Test
	public void testFactorOutMixed() {
		shouldSimplify("(-8 + 2sqrt(2)) (-2 - sqrt(6))", "-2 (4 + sqrt(2)) (-2 - sqrt(6))");
		shouldSimplify("((sqrt(2) - 5) (2 - 2sqrt(2)))", "((2 * (1 - sqrt(2)) (sqrt(2) - 5)))");
		shouldSimplify("(2sqrt(2) + -2) / -5", "(2 (sqrt(2) - 1)) / -5");
	}

	@Test
	public void testAccept() {
		shouldNotAccept("(sqrt(10) - 10) / 8");
		shouldNotAccept("(sqrt(10) - 10)");
		shouldAccept("2 + 6sqrt(2)");
		shouldNotAccept("2 + sqrt(2)");
		shouldAccept("-2 + 6sqrt(2)");
		shouldNotAccept("sqrt(2) - 2");
		shouldNotAccept("-2 + sqrt(2)");
		shouldNotAccept("sqrt(2) - 1");
		shouldNotAccept("(sqrt(2) - 1)");
		shouldNotAccept("-(sqrt(2) - 1)");
		shouldNotAccept("(3 - sqrt(2))");
		shouldNotAccept("(3 - 2sqrt(2))");
		shouldNotAccept("(2 - 3sqrt(2))");
		shouldNotAccept("(1 - 3sqrt(2))");
		shouldNotAccept("(3 + sqrt(2))");
		shouldNotAccept("(3 + 2sqrt(2))");
		shouldNotAccept("(2 + 3sqrt(2))");
		shouldNotAccept("(1 + 3sqrt(2))");
		shouldNotAccept("-(3 - sqrt(2))");
		shouldNotAccept("-(3 - 2sqrt(2))");
		shouldNotAccept("-(2 - 3sqrt(2))");
		shouldNotAccept("-(1 - 3sqrt(2))");
		shouldNotAccept("-(3 + sqrt(2))");
		shouldNotAccept("-(3 + 2sqrt(2))");
		shouldNotAccept("-(2 + 3sqrt(2))");
		shouldNotAccept("-(1 + 3sqrt(2))");
		shouldNotAccept("4 (-1 + sqrt(2))");
		shouldNotAccept("-10 + sqrt(10)");
		shouldAccept("3 (2 + 2sqrt(2))");
		shouldNotAccept("(-3 - sqrt(2))");
	}

	@Test
	public void testFactorOutNominator() {
		shouldSimplify("(2 + 2sqrt(2)) / -4", "(-2(1+sqrt(2)))/4",
				getSimplifier(), new PositiveDenominator(utils));
		shouldSimplify("(-2 - 2sqrt(2)) / 4", "(-2 (1 + sqrt(2))) / 4");
		shouldSimplify("(2 - 2sqrt(2)) / 4", "(2 (1 - sqrt(2))) / 4");
	}

	@Test
	public void testFactorOutMultipliedExpressions() {
		shouldSimplify("3 (2 + 2sqrt(2))", "6 (1 + sqrt(2))");
		shouldSimplify("3 (2 - 10sqrt(2))", "6 (1 - 5sqrt(2))");
		shouldSimplify("3 (-2 - 10sqrt(2))", "-6 (1 + 5sqrt(2))");
	}

	@Test
	public void testMinusTimesMinusShouldFlipOperand() {
		shouldSimplify("-6 (-2 - 2sqrt(2))", "12 (1 + sqrt(2))");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return FactorOut.class;
	}
}
