package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.Panel;

import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Popup for history of inputs
 */
public class HistoryPopupW extends GPopupPanel implements ClickHandler,
        KeyUpHandler, ChangeHandler {

	private final AutoCompleteTextFieldW textField;
	private boolean downPopup;
	private final ListBox historyList;
	private String originalTextEditorContent;

	/**
	 * @param autoCompleteTextField
	 *            input field
	 * @param root
	 *            root for the popup
	 */
	public HistoryPopupW(AutoCompleteTextFieldW autoCompleteTextField, Panel root) {
		super(root, autoCompleteTextField.getApplication());
		this.textField = autoCompleteTextField;

		historyList = new ListBox();
		historyList.addChangeHandler(this);
		historyList.addKeyUpHandler(this);
		historyList.addClickHandler(this);
		historyList.addStyleName("historyList");

		add(historyList);
		addStyleName("GeoGebraPopup");
		setAutoHideEnabled(true);
	}

	/**
	 * @param isDownPopup
	 *            whether popup should be below the field
	 */
	public void setDownPopup(boolean isDownPopup) {
		this.downPopup = isDownPopup;
	}

	/**
	 * Show history popup
	 */
	public void showPopup() {
		ArrayList<String> list = textField.getHistory();
		if (list.isEmpty()) {
			return;
		}

		originalTextEditorContent = textField.getText();
		historyList.clear();
		historyList.setVisibleItemCount(Math.min(Math.max(list.size(), 2), 10));

		for (String link : list) {
			historyList.addItem(link);
		}

		show();
		setPopupPosition(textField.getAbsoluteLeft(),
				textField.getAbsoluteTop() - getOffsetHeight());

		historyList.setSelectedIndex(list.size() - 1);

		// focus one extra time in case the setText method would freeze
		// e.g. due to bad formula string
		historyList.setFocus(true);

		textField.setText(historyList.getItemText(historyList.getSelectedIndex()));

		historyList.setFocus(true);
	}

	/**
	 * @return whether popup is below the field
	 */
	public boolean isDownPopup() {
		return downPopup;
	}

	@Override
	public void onChange(ChangeEvent event) {
		textField.setText(historyList.getItemText(historyList.getSelectedIndex()));
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		int charCode = event.getNativeKeyCode();
		switch (charCode) {
		default:
			// do nothing
			break;
		case GWTKeycodes.KEY_ESCAPE:
			hide();
			textField.setText(originalTextEditorContent);
			textField.setFocus(true);
			break;
		case GWTKeycodes.KEY_ENTER:
			hide();
			textField.setFocus(true);
			break;
		}

	}

	@Override
	public void onClick(ClickEvent event) {
		hide();
	}

}
