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

public class CommandNameFilterTest {

	@Test
	public void testFilter() {
		CommandFilter filter = new CommandNameFilter(false, Commands.Delete, Commands.Curve);
		assertTrue(filter.isCommandAllowed(Commands.Delete));
		assertTrue(filter.isCommandAllowed(Commands.Curve));
		assertTrue(filter.isCommandAllowed(Commands.CurveCartesian));

		assertFalse(filter.isCommandAllowed(Commands.Quartile1));
		assertFalse(filter.isCommandAllowed(Commands.Q1));
	}

	@Test
	public void testInverseFilter() {
		CommandFilter filter = new CommandNameFilter(true, Commands.Delete, Commands.Curve);
		assertFalse(filter.isCommandAllowed(Commands.Delete));
		assertFalse(filter.isCommandAllowed(Commands.Curve));
		assertFalse(filter.isCommandAllowed(Commands.CurveCartesian));

		assertTrue(filter.isCommandAllowed(Commands.Quartile1));
		assertTrue(filter.isCommandAllowed(Commands.Q1));
	}
}
