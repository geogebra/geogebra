package org.geogebra.web.full.helper;

import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Resource injector that includes UI styles.
 */
public class ResourcesInjectorFull extends ResourcesInjector {

	@Override
	protected void injectResourcesGUI(AppletParameters parameters) {
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS());

		new StyleInjector(GWT.getModuleBaseURL())
				.inject("css/bundles", "simple-bundle")
				.inject("css/bundles", "bundle")
				.inject("css", "keyboard-styles")
				.inject("css", "fonts")
				.inject("css", "greek-font");
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
