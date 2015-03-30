package geogebra.web.gui.app;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.main.App;
import geogebra.html5.gui.util.ClickStartHandler;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.util.keyboard.OnScreenKeyBoard;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnScreenKeyBoard}
 */
public class ShowKeyboardButton extends SimplePanel {
	
	private final int HEIGHT = 33;
	private Widget parent;

	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param textField
	 *            {@link Widget}
	 * @param parent
	 *            {@link Element}
	 */
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
	        final Widget textField, Widget parent) {

		this.parent = parent;
		this.addStyleName("openKeyboardButton");
		NoDragImage showKeyboard = new NoDragImage(GuiResources.INSTANCE
		        .keyboard_show().getSafeUri().asString());
		this.add(showKeyboard);

		((DockPanelW) parent).addSouth(this);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				App.debug("ADDING LISTENER"
				        + ShowKeyboardButton.this.getElement());
				ClickStartHandler.init(ShowKeyboardButton.this,
				        new ClickStartHandler(true, true) {

					        @Override
					        public void onClickStart(int x, int y,
					                PointerEventType type) {
						        App.debug("show keyboard");
						        listener.doShowKeyBoard(true, textField);
					        }

				        });

			}
		});



	}

	/**
	 * 
	 * @param show
	 *            {@code true} to show the button to open the OnScreenKeyboard
	 * @param textField
	 *            {@link Widget} to set as AutoHidePartner
	 */
	public void show(boolean show, Widget textField) {


		if (show && parent.isVisible()) {
			setVisible(true);
		} else {
			App.printStacktrace("");
			setVisible(false);
		}

	}

	public void hide() {
		setVisible(false);
	}

}
