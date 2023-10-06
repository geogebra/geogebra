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
