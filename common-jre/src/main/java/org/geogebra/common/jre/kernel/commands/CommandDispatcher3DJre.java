package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherBasic3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherSpatial;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherBasic;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;

public class CommandDispatcher3DJre extends CommandDispatcherJre {
	private CommandDispatcherInterface commands3DDispatcher;

	public CommandDispatcher3DJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandDispatcherInterface getSpatialDispatcher() {
		if (commands3DDispatcher == null) {
			commands3DDispatcher = new CommandDispatcherSpatial();
		}

		return commands3DDispatcher;
	}

	@Override
	public CommandDispatcherBasic getBasicDispatcher() {
		if (basicDispatcher == null) {
			basicDispatcher = new CommandDispatcherBasic3D();
		}
		return basicDispatcher;
	}
}
