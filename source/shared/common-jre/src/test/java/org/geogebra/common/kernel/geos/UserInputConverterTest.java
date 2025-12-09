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

package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.geos.inputbox.UserInputConverter;
import org.junit.Test;

public class UserInputConverterTest {
	private UserInputConverter converter = new UserInputConverter();

	@Test
	public void defined2DMatrixShouldStay() {
		assertEquals("{{1},{2}}",
				converter.matrixToUndefined("{{1},{2}}"));
	}

	@Test
	public void empty4x4MatrixToUndefined() {
		assertEquals("{{?,?,?,?},{?,?,?,?},{?,?,?,?},{?,?,?,?}}",
				converter.matrixToUndefined("{{,,,},{,,,},{,,,},{,,,}}"));
	}

	@Test
	public void emptyVectorToUndefined() {
		assertEquals("{{?},{?},{?}}",
				converter.matrixToUndefined("{{},{},{}}"));
	}

	@Test
	public void semiEmpty4x4MatrixToUndefined() {
		converter = new UserInputConverter();
		assertEquals("{{1,?,?,?},{?,2,?,?},{?,?,3,?},{?,4,5,6}}",
				converter.matrixToUndefined("{{1,,,},{,2,,},{,,3,},{,4,5,6}}"));
	}
}
