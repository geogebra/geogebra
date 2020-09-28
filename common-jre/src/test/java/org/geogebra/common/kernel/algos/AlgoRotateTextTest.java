package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class AlgoRotateTextTest extends BaseUnitTest {

	@Test
	public void serifPropertyShouldBePersistent() {
		GeoText in = addAvInput("txt=\"Rotate me\"");
		in.setSerifFont(true);
		GeoText rotated = addAvInput("RotateText(txt,1)");
		assertThat(rotated.isSerifFont(), is(true));
		in.setSerifFont(false);
		addAvInput("UpdateConstruction()");
		assertThat(rotated.isSerifFont(), is(true));
	}
}
