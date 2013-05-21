package geogebra.html5.gui.view.autocompletion;

import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompletionsPopup extends MultiWordSuggestOracle {

	private AutoCompleteTextFieldW textField;
	private VerticalPanel list;

	public CompletionsPopup() {
	   super();
	   clear();
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

	public void addTextField(AutoCompleteTextFieldW autoCompleteTextField) {
	  this.textField = autoCompleteTextField;
    }

	public void showHistoryCompletions(ArrayList<String> history) {
	   if (history != null) {
		   clear();
		   addAll(history);
	   }
    }

}
