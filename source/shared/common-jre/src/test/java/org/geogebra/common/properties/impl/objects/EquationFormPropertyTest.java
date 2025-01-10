package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class EquationFormPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorForLineInGeometry() {
		getApp().setGeometryConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		try {
			new EquationFormProperty(getLocalization(), line);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorForUserFunctionInGeometry() {
		getApp().setGeometryConfig();
		GeoElement f = addAvInput("f(x) = x");
		assertThrows(NotApplicablePropertyException.class,
				() -> new EquationFormProperty(getLocalization(), f));
	}

	@Test
	public void testConstructorForLineListInGeometry() {
		getApp().setGeometryConfig();
		addAvInput("f = Line((1,1),(2,2))");
		addAvInput("g = Line((3,4),(4,3))");
		GeoList list = addAvInput("{f, g}");
		try {
			new EquationFormProperty(getLocalization(), list);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorForLineAndUserFunctionListInGeometry() {
		getApp().setGeometryConfig();
		addAvInput("f = Line((1,1),(2,2))");
		addAvInput("g(x) = x");
		GeoList list = addAvInput("{f, g}");
		assertThrows(NotApplicablePropertyException.class,
				() -> new EquationFormProperty(getLocalization(), list));
	}

	@Test
	public void testConstructorForLineInGraphing() {
		getApp().setGraphingConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		assertThrows(NotApplicablePropertyException.class,
				() -> new EquationFormProperty(getLocalization(), line));
	}

	@Test
	public void testConstructorForLineListInGraphing() {
		getApp().setGraphingConfig();
		addAvInput("f = Line((1,1),(2,2))");
		addAvInput("g = Line((3,4),(4,3))");
		GeoList list = addAvInput("{f, g}");
		assertThrows(NotApplicablePropertyException.class,
				() -> new EquationFormProperty(getLocalization(), list));
	}
}