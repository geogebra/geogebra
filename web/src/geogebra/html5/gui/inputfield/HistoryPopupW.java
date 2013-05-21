package geogebra.html5.gui.inputfield;

import geogebra.common.main.GWTKeycodes;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HistoryPopupW extends PopupPanel implements ClickHandler, KeyUpHandler {

	private AutoCompleteTextFieldW textField;
	private boolean isDownPopup;
	private VerticalPanel historyList;
	private int highlighted = 0;

	public HistoryPopupW(AutoCompleteTextFieldW autoCompleteTextField) {
		 this.textField = autoCompleteTextField;
		 historyList = new VerticalPanel();
		 historyList.addStyleName("historyList");
		 historyList.addHandler(this, KeyUpEvent.getType());
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
			
			//With this constructor, there is no default href added to anchor,
			//in this way, there is no "Javascript:;" popup on the left bottom corner of Chrome.
	        Anchor a = new Anchor(false);	        
	        a.setText(link);
	        a.addClickHandler(this);
	        //sadly, it is not so nice, but I can't attach it to historyList :-(
	        a.addKeyUpHandler(this);
	        historyList.add(a);
        }
		show();
		setPopupPosition(textField.getAbsoluteLeft(), textField.getAbsoluteTop()-getOffsetHeight());
		int lastItem = historyList.getWidgetCount()-1;
		historyList.getWidget(lastItem).getElement().focus();
		highlighted = lastItem;
    }

	public boolean isDownPopup() {
		return isDownPopup;
    }

	public void onClick(ClickEvent event) {
	  Anchor target = (Anchor) event.getSource();
	  textField.setText(target.getText());
	  hide();
    }

	public void onKeyUp(KeyUpEvent event) {
	    int charCode = event.getNativeKeyCode();
	    if (historyList.getWidgetCount() == 0) {
	    	return;
	    }
	    for (int i = 0; i < historyList.getWidgetCount(); i++) {
	    	historyList.getWidget(i).removeStyleName("highlight");
	    }
	    Anchor a = null;
	    switch (charCode) {
		case GWTKeycodes.KEY_DOWN:
			highlighted++;
			if (highlighted >= historyList.getWidgetCount()) {
				highlighted = historyList.getWidgetCount() -1;
			}
			a = (Anchor) historyList.getWidget(highlighted);
			a.addStyleName("highlight");
			a.getElement().focus();
		break;
		case GWTKeycodes.KEY_UP:
			highlighted--;
			if (highlighted < 0) {
				highlighted = 0;
			}
			a = (Anchor) historyList.getWidget(highlighted);
			a.addStyleName("highlight");
			a.getElement().focus();
		break;
		case GWTKeycodes.KEY_ENTER: 
			a = (Anchor) historyList.getWidget(highlighted);
			 textField.setText(a.getText());
			 hide();
		break;
		case GWTKeycodes.KEY_ESCAPE:
			hide();
		break;
	    }
    }

}
