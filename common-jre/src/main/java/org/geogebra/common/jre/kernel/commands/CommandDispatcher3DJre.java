package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.BasicCommandProcessorFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.BasicCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;

/**
 * Adds 3D support to {@link CommandDispatcherJre}
 */
public class CommandDispatcher3DJre extends CommandDispatcherJre {
	private CommandProcessorFactory commands3DDispatcher;

	public CommandDispatcher3DJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getSpatialCmdFactory() {
		if (commands3DDispatcher == null) {
			commands3DDispatcher = new SpatialCommandProcessorFactory();
		}

		return commands3DDispatcher;
	}

	@Override
	public BasicCommandProcessorFactory getBasicDispatcher() {
		if (basicDispatcher == null) {
			basicDispatcher = new BasicCommandProcessorFactory3D();
		}
		return basicDispatcher;
	}
}
