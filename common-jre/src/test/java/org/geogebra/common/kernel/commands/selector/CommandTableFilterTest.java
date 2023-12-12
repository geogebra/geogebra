package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.junit.Assert;
import org.junit.Test;

public class CommandTableFilterTest {

	@Test
	public void testFilter3DTable() {
		CommandFilter filter = new TableCommandFilter(CommandsConstants.TABLE_3D);
		Assert.assertFalse(filter.isCommandAllowed(Commands.Cube));
		Assert.assertFalse(filter.isCommandAllowed(Commands.Cone));
		Assert.assertTrue(filter.isCommandAllowed(Commands.Angle));
	}

	@Test
	public void testFilterMultipleTables() {
		CommandFilter filter = new TableCommandFilter(CommandsConstants.TABLE_ALGEBRA,
				CommandsConstants.TABLE_GEOMETRY, CommandsConstants.TABLE_SCRIPTING);
		Assert.assertFalse(filter.isCommandAllowed(Commands.NSolve));
		Assert.assertFalse(filter.isCommandAllowed(Commands.Angle));
		Assert.assertFalse(filter.isCommandAllowed(Commands.TurtleForward));
		Assert.assertTrue(filter.isCommandAllowed(Commands.Numerator));
	}
}
