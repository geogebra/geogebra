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
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ArchiveLoader;

/**
 * Like production, mock async loading of commands
 *
 */
public class AppWSimpleMock extends AppWsimple {
	private ArchiveLoader view;

	/**
	 * @param article article element
	 * @param frame GeoGebraFrame
	 * @param undoActive if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWSimpleMock(AppletParameters article, GeoGebraFrameSimple frame,
			boolean undoActive) {
		super(DomMocker.getGeoGebraElement(), article, frame, undoActive);
	}

	@Override
	public ArchiveLoader getArchiveLoader() {
		if (view == null) {
			view = new ArchiveLoaderMock(this);
		}
		return view;
	}

	@Override
	public CommandDispatcherW newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcherWSync(cmdKernel);
	}
}