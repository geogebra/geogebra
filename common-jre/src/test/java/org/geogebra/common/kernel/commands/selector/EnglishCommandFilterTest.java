package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Test;

public class EnglishCommandFilterTest implements CommandFilter {

	@Test
	public void testEnglishCommandFilter() {
		EnglishCommandFilter filter = new EnglishCommandFilter(this);
		Assert.assertFalse(filter.isCommandAllowed(Commands.CircumcircularSector));
		Assert.assertFalse(filter.isCommandAllowed(Commands.CircumcircleSector));
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		return command == Commands.CircumcircularSector;
	}
}
