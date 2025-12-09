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

package org.geogebra.web.geogebra3D.web.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.commands.BasicCommandProcessorFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Adds 3D support to {@link CommandDispatcherW}.
 */
public class CommandDispatcher3DW extends CommandDispatcherW {

	/** dispatcher for 3D commands */
	private static CommandProcessorFactory spatialCommandProcessorFactory = null;

	/**
	 * @param kernel
	 *            kernel
	 */
	public CommandDispatcher3DW(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getBasicCommandProcessorFactory() {
		if (basicFactory == null) {
			basicFactory = new BasicCommandProcessorFactory3D();
		}
		return basicFactory;
	}

	@Override
	public CommandProcessorFactory getSpatialCommandProcessorFactory() {
		if (spatialCommandProcessorFactory == null) {
			GWT.runAsync(SpatialCommandProcessorFactory.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for 3D commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("3D commands");
					spatialCommandProcessorFactory = new SpatialCommandProcessorFactory();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("3D commands not loaded yet");
		}

		return spatialCommandProcessorFactory;
	}
}
