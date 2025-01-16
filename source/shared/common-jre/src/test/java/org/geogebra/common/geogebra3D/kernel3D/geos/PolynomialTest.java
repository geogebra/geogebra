package org.geogebra.common.geogebra3D.kernel3D.geos;

import static com.himamis.retex.editor.share.util.Unicode.EULER_CHAR;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class PolynomialTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D(new AppConfigDefault());
	}

	@Test
	public void polynomialHandle0Degree() {
		add("b = 3");
		GeoLine f = add("f:y-3=x+" + EULER_CHAR + "^(b)");
		assertThat(f, hasValue("y - 3 = x + " + EULER_CHAR + Unicode.SUPERSCRIPT_3));
	}
}
