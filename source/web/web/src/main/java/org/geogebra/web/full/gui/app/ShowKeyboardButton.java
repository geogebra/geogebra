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

package org.geogebra.web.full.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.keyboard.OnscreenTabbedKeyboard;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnscreenTabbedKeyboard}
 */
public class ShowKeyboardButton extends SimplePanel {
	
	/**
	 * @param listener
	 *            {@link KeyboardCloseListener}
	 * @param dm
	 *            {@link DockManagerW}
	 * @param app
	 *            app
	 */
	public ShowKeyboardButton(final GeoGebraFrameFull listener,
			final DockManagerW dm, final AppWFull app) {
		this.addStyleName("matOpenKeyboardBtn");
		if (app.isApplet() || app.getConfig().getKeyboardType() == AppKeyboardType.SCIENTIFIC) {
			addStyleName("cornerPosition");
		}
		NoDragImage showKeyboard = new NoDragImage(KeyboardResources.INSTANCE
				.keyboard_show_material().getSafeUri().asString());
		this.add(showKeyboard);
		TestHarness.setAttr(this, "showKeyboardButton");

		listener.add(this);

		ClickStartHandler.init(this, new ClickStartHandler(
				true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				BrowserStorage.LOCAL.setItem(BrowserStorage.KEYBOARD_WANTED, "true");
				DockPanelW panel = dm.getPanelForKeyboard();
				GuiManagerW guiManagerW = app.getGuiManager();
				final MathKeyboardListener mathKeyboardListener = guiManagerW
						.getKeyboardListener(panel);
						
				if (panel instanceof AlgebraPanelInterface) {
					listener.doShowKeyboard(true,
							((AlgebraPanelInterface) panel)
									.updateKeyboardListener(
											mathKeyboardListener));
				} else {
					listener.doShowKeyboard(true, mathKeyboardListener);
				}

				if (guiManagerW.hasSpreadsheetView()) {
					guiManagerW.getSpreadsheetView().setKeyboardEnabled(true);
				}

				if (mathKeyboardListener != null) {
					mathKeyboardListener.ensureEditing();
					mathKeyboardListener.setFocus(true);
				}
			}
		});
	}
}
