/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.helper;

import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.LinkElement;
import org.gwtproject.user.client.ui.RootPanel;

import com.google.gwt.core.client.GWT;

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
