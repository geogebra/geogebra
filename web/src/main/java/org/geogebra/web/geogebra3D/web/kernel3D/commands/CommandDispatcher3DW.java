package org.geogebra.web.geogebra3D.web.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherCommands3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Async command distpatcher for Web
 *
 */
public class CommandDispatcher3DW extends CommandDispatcher3D {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CommandDispatcher3DW(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandDispatcherInterface get3DDispatcher() {
		if (commands3DDispatcher == null) {
			GWT.runAsync(CommandDispatcher3D.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for 3D commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("3D commands");
					commands3DDispatcher = new CommandDispatcherCommands3D();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("3D commands not loaded yet");
		}

		return commands3DDispatcher;
	}
}
