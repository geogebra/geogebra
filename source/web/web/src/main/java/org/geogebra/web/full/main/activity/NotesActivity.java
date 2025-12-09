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

package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

/**
 * Activity class for the notes app
 */
public class NotesActivity extends BaseActivity {

	/**
	 * New notes activity
	 */
	public NotesActivity() {
		super(new AppConfigNotes());
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null, true);
	}

	@Override
	public void start(AppW appW) {
		super.start(appW);
		if (NavigatorUtil.isIE()) {
			showBrowserNotSupportedMessage(appW);
		}
	}

	private void showBrowserNotSupportedMessage(AppW app) {
		VendorSettings vendorSettings = app.getVendorSettings();
		InfoErrorData data = new InfoErrorData("UnsupportedBrowser",
				vendorSettings.getMenuLocalizationKey("UnsupportedBrowser.Message"));
		ComponentInfoErrorPanel browserNotSupported =
				new ComponentInfoErrorPanel(app.getLocalization(), data);
		browserNotSupported.addStyleName("browserNotSupported");

		GeoGebraFrameW frame = app.getAppletFrame();
		frame.clear();
		frame.add(browserNotSupported);
		frame.forceHeaderHidden(true);
	}

	@Override
	public boolean isWhiteboard() {
		return true;
	}
}
