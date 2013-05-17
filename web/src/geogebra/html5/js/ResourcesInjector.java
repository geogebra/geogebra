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
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS().getText());
		
		String myModuleBase = GWT.getModuleBaseForStaticFiles();
		String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText().
				replace("url(web/font/Symbola", "url(" + myModuleBase + "font/Symbola");
		StyleInjector.inject(mathquillcss);
	
		//insert zip.js
		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.jQueryJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.mathquillJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.giacJs().getText());
		Browser.webWorkerSupported = Browser.checkWorkerSupport(GWT.getModuleBaseURL());
		if (!Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.arrayBufferJs().getText());
		//strange, but iPad can blow it away again...
		if (Browser.checkIfFallbackSetExplicitlyInArrayBufferJs() && Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.dataViewJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.base64Js().getText());
	}

}
