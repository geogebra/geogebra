package org.geogebra.web.full.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;

/**
 * Resource injector that includes UI styles.
 */
public class ResourcesInjectorReTeX extends ResourcesInjector {

	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());
		StyleInjector.inject(GuiResources.INSTANCE.mowStyle());
		StyleInjector.inject(GuiResources.INSTANCE.spreadsheetStyle());
		StyleInjector.inject(GuiResources.INSTANCE.openScreenStyle());
		StyleInjector.inject(GuiResources.INSTANCE.fonts());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());

		StyleInjector.inject(GuiResources.INSTANCE.modernStyle());
		StyleInjector.inject(GuiResources.INSTANCE.avStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.toolBarStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.sharedStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.menuStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.popupStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.dialogStylesScss());
		StyleInjector.inject(GuiResources.INSTANCE.settingsStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.perspectivesPopupScss());

		StyleInjector.inject(GuiResources.INSTANCE.snackbarScss());
		StyleInjector.inject(GuiResources.INSTANCE.texttoolStyle());
	}

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
}
