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

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ArchiveLoader;

/**
 * Like production, mock async loading of commands
 *
 */
public class AppWapplet3DTest extends AppWapplet3D {

	private ArchiveLoader view;

	/**
	 * @param ae     settings
	 * @param gf     frame
	 * @param laf    laf
	 * @param device device
	 */
	public AppWapplet3DTest(AppletParameters ae, GeoGebraFrameFull gf,
							GLookAndFeel laf, GDevice device) {
		super(DomMocker.getGeoGebraElement(), ae, gf, laf, device);
	}

	@Override
	public CommandDispatcherW newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DWSync(cmdKernel);

	}

	@Override
	public ArchiveLoader getArchiveLoader() {
		if (view == null) {
			view = new ArchiveLoaderMock(this);
		}
		return view;
	}

	@Override
	public void setExport3D(Format format) {
		// no-op
	}

	@Override
	public void resetUrl() {
		// no-op
	}

	@Override
	public void invokeLater(Runnable r) {
		r.run();
	}
}
