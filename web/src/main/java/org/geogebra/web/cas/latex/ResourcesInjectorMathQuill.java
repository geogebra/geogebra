package org.geogebra.web.cas.latex;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.js.ResourcesInjector;

import com.google.gwt.core.client.GWT;

public class ResourcesInjectorMathQuill extends ResourcesInjector {
	@Override
	protected void injectResourcesGUI(boolean forceReTeX) {
		String myModuleBase = GWT.getModuleBaseForStaticFiles();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		if (!forceReTeX) {
			JavaScriptInjector
					.inject(MathQuillResources.INSTANCE.mathquillggbJs());
			String mathquillggbcss = MathQuillResources.INSTANCE
					.mathquillggbCss().getText().replace("url(web/font/Symbola",
							"url(" + myModuleBase + "font/Symbola");
			StyleInjector.inject(mathquillggbcss);
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.jqueryStyle().getText());
		if (forceReTeX) {
			jQueryNoConflict();
		}
	}

	private native void jQueryNoConflict() /*-{
		$wnd.$ggbQuery = $wnd.jQuery;
		$wnd.jQuery.noConflict();
	}-*/;
}
