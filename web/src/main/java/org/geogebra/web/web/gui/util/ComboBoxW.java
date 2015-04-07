package org.geogebra.web.web.gui.util;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW.InsertHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.web.gui.advanced.client.datamodel.ListModelEvent;
import org.geogebra.web.web.gui.advanced.client.ui.widget.ComboBox;
import org.geogebra.web.web.gui.advanced.client.ui.widget.combo.DropDownPosition;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	private static final int DEFAULT_VISIBLE_ROWS = 10;
	private static final String DEFAULT_WIDTH = "90px";
	private static final Image choiceImage = new Image(GuiResources.INSTANCE.toolbar_further_tools());
	private static final Image choiceImageActive = new Image(GuiResources.INSTANCE.toolbar_further_tools());

	public ComboBoxW(AppW app) {
		super(app);

		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		setWidth(DEFAULT_WIDTH);
		
		this.prepareChoiceButton();
		this.setChoiceButtonVisible(true);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			
			public void onClose(CloseEvent<PopupPanel> event) {
				onValueChange(getValue());
			}
		});

		addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onValueChange(getValue());
				}
	        }});
		//

		final AutoCompleteTextFieldW tf = getSelectedValue();
		tf.addStyleName("AutoCompleteTextFieldW");
		tf.addFocusListener(new FocusListenerW(this) {
			@Override
			public void onFocus(FocusEvent event) {
				super.onFocus(event);
			}

			@Override
			public void onBlur(BlurEvent event) {
				onValueChange(tf.getText());
				super.onBlur(event);
			}

		});

		tf.addKeyHandler(new KeyHandler() {

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					onValueChange(tf.getText());
				}
			}
		});


		tf.addInsertHandler(new InsertHandler() {

			public void onInsert(String text) {
				ComboBoxW.this.onValueChange(text);
			}
		});
	}

	@Override 
    protected void select(ListModelEvent event) {
		if (isListPanelOpened()) {
			return;
		}
		super.select(event);
	}
	
	protected abstract void onValueChange(String value);

	public void addItem(String item) {
		getModel().add(item, item);
	}
}