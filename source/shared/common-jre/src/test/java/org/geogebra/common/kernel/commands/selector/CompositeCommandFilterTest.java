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

package org.geogebra.common.kernel.commands.selector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.commands.Commands;
import org.junit.Test;

public class CompositeCommandFilterTest {

	@Test
	public void testCompositeCommandFilter() {
		CompositeCommandFilter commandFilter = new CompositeCommandFilter(
				command -> command == Commands.Cone, command -> command == Commands.Cube);
		assertFalse(commandFilter.isCommandAllowed(Commands.Cone));
		assertFalse(commandFilter.isCommandAllowed(Commands.Cube));
	}

	@Test
	public void testEmptyCompositeCommandFilter() {
		CompositeCommandFilter filter = new CompositeCommandFilter();
		assertTrue(filter.isCommandAllowed(Commands.Cube));
	}
}
