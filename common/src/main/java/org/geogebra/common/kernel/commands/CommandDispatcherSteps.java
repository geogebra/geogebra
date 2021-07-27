package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;

/**
 * Command dispatcher for step-by-step
 */
public class CommandDispatcherSteps implements CommandDispatcherInterface {

	@Override
	public CommandProcessor dispatch(Commands c, Kernel kernel) {
		switch (c) {
		case ShowSteps:
			return new CmdShowSteps(kernel);
		}

		return null;
	}
}
