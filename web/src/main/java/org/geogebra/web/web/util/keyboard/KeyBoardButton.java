package org.geogebra.web.web.util.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.util.keyboardBase.KeyBoardButtonBase;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * A button of the {@link OnScreenKeyBoard}.
 */
public class KeyBoardButton extends KeyBoardButtonBase {

	/**
	 * @param caption
	 *            text of the button
	 * @param feedback
	 *            String to send if click occurs
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public KeyBoardButton(String caption, String feedback, OnScreenKeyBoard handler) {
		this(handler);
		this.label = new Label();
		setCaption(caption);
		this.feedback = feedback;

		this.setWidget(label);
	}

	/**
	 * Constructor for subclass {@link KeyBoardButtonFunctional}
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 */
	protected KeyBoardButton(final OnScreenKeyBoard handler) {
		super();

		//addDomHandler(handler, ClickEvent.getType());
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				ToolTipManagerW.hideAllToolTips();
				if(handler.getApp().getLAF().isSmart() && type == PointerEventType.TOUCH){
					return;
				}
				handler.onClick(KeyBoardButton.this);
			}

		});

		// only used for preventDefault and stopPropagation
		ClickEndHandler.init(this, new ClickEndHandler(true, true) {
			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// nothing to do here
			}
		});
		addStyleName("KeyBoardButton");
		addStyleName("MouseDownDoesntExitEditingFeature");
	}

}
