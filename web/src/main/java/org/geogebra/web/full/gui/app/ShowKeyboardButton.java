package org.geogebra.web.full.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.full.gui.GuiManagerW;
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

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnscreenTabbedKeyboard}
 */
public class ShowKeyboardButton extends SimplePanel {
	
	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param dm
	 *            {@link DockManagerW}
	 * @param app
	 *            app
	 */
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
			final DockManagerW dm, final AppWFull app) {
		this.addStyleName("matOpenKeyboardBtn");
		NoDragImage showKeyboard = new NoDragImage(KeyboardResources.INSTANCE
				.keyboard_show_material().getSafeUri().asString());
		this.add(showKeyboard);
		TestHarness.setAttr(this, "showKeyboardButton");

		if (listener instanceof ComplexPanel) {
			((ComplexPanel) listener).add(this);
		}
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
					listener.doShowKeyBoard(true,
							((AlgebraPanelInterface) panel)
									.updateKeyboardListener(
											mathKeyboardListener));
				} else {
					listener.doShowKeyBoard(true, mathKeyboardListener);
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
