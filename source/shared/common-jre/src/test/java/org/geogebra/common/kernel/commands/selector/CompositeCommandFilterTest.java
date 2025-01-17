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
