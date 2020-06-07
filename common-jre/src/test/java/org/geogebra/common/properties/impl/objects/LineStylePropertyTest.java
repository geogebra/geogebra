package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class LineStylePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		new LineStyleProperty(line);
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement point = addAvInput("(1,1)");
		assertThrows(NotApplicablePropertyException.class, () -> new LineStyleProperty(point));
	}
}