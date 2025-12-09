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

package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

/**
 * Tests for CopyFreeObject command
 */
public class CopyFreeObjectTest extends BaseUnitTest {

	/**
	 * Check that all properties are maintained for points
	 */
	@Test
	public void copyShouldHaveSameXML() {
		add("A=(1,1)");
		lookup("A").setAnimationStep(2.0);
		String aXml = lookup("A").getXML();
		add("B=CopyFreeObject(A)");
		add("Delete(A)");
		add("Rename(B,A)");
		assertEquals(aXml, lookup("A").getXML());
	}
}
