package org.geogebra.common.kernel.kernelND;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class GeoSurfaceCartesian2DTest extends BaseUnitTest {

	@Test
	public void testUndefined() {
		GeoSurfaceCartesian2D surfaceCartesian2D =
				new GeoSurfaceCartesian2D(getConstruction(), null, null);
		assertThat(
				surfaceCartesian2D.toString(StringTemplate.defaultTemplate),
				equalTo("?"));
	}
}