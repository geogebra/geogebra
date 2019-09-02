package org.geogebra.web.full.gui.app;

import java.util.Date;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.keyboard.OnscreenTabbedKeyboard;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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

		if (listener instanceof ComplexPanel) {
			((ComplexPanel) listener).add(this);
		}
		ClickStartHandler.init(this, new ClickStartHandler(
				true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Cookies.setCookie("GeoGebraKeyboardWanted", "true",
						new Date(System.currentTimeMillis()
								+ 1000 * 60 * 60 * 24 * 365));
				DockPanelW panel = dm.getPanelForKeyboard();
				GuiManagerW guiManagerW = app.getGuiManager();
				final MathKeyboardListener mathKeyboardListener = guiManagerW
						.getKeyboardListener(panel);
						
				if (!app.isWhiteboardActive()
						&& (panel instanceof AlgebraPanelInterface)) {
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

				// TODO: check why scheduleFixedDelay is needed,
				// would not scheduleDeferred or something like that better?
				// but it's probably Okay, as the method returns false

				guiManagerW.focusScheduled(false, false, false);

				Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

					@Override
					public boolean execute() {
						if (mathKeyboardListener != null) {
							mathKeyboardListener.ensureEditing();
							mathKeyboardListener.setFocus(true, true);
						}
						return false;
					}
				}, 0);
			}
		});
	}

	/**
	 * 
	 * @param show
	 *            {@code true} to show the button to open the OnScreenKeyboard
	 * @param textField
	 *            {@link Widget} to receive the text input
	 */
	public void show(boolean show, MathKeyboardListener textField) {
		setVisible(show);
	}

	/**
	 * Hide the button
	 */
	public void hide() {
		setVisible(false);
	}
}
