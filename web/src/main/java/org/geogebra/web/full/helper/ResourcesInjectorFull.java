package org.geogebra.web.full.helper;

import org.geogebra.common.util.StringUtil;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MebisResources;
import org.geogebra.web.full.css.StylesProvider;
import org.geogebra.web.full.css.TextStyles;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Resource injector that includes UI styles.
 */
public class ResourcesInjectorFull extends ResourcesInjector {

	@Override
	protected void injectResourcesGUI(AppletParameters ae) {
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
		StyleInjector.inject(stylesProvider.componentStyles());
		StyleInjector.inject(stylesProvider.dialogStylesScss());
		StyleInjector.inject(stylesProvider.settingsStyleScss());

		StyleInjector.inject(GuiResources.INSTANCE.perspectivesPopupScss());

		StyleInjector.inject(GuiResources.INSTANCE.snackbarScss());
		StyleInjector.inject(GuiResources.INSTANCE.scientificLayoutScss());
		StyleInjector.inject(GuiResources.INSTANCE.evaluatorScss());
		StyleInjector.inject(TextStyles.INSTANCE.textStyles());

		StyleInjector.inject(GuiResources.INSTANCE.generalStyle());
		StyleInjector.inject(GuiResources.INSTANCE.avStyle());
		StyleInjector.inject(GuiResources.INSTANCE.headerbarStyle());

		injectGreekFonts();
	}

	private StylesProvider createStylesProvider(AppletParameters ae) {
		if ("mebis".equalsIgnoreCase(ae.getParamVendor())) {
			return MebisResources.INSTANCE;
		} else {
			return GuiResources.INSTANCE;
		}
	}

	public void injectGreekFonts() {
		StyleInjector.inject(KeyboardResources.INSTANCE.greekFonts());
	}

	@Override
	public void loadWebFont(String fontUrl) {
		if (!StringUtil.empty(fontUrl)) {
			LinkElement link = Document.get().createLinkElement();
			link.setHref(fontUrl);
			link.setRel("stylesheet");
			RootPanel.getBodyElement().appendChild(link);
		}
	}
}
