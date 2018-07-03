package org.geogebra.web.full.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Resource injector that includes UI styles.
 */
public class ResourcesInjectorReTeX extends ResourcesInjector {

	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());
		JavaScriptObject oldQuery = getOldJQuery();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		jQueryNoConflict(oldQuery);
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector.inject(GuiResources.INSTANCE.mowStyle());
		StyleInjector.inject(GuiResources.INSTANCE.spreadsheetStyle());
		StyleInjector.inject(GuiResources.INSTANCE.openScreenStyle());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.jqueryStyle());
		StyleInjector.inject(GuiResources.INSTANCE.fonts());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());

		StyleInjector.inject(GuiResources.INSTANCE.avStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.toolBarStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.stepTreeStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.sharedStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.menuStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.popupStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.dialogStylesScss());
		StyleInjector.inject(GuiResources.INSTANCE.settingsStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.perspectivesPopupScss());

		StyleInjector.inject(GuiResources.INSTANCE.snackbarScss());
	}

	private native JavaScriptObject getOldJQuery() /*-{
		var oldQuery = $wnd.jQuery;
		delete ($wnd.jQuery);
		return oldQuery;
	}-*/;

	@Override
	public native void loadWebFont(String fontUrl) /*-{
		$wnd.WebFontConfig = {
			custom : {
				families : [ "mathsans" ]
			}
		};
		if (fontUrl) {
			$wnd.WebFontConfig.custom.families = [ "mathsans",
					"geogebra-serif", "geogebra-sanserif" ];
			$wnd.WebFontConfig.custom.urls = [ fontUrl ];
		}
		$wnd.WebFont && $wnd.WebFont.load($wnd.WebFontConfig);
	}-*/;

	/**
	 * Runs JQ in noconflict mode; note that when running injectResourcesGUI
	 * twice jQuery is undefined on the second run
	 * 
	 * @param oldQuery
	 *            value of window.jQuery before we started loading
	 */
	private native void jQueryNoConflict(JavaScriptObject oldQuery) /*-{
		if ($wnd.jQuery && $wnd.jQuery.noConflict) {
			$wnd.$ggbQuery = $wnd.jQuery.noConflict(true);
		}
		$wnd.jQuery = oldQuery;
	}-*/;
}
