package org.geogebra.web.web.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.NoDragImage;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.util.keyboard.OnScreenKeyBoard;
import org.geogebra.web.web.util.keyboard.UpdateKeyBoardListener;

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
	 * @param textField
	 *            {@link Widget}
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
				DockPanelW panel = dm.getFocusedPanel();
				if (panel == null
						|| (panel.getViewId() != App.VIEW_ALGEBRA && panel
								.getViewId() != App.VIEW_CAS)) {
					panel = dm.getPanel(App.VIEW_ALGEBRA);
					if (!panel.isVisible()) {
						panel = dm.getPanel(App.VIEW_CAS);
						if (!panel.isVisible()) {
							panel = dm.getPanel(App.VIEW_ALGEBRA);
						}
					}
				}
				final MathKeyboardListener mathKeyboardListener = panel
						.getKeyboardListener();
				listener.doShowKeyBoard(true, mathKeyboardListener);

				Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

					@Override
					public boolean execute() {
						if (mathKeyboardListener != null) {
							mathKeyboardListener.ensureEditing();
							mathKeyboardListener.setFocus(true);
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
