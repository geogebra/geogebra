package org.geogebra.web.web.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.util.keyboard.OnScreenKeyBoard;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnScreenKeyBoard}
 */
public class ShowKeyboardButton extends SimplePanel {
	
	private Widget parent;

	// MathKeyboardListener mathKeyboardListener;

	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param dm
	 *            {@link DockManagerW}
	 * @param parent
	 *            {@link Element}
	 */
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
			final DockManagerW dm, Widget parent) {

		this.parent = parent;
		this.addStyleName("openKeyboardButton");
		NoDragImage showKeyboard = new NoDragImage(GuiResources.INSTANCE
		        .keyboard_show().getSafeUri().asString());
		this.add(showKeyboard);

		((DockPanelW) parent).addSouth(this);
		ClickStartHandler.init(ShowKeyboardButton.this, new ClickStartHandler(
		        true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				DockPanelW panel = dm.getPanelForKeyboard();
				final MathKeyboardListener mathKeyboardListener = panel
						.getKeyboardListener();
				listener.doShowKeyBoard(true, mathKeyboardListener);

				if ((listener.getAppW() == null)
						|| (listener.getAppW().getGuiManager() == null)) {
					// e.g. AppStub, Android device, do the old way
					Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

						@Override
						public boolean execute() {
							if (mathKeyboardListener != null) {
								mathKeyboardListener.ensureEditing();
								mathKeyboardListener.setFocus(true, false);
							}
							return false;
						}
					}, 0);
				} else {
					// TODO: check why scheduleFixedDelay is needed,
					// would not scheduleDeferred or something like that better?
					// but it's probably Okay, as the method returns false

					listener.getAppW().getGuiManager()
							.focusScheduled(false, false, false);

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

		if (show && parent.isVisible()) {
			setVisible(true);
		} else {
			setVisible(false);
		}

	}

	public void hide() {
		setVisible(false);
	}

}
