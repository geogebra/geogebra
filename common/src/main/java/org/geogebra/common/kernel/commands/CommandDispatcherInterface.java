package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;

/**
 * @author gabor
 * 
 *         interface for use in async calls
 *
 */
public interface CommandDispatcherInterface {

	/**
	 * @param c
	 *            Command
	 * @param kernel
	 *            Kernel
	 * @param command
	 * @return CommandProcessor
	 */
	public CommandProcessor dispatch(Commands c, Kernel kernel,
			Command command);

}
