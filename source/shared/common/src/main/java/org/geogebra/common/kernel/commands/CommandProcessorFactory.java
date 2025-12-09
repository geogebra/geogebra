/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
