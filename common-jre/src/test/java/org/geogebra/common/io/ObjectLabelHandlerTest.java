package org.geogebra.common.io;

import static org.junit.Assert.assertArrayEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class ObjectLabelHandlerTest extends BaseUnitTest {

	@Test
	public void shouldFindObjects() {
		add("Slider[1,4]");
		add("(1,1)+(3,2)");
		add("Midpoint(A,(0,0))");
		assertArrayEquals(new String[]{"a", "A", "B"},
				ObjectLabelHandler.findObjectNames(getApp().getXML()));
	}

}
