package geogebra.web.gui.autocompletion;

import java.util.List;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.inputfield.AutoCompleteTextField;

public class CompletionsPopup extends MultiWordSuggestOracle {

	private AutoCompleteTextField textField;
	private VerticalPanel list;
	

	public CompletionsPopup(AutoCompleteTextField textField,
            CommandCompletionListCellRenderer cellRenderer, int i) {
		super();
		clear();
		this.textField = textField;
    }

	private void registerListeners() {
	    // TODO Auto-generated method stub
	    
    }

	public void showCompletions() {
		if (!textField.getAutoComplete()) {
			return;
		}
		List<String> completions = textField.getCompletions();
		if (completions != null) {
			clear();
			addAll(completions);
		}
    }

}
