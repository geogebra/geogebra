package org.geogebra.web.test;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherBasic3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherSpatial;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherBasic;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;

public class CommandDispatcher3DWSync extends CommandDispatcherWSync {
	public CommandDispatcher3DWSync(Kernel kernel) {
		super(kernel);
	}

	public CommandDispatcherInterface getSpatialDispatcher() {
		return new CommandDispatcherSpatial();
	}

	public CommandDispatcherBasic getBasicDispatcher() {
		return new CommandDispatcherBasic3D();
	}
}
