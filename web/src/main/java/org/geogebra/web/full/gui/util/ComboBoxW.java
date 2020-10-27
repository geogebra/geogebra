package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListModelEvent;
import org.geogebra.web.full.gui.advanced.client.ui.widget.ComboBox;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.DropDownPosition;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW.InsertHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

/**
 * Combo box
 */
public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	private static final int DEFAULT_VISIBLE_ROWS = 10;
	private static final String DEFAULT_WIDTH = "90px";

	/**
	 * @param app
	 *            application
	 */
	public ComboBoxW(AppW app) {
		super(app);

		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		setWidth(DEFAULT_WIDTH);
		
		this.prepareChoiceButton();
		this.setChoiceButtonVisible(true);
		
		addCloseHandler(new CloseHandler<GPopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				onValueChange(getValue());
			}
		});

		addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onValueChange(getValue());
				}
			}
		});
		//

		final AutoCompleteTextFieldW tf = getSelectedValue();
		tf.addStyleName("AutoCompleteTextFieldW");
		tf.enableGGBKeyboard();
		tf.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				onValueChange(tf.getText());
			}

		});

		tf.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					onValueChange(tf.getText());
				}
			}
		});

		tf.addInsertHandler(new InsertHandler() {

			@Override
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
	
	/**
	 * @param value
	 *            changed value
	 */
	protected abstract void onValueChange(String value);

	/**
	 * @param item
	 *            new item
	 */
	public void addItem(String item) {
		getModel().add(item, item);
	}

	/**
	 * @param id
	 *            item description
	 * @param item
	 *            item (used for color)
	 */
	public void addItem(String id, GeoElement item) {
		getModel().add(id, item);
	}
}