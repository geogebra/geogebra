package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.BasicCommandProcessorFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;

/**
 * Adds 3D support to {@link CommandDispatcherJre}
 */
public class CommandDispatcher3DJre extends CommandDispatcherJre {
	private CommandProcessorFactory spatialCommandProcessorFactory;

	public CommandDispatcher3DJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getSpatialCommandProcessorFactory() {
		if (spatialCommandProcessorFactory == null) {
			spatialCommandProcessorFactory = new SpatialCommandProcessorFactory();
		}
		return spatialCommandProcessorFactory;
	}

	@Override
	public CommandProcessorFactory getBasicCommandProcessorFactory() {
		if (basicFactory == null) {
			basicFactory = new BasicCommandProcessorFactory3D();
		}
		return basicFactory;
	}
}
