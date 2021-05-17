package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class AlgoIfTest extends BaseUnitTest {

	@Test
	public void listTypeShouldFollowNonEmptyAlternative() {
		GeoList list = add("If(true,{},If(true,{},{\"text\"}))");
		assertEquals("text", list.getTypeStringForXML());
	}
}
