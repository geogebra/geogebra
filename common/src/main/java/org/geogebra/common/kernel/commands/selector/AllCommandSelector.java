package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Selector to allow all the commands.
 * 
 * @author laszlo
 *
 */
public class AllCommandSelector implements CommandSelector {

	@Override
	public boolean isCommandAllowed(Commands command) {
		return true;
	}

}
