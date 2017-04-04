package org.geogebra.web.web.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;

public class ResourcesInjectorReTeX extends ResourcesInjector {
	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		jQueryNoConflict();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.jqueryStyle().getText());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
	}

	private native void jQueryNoConflict() /*-{
		$wnd.$ggbQuery = $wnd.jQuery;
		$wnd.jQuery.noConflict();
	}-*/;
}
