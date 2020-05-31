package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class EquationFormPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		getApp().setGeometryConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		new EquationFormProperty(line);
	}

	@Test
	public void testConstructorThrowsError() {
		getApp().setGraphingConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		assertThrows(NotApplicablePropertyException.class, () -> new EquationFormProperty(line));
	}
}