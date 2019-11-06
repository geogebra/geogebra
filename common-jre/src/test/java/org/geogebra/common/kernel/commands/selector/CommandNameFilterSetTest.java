package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandNameFilterSetTest extends BaseUnitTest {

	private CommandNameFilter filter = null;

	@Before
	public void setupTest() {
		filter = CommandNameFilterFactory.createCasCommandNameFilter();
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
	public void minShouldBeDisabled() {
		Assert.assertFalse(filter.isCommandAllowed(Commands.Min));
	}
}
