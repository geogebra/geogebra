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

package org.geogebra.web.test;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ProverCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;

/**
 * Synchronized version of Command Dispatcher.
 * Functionally equivalent to the {@code common-jre} implementation which cannot be
 * simply imported to web because of code splitting.
 */
class CommandDispatcherWSync extends CommandDispatcherW {

	public CommandDispatcherWSync(Kernel cmdKernel) {
		super(cmdKernel);
	}

	@Override
	public CommandProcessorFactory getStatsCommandProcessorFactory() {
		return new StatsCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getDiscreteCommandProcessorFactory() {
		return new DiscreteCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getCASCommandProcessorFactory() {
		return new CASCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getScriptingCommandProcessorFactory() {
		return new ScriptingCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getAdvancedCommandProcessorFactory() {
		return new AdvancedCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getProverCommandProcessorFactory() {
		return new ProverCommandProcessorFactory();
	}
}
