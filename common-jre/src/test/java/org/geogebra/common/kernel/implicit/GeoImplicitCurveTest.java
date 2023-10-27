package org.geogebra.common.kernel.implicit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoImplicitCurveTest extends BaseUnitTest {

	@Test
	public void toValueStringTest() {
		GeoElement implicit = add("sqrt(2)/sqrt(x)=4");
		assertThat(implicit.toValueString(StringTemplate.algebraTemplate),
				is("r(2) / r(x) = 4".replace('r', Unicode.SQUARE_ROOT)));
	}
}
