package org.geogebra.web.test;

import org.geogebra.common.geogebra3D.kernel3D.commands.BasicCommandProcessorFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.BasicCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;

/**
 * Adds 3D support to {@link CommandDispatcherWSync}.
 */
public class CommandDispatcher3DWSync extends CommandDispatcherWSync {
	public CommandDispatcher3DWSync(Kernel kernel) {
		super(kernel);
	}

	public CommandProcessorFactory getSpatialCmdFactory() {
		return new SpatialCommandProcessorFactory();
	}

	public BasicCommandProcessorFactory getBasicDispatcher() {
		return new BasicCommandProcessorFactory3D();
	}
}
