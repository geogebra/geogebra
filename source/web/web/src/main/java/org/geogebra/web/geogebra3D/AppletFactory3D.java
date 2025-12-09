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

package org.geogebra.web.geogebra3D;

import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

/**
 * 3D Applets factory class
 *
 */
public class AppletFactory3D implements AppletFactory {

	@Override
	public AppWFull getApplet(GeoGebraElement element,
			AppletParameters parameters, GeoGebraFrameFull fr,
			GLookAndFeelI laf, GDevice device) {
		return new AppWapplet3D(element, parameters, fr, (GLookAndFeel) laf, device);
	}
}
