package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class AlgoElementTest extends BaseUnitTest {

	@Test
	public void latexIntegral() {
		add("f(n)=n^2");
		assertThat(add("Integral(x,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}x\\,\\mathrm{d}x"));
		assertThat(add("Integral(x)"), hasLaTeXDefinition("\\int x\\,\\mathrm{d}x"));
		assertThat(add("Integral(f,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}f\\,\\mathrm{d}n"));
		assertThat(add("Integral(f)"), hasLaTeXDefinition("\\int f\\,\\mathrm{d}n"));
		assertThat(add("Sequence(Integral(x^k),k,1,2)"), hasLaTeXDefinition(
				"Sequence\\left(\\int x^{k}\\,\\mathrm{d}x, k, 1, 2 \\right)"));
	}

	@Test
	public void latexIntegralShouldHaveCorrectDerivativeVariable() {
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertThat(add("Integral(x-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}x - d\\,\\mathrm{d}x"));
		assertThat(add("Integral(t-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}t - d\\,\\mathrm{d}t"));
		assertThat(add("Integral(s-d,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}s - d\\,\\mathrm{d}d"));
		assertThat(add("Integral(s-r,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}s - r\\,\\mathrm{d}r"));
		assertThat(add("Integral(t-x,a,b)"), hasLaTeXDefinition(
				"\\int\\limits_{a}^{b}t - x\\,\\mathrm{d}x"));
	}

	private TypeSafeMatcher<GeoElement> hasLaTeXDefinition(String def) {
		return hasProperty("definition", item ->
				item.getDefinition(StringTemplate.latexTemplate), def);
	}
}
