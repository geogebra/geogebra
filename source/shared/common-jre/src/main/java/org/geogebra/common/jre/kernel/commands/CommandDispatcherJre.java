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

package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ProverCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;

/**
 * Command dispatcher that creates command processors (via factories)
 * synchronously. Does not support 3D specific commands.
 */
public class CommandDispatcherJre extends CommandDispatcher {

	public CommandDispatcherJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getStatsCommandProcessorFactory() {
		if (statsFactory == null) {
			statsFactory = new StatsCommandProcessorFactory();
		}
		return statsFactory;
	}

	@Override
	public CommandProcessorFactory getDiscreteCommandProcessorFactory() {
		if (discreteFactory == null) {
			discreteFactory = new DiscreteCommandProcessorFactory();
		}
		return discreteFactory;
	}

	@Override
	public CommandProcessorFactory getCASCommandProcessorFactory() {
		if (casFactory == null) {
			casFactory = new CASCommandProcessorFactory();
		}
		return casFactory;
	}

	@Override
	public CommandProcessorFactory getScriptingCommandProcessorFactory() {
		if (scriptingFactory == null) {
			scriptingFactory = new ScriptingCommandProcessorFactory();
		}
		return scriptingFactory;
	}

	@Override
	public CommandProcessorFactory getAdvancedCommandProcessorFactory() {
		if (advancedFactory == null) {
			advancedFactory = new AdvancedCommandProcessorFactory();
		}
		return advancedFactory;
	}

	@Override
	public CommandProcessorFactory getProverCommandProcessorFactory() {
		if (proverFactory == null) {
			proverFactory = new ProverCommandProcessorFactory();
		}
		return proverFactory;
	}
}
