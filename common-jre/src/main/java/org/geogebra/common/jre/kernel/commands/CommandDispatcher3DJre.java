package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherCommands3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;

public class CommandDispatcher3DJre extends CommandDispatcher3D {

	public CommandDispatcher3DJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandDispatcherInterface get3DDispatcher() {
		if (commands3DDispatcher == null) {
			commands3DDispatcher = new CommandDispatcherCommands3D();
		}
		return commands3DDispatcher;
	}
}
