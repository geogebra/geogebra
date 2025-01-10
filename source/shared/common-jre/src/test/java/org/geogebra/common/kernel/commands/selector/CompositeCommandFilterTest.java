package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Test;

public class CompositeCommandFilterTest {

	@Test
	public void testCompositeCommandFilter() {
		CompositeCommandFilter commandFilter = new CompositeCommandFilter(
				command -> command == Commands.Cone, command -> command == Commands.Cube);
		Assert.assertFalse(commandFilter.isCommandAllowed(Commands.Cone));
		Assert.assertFalse(commandFilter.isCommandAllowed(Commands.Cube));
	}

	@Test
	public void testEmptyCompositeCommandFilter() {
		CompositeCommandFilter filter = new CompositeCommandFilter();
		Assert.assertTrue(filter.isCommandAllowed(Commands.Cube));
	}
}
