package org.geogebra.common.kernel.cas;

import static org.geogebra.test.TestStringUtil.unicode;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Test;

public class AlgoDerivativeTest extends BaseUnitTest {

	@Test
	public void nDerivativeShouldNotMaskOtherExpressions() {
		t("fm(x,y,z)=x*y*z", "x * y * z");
		t("NDerivative[fm,z]", unicode("NDerivative(fm, z)"));
		t("fm", "x * y * z");
	}

	private void t(String s, String s1) {
		AlgebraTestHelper.testSyntaxSingle(s, new String[]{s1},
				getApp().getKernel().getAlgebraProcessor(),	StringTemplate.testTemplate);
	}
}
