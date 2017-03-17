package org.geogebra.web.web.helper;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.js.ResourcesInjector;

public class ResourcesInjectorReTeX extends ResourcesInjector {
	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		jQueryNoConflict();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.jqueryStyle().getText());
	}

	private native void jQueryNoConflict() /*-{
		$wnd.$ggbQuery = $wnd.jQuery;
		$wnd.jQuery.noConflict();
	}-*/;
}
