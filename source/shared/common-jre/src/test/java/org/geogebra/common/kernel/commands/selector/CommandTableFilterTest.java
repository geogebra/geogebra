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
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.junit.Test;

public class CommandTableFilterTest {

	@Test
	public void testFilter3DTable() {
		CommandFilter filter = new CommandTableFilter(CommandsConstants.TABLE_3D);
		assertFalse(filter.isCommandAllowed(Commands.Cube));
		assertFalse(filter.isCommandAllowed(Commands.Cone));
		assertTrue(filter.isCommandAllowed(Commands.Angle));
	}

	@Test
	public void testFilterMultipleTables() {
		CommandFilter filter = new CommandTableFilter(CommandsConstants.TABLE_ALGEBRA,
				CommandsConstants.TABLE_GEOMETRY, CommandsConstants.TABLE_SCRIPTING);
		assertFalse(filter.isCommandAllowed(Commands.NSolve));
		assertFalse(filter.isCommandAllowed(Commands.Angle));
		assertFalse(filter.isCommandAllowed(Commands.TurtleForward));
		assertTrue(filter.isCommandAllowed(Commands.Numerator));
	}

	@Test
	public void testEnglishNamesAreConverted() {
		CommandFilter filter = new CommandTableFilter(CommandsConstants.TABLE_STATISTICS);
		assertFalse(filter.isCommandAllowed(Commands.Quartile1));
	}
}
