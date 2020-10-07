package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoFunctionTest extends BaseUnitTest {

	@Test
	public void testEquals() {
		GeoFunction func1 = addAvInput("f(x)=x+2");
		GeoFunction func2 = addAvInput("g(x)=2+x");
		assertThat(func1.isEqual(func2), is(true));
		assertThat(func2.isEqual(func1), is(true));
		addAvInput("SetValue(f,?)");
		assertThat(func1.isEqual(func2), is(false));
		assertThat(func2.isEqual(func1), is(false));
	}
}