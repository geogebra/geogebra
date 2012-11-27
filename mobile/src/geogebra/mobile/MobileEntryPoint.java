package geogebra.mobile;

import geogebra.mobile.gui.GuiResources;
import geogebra.mobile.gui.TabletGUI;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.StyleInjector;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{
	MobileApp app;

	@Override
	public void onModuleLoad()
	{
		this.app = new MobileApp(new TabletGUI());

		// this.app = new MobileApp(new IconTestGUI());
		loadMobileAsync();

		// insert mathquill css
		String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText();
		// As of GWT 2.4, GWT.getModuleBaseURL is used,
		// GWT.getModuleBaseForStaticFiles may be used afterwards, maybe not.
		// String.replace(CharSequence, CharSequence) should replace all in
		// theory
		
		//FIXME does this do anything? the resulting string is ignored.
		mathquillcss.replace("url(mobile/font/Symbola",
				"url(" + GWT.getModuleBaseURL() + "font/Symbola");
		mathquillcss.replace("url(web/font/Symbola",
				"url(" + GWT.getModuleBaseURL() + "font/Symbola");
		StyleInjector.inject(mathquillcss);
		
		
		//insert zip.js - for saving - not needed yet, because now we use xml-Strings
//		String deflateJs = GuiResources.INSTANCE.deflateJs().getText();
//		deflateJs.replace("url(mobile/js/zipjs/deflate", "url(" + GWT.getModuleBaseURL() + "js/zipjs/deflate");
//		deflateJs.replace("url(web/js/zipjs/deflate", "url(" + GWT.getModuleBaseURL() + "js/zipjs/deflate");
//		JavaScriptInjector.inject(deflateJs);
//		
//		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs().getText());
//		JavaScriptInjector.inject(GuiResources.INSTANCE.downloadggbJs().getText());
//		//JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
//		JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
//		JavaScriptInjector.inject(GuiResources.INSTANCE.arrayBufferJs().getText());
//		JavaScriptInjector.inject(GuiResources.INSTANCE.dataViewJs().getText());
//		JavaScriptInjector.inject(GuiResources.INSTANCE.base64Js().getText());
		
		
	}

	private void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			@Override
			public void onSuccess()
			{
				MobileEntryPoint.this.app.start();
			}

			@Override
			public void onFailure(Throwable reason)
			{
				// App.debug(reason);
				reason.printStackTrace();
			}
		});
	}
}