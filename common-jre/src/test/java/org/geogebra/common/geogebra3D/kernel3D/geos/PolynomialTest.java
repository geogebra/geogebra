package org.geogebra.common.geogebra3D.kernel3D.geos;

import static com.himamis.retex.editor.share.util.Unicode.EULER_CHAR;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

public class PolynomialTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void polynomialHandle0Degree() {
		t("b = 3", "3");
		t("y-3=x+" + EULER_CHAR + "^(b)", "-x + y = 23.085536923187664");
	}
}
