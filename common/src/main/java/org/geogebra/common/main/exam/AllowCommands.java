package org.geogebra.common.main.exam;

import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.AllCommandSelector;
import org.geogebra.common.kernel.commands.selector.CommandSelector;
import org.geogebra.common.kernel.commands.selector.NoCASCommandSelectorFactory;

public class AllowCommands {

	private CommandDispatcher commandDispatcher;
	private CommandSelector noCASSelector = new NoCASCommandSelectorFactory()
			.createCommandSelector();

	private CommandSelector allSelector = new AllCommandSelector();

	public AllowCommands(CommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}
	
	public void enableCAS() {
		commandDispatcher.setCommandSelector(allSelector);
	}
	
	public void disableCAS() {
		commandDispatcher.setCommandSelector(noCASSelector);
	}

	public void saveCommandSelector() {
		commandDispatcher.saveCommandSelector();
	}
	
	public void restoreCommandSelector() {
		commandDispatcher.restoreCommandSelector();
	}	
}
