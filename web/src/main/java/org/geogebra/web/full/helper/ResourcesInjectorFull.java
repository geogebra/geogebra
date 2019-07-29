package org.geogebra.web.full.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MebisResources;
import org.geogebra.web.full.css.StylesProvider;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;

/**
 * Resource injector that includes UI styles.
 */
public class ResourcesInjectorFull extends ResourcesInjector {

	@Override
	protected void injectResourcesGUI(ArticleElementInterface ae) {
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());

		StylesProvider stylesProvider = createStylesProvider(ae);
		StyleInjector.inject(stylesProvider.mowStyle());
		StyleInjector.inject(stylesProvider.mowToolbarStyle());
		StyleInjector.inject(GuiResources.INSTANCE.spreadsheetStyle());
		StyleInjector.inject(stylesProvider.openScreenStyle());
		StyleInjector.inject(GuiResources.INSTANCE.fonts());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());

		StyleInjector.inject(GuiResources.INSTANCE.modernStyle());
		StyleInjector.inject(GuiResources.INSTANCE.avStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.toolBarStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.tableViewStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.menuStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.popupStyleScss());
		StyleInjector.inject(GuiResources.INSTANCE.componentStyles());
		StyleInjector.inject(stylesProvider.dialogStylesScss());
		StyleInjector.inject(GuiResources.INSTANCE.settingsStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.perspectivesPopupScss());

		StyleInjector.inject(GuiResources.INSTANCE.snackbarScss());
		StyleInjector.inject(GuiResources.INSTANCE.texttoolStyle());
		StyleInjector.inject(GuiResources.INSTANCE.scientificLayoutScss());
		StyleInjector.inject(GuiResources.INSTANCE.headerScss());

		injectLTRstyles();
		injectGreekFonts();
	}

	private StylesProvider createStylesProvider(ArticleElementInterface ae) {
		if ("mebis".equalsIgnoreCase(ae.getParamVendor())) {
			return MebisResources.INSTANCE;
		} else {
			return GuiResources.INSTANCE;
		}
	}

	/**
	 * Inject UI styles for LTR languages.
	 */
	public static void injectLTRstyles() {
		StyleInjector.inject(GuiResources.INSTANCE.generalStyleLTR());
		StyleInjector.inject(GuiResources.INSTANCE.avStyleLTR());
		StyleInjector.inject(GuiResources.INSTANCE.headerbarStyleLTR());
	}

	/**
	 * Inject UI styles for RTL languages.
	 */
	public static void injectRTLstyles() {
		StyleInjector.inject(GuiResources.INSTANCE.generalStyleRTL());
		StyleInjector.inject(GuiResources.INSTANCE.avStyleRTL());
		StyleInjector.inject(GuiResources.INSTANCE.headerbarStyleRTL());
	}

	public void injectGreekFonts() {
		StyleInjector.inject(KeyboardResources.INSTANCE.greekFonts());
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
