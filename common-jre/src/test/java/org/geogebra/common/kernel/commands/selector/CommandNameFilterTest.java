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
