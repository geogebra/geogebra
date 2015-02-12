package geogebra.html5.gui.inputfield;

import geogebra.common.main.GWTKeycodes;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

public class HistoryPopupW extends PopupPanel implements ClickHandler,
        KeyUpHandler, ChangeHandler {

	private AutoCompleteW textField;
	private boolean isDownPopup;
	private ListBox historyList;
	private String originalTextEditorContent;

	public HistoryPopupW(AutoCompleteW autoCompleteTextField) {

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

	public void setDownPopup(boolean isDownPopup) {
		this.isDownPopup = isDownPopup;
	}

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
		textField.setText(historyList.getItemText(historyList
		        .getSelectedIndex()));

		historyList.setFocus(true);
	}

	public boolean isDownPopup() {
		return isDownPopup;
	}

	@Override
	public void onChange(ChangeEvent event) {
		textField.setText(historyList.getItemText(historyList
		        .getSelectedIndex()));
	}

	public void onKeyUp(KeyUpEvent event) {
		int charCode = event.getNativeKeyCode();
		switch (charCode) {
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

	public void onClick(ClickEvent event) {
		hide();
	}

}
