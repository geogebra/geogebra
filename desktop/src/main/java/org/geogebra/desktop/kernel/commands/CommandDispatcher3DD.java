package org.geogebra.desktop.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherCommands3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.*;

public class CommandDispatcher3DD extends CommandDispatcher3D {

	public CommandDispatcher3DD(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected CommandDispatcherInterface get3DDispatcher() {
		if (commands3DDispatcher == null) {
			commands3DDispatcher = new CommandDispatcherCommands3D();
		}

		return commands3DDispatcher;
	}
}
