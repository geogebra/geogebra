/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class LinearEquationFormPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorForLineInGeometry() {
		getApp().setGeometryConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		try {
			new LinearEquationFormProperty(getLocalization(), line);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorForUserFunctionInGeometry() {
		getApp().setGeometryConfig();
		GeoElement f = addAvInput("f(x) = x");
		assertThrows(NotApplicablePropertyException.class,
				() -> new LinearEquationFormProperty(getLocalization(), f));
	}

	@Test
	public void testConstructorForLineListInGeometry() {
		getApp().setGeometryConfig();
		addAvInput("f = Line((1,1),(2,2))");
		addAvInput("g = Line((3,4),(4,3))");
		GeoList list = addAvInput("{f, g}");
		try {
			new LinearEquationFormProperty(getLocalization(), list);
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
				() -> new LinearEquationFormProperty(getLocalization(), list));
	}

	@Test
	public void testConstructorForLineInGraphing() {
		getApp().setGraphingConfig();
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		assertThrows(NotApplicablePropertyException.class,
				() -> new LinearEquationFormProperty(getLocalization(), line));
	}

	@Test
	public void testConstructorForLineListInGraphing() {
		getApp().setGraphingConfig();
		addAvInput("f = Line((1,1),(2,2))");
		addAvInput("g = Line((3,4),(4,3))");
		GeoList list = addAvInput("{f, g}");
		assertThrows(NotApplicablePropertyException.class,
				() -> new LinearEquationFormProperty(getLocalization(), list));
	}
}