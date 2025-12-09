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

import org.geogebra.web.full.Web;
import org.geogebra.web.full.gui.applet.AppletFactory;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class Web3D extends Web {

	@Override
	public void onModuleLoad() {
		Function onReady = (Function) Js.asPropertyMap(DomGlobal.window)
				.nestedGet("web3d.onReady");
		if (onReady != null) {
			onReady.call(DomGlobal.window, "mathApps",
					new RenderMathApps(getLAF(), getAppletFactory()));

		} else {
			super.onModuleLoad();
		}
	}

	@Override
	protected AppletFactory getAppletFactory() {
		return new AppletFactory3D();
	}
}
