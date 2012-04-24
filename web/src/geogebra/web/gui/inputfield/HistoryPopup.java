package geogebra.web.gui.inputfield;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import geogebra.common.main.AbstractApplication;

public class HistoryPopup extends PopupPanel implements ClickHandler {

	private AutoCompleteTextField textField;
	private boolean isDownPopup;
	private VerticalPanel historyList;

	public HistoryPopup(AutoCompleteTextField autoCompleteTextField) {
		 this.textField = autoCompleteTextField;
		 historyList = new VerticalPanel();
		 historyList.addStyleName("historyList");
		 add(historyList);
    }

	public void setDownPopup(boolean isDownPopup) {
		this.isDownPopup = isDownPopup;
    }

	public void showPopup() {
		ArrayList<String> list = textField.getHistory();
		if (list.isEmpty()) {
			return;
		}
		historyList.clear();
		for (String link : list) {
	        Anchor a = new Anchor(link);
	        a.addClickHandler(this);
	        historyList.add(a);
        }
		setPopupPosition(textField.getAbsoluteLeft(), textField.getAbsoluteTop()-historyList.getOffsetHeight());
		show();
		
    }

	public boolean isDownPopup() {
		return isDownPopup;
    }

	public void onClick(ClickEvent event) {
	  Anchor target = (Anchor) event.getSource();
	  textField.setText(target.getText());
	  hide();
    }

}
