package org.geogebra.common.kernel.cas;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Test;

public class   AlgoDerivativeTest extends BaseUnitTest {

	@Test
	public void nDerivativeShouldNotMaskOtherExpressions() {
		t("fm(x,y,z)=x*y*z", "x * y * z");
		t("NDerivative[fm,z]", unicode("NDerivative(fm, z)"));
		t("fm", "x * y * z");
	}

	@Test
	public void fastDerivativeDirac() {
		t("f(x)=Dirac(x)", "Dirac(x)");
		t("g(x)=Heaviside(x)", "Heaviside(x)");
		EvalInfo info = new EvalInfo().withCAS(false);
		AlgoDerivative der = new AlgoDerivative(getConstruction(),
				(GeoFunction) lookup("f"), info);
		assertThat(der.getResult(), hasValue("Dirac(x)"));
		der = new AlgoDerivative(getConstruction(), (GeoFunction) lookup("g"), info);
		assertThat(der.getResult(), hasValue("Dirac(x)"));
	}

	private void t(String s, String s1) {
		AlgebraTestHelper.checkSyntaxSingle(s, new String[]{s1},
				getApp().getKernel().getAlgebraProcessor(), StringTemplate.testTemplate);
	}
}
