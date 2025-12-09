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

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class LogoAndName implements IsWidget, SetLabels {

	private static final int LOGO_MARGIN = 72; // 24px top + 48px bottom
	private final Widget panel;
	private final Label name;
	private final App app;

	/**
	 * @param app application
	 */
	public LogoAndName(App app) {
		this.app = app;
		name = new Label();
		NoDragImage icon = new NoDragImage(((AppWFull) app).getActivity().getIcon(),
				24);
		AriaHelper.setAlt(icon, "");
		panel = LayoutUtilW.panelRow(icon, name);
		panel.addStyleName("avNameLogo");
		setLabels();
	}

	@Override
	public void setLabels() {
		name.setText(app.getLocalization().getMenu(
				app.getConfig().getAppTransKey()));
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	/**
	 * @param aView algebra view
	 * @param parentHeight parent panel height in pixels
	 */
	public void onResize(AlgebraViewW aView, int parentHeight) {
		AppW app = aView.getApp();
		boolean showLogo = !app.getAppletFrame().isKeyboardShowing();
		panel.setVisible(showLogo);
		if (showLogo) {
			placeLogoToBottom(aView, parentHeight);
		} else {
			removeLogoFromBottom(aView);
		}
	}

	private void removeLogoFromBottom(AlgebraViewW aView) {
		aView.getElement().getStyle().clearProperty("minHeight");
	}

	private void placeLogoToBottom(AlgebraViewW aView, int parentHeight) {
		int minHeight = parentHeight - panel.getOffsetHeight() - LOGO_MARGIN;
		aView.getElement().getStyle().setProperty("minHeight", minHeight + "px");
	}
}
