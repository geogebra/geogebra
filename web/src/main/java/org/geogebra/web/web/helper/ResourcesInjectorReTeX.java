package org.geogebra.web.web.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.web.css.GuiResources;

public class ResourcesInjectorReTeX extends ResourcesInjector {
	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		jQueryNoConflict();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector.inject(GuiResources.INSTANCE.mowStyle());
		StyleInjector.inject(GuiResources.INSTANCE.spreadsheetStyle());
		StyleInjector.inject(GuiResources.INSTANCE.openScreenStyle());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.jqueryStyle());
		StyleInjector.inject(GuiResources.INSTANCE.fonts());
		loadWebFont();
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());

		StyleInjector.inject(GuiResources.INSTANCE.avStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.toolBarStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.menuStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.popupStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.dialogStylesScss());
		StyleInjector.inject(GuiResources.INSTANCE.settingsStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.perspectivesPopupScss());

		StyleInjector.inject(GuiResources.INSTANCE.snackbarScss());

	}

	private native void loadWebFont() /*-{
		$wnd.WebFontConfig = {
			custom : {
				families : [ "mathsans" ]
			}
		};
		$wnd.WebFont && $wnd.WebFont.load($wnd.WebFontConfig);
	}-*/;

	/**
	 * Runs JQ in noconflict mode; note that when running injectResourcesGUI
	 * twice jQuery is undefined on the second run
	 */
	private native void jQueryNoConflict() /*-{
		if ($wnd.jQuery && $wnd.jQuery.noConflict) {
			$wnd.$ggbQuery = $wnd.jQuery.noConflict(true);
		}
	}-*/;
}
