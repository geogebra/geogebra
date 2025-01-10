package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;

/**
 * Factory creating a command processor for given command.
 * Several separate factories are used for different command groups
 * to allow code splitting into chunks (Web) or separate .jar files (Desktop).
 *
 * @apiNote factories implementing this interface should not be directly referenced in
 * {@code common} so that GWT code splitting works properly. Exception to this rule
 * is {@link BasicCommandProcessorFactory} which is part of the initial chunk.
 *
 * @author gabor
 */
public interface CommandProcessorFactory {

	/**
	 * @param command
	 *            Command
	 * @param kernel
	 *            Kernel
	 * @return CommandProcessor
	 */
	CommandProcessor getProcessor(Commands command, Kernel kernel);

}
