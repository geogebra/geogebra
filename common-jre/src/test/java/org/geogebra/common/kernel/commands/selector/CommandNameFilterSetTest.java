package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandNameFilterSetTest {

	private CommandFilter filter = null;

	@Before
	public void setupTest() {
		filter = CommandFilterFactory.createCasCommandFilter();
	}

	@Test
	public void deleteShouldBeDisabled() {
		Assert.assertFalse(filter.isCommandAllowed(Commands.Delete));
	}

	@Test
	public void firstShouldBeDisabled() {
		Assert.assertTrue(filter.isCommandAllowed(Commands.First));
	}

	@Test
	public void minShouldBeEnabled() {
		Assert.assertTrue(filter.isCommandAllowed(Commands.Min));
	}
}
