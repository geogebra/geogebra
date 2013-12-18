package geogebra.html5.js;

import geogebra.html5.Browser;
import geogebra.html5.css.GuiResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;

/**
 * @author gabor
 * 
 * injects the javascript resources
 *
 */
public class ResourcesInjector {

	public static void injectResources() {
		// always need English properties available, eg Function.sin
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());
		
		String myModuleBase = GWT.getModuleBaseForStaticFiles();
		String mathquillggbcss = GuiResources.INSTANCE.mathquillggbCss().getText().
				replace("url(web/font/Symbola", "url(" + myModuleBase + "font/Symbola");
		StyleInjector.inject(mathquillggbcss);
	
		//insert zip.js
		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs());
		JavaScriptInjector.inject(GuiResources.INSTANCE.jQueryJs());
		JavaScriptInjector.inject(GuiResources.INSTANCE.mathquillggbJs());
		Browser.webWorkerSupported = Browser.checkWorkerSupport(GWT.getModuleBaseURL());
		if (!Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.arrayBufferJs());
		//strange, but iPad can blow it away again...
		if (Browser.checkIfFallbackSetExplicitlyInArrayBufferJs() && Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.dataViewJs());
		JavaScriptInjector.inject(GuiResources.INSTANCE.base64Js());
	}

}
